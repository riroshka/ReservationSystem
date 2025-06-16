package ru.mivlgu.ReservationSystem.Services;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mivlgu.ReservationSystem.Entities.*;
import ru.mivlgu.ReservationSystem.Enums.EventStatus;
import ru.mivlgu.ReservationSystem.Enums.RegistrationStatus;
import ru.mivlgu.ReservationSystem.Repositories.*;
import ru.mivlgu.ReservationSystem.dto.ReportData;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final EventRepository eventRepository;
    private final ClassroomRepository classroomRepository;
    private final EquipmentRepository equipmentRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final UserRepository userRepository;
    private final EventEquipmentRequirementRepository eventEquipmentRequirementRepository;

    @Autowired
    public ReportService(EventRepository eventRepository,
                         ClassroomRepository classroomRepository,
                         EquipmentRepository equipmentRepository,
                         UserRepository userRepository,
                         EventRegistrationRepository eventRegistrationRepository,
                         EventEquipmentRequirementRepository eventEquipmentRequirementRepository) {
        this.eventRepository = eventRepository;
        this.classroomRepository = classroomRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.eventEquipmentRequirementRepository = eventEquipmentRequirementRepository;
    }

    public ReportData generateReport(String reportType, LocalDate startDate, LocalDate endDate) {
        return switch (reportType) {
            case "popularity" -> generatePopularityReport(startDate, endDate);
            case "utilization" -> generateUtilizationReport(startDate, endDate);
            case "equipment" -> generateEquipmentReport(startDate, endDate);
            case "registrations" -> generateRegistrationsReport(startDate, endDate);
            default -> throw new IllegalArgumentException("Неизвестный тип отчета: " + reportType);
        };
    }

    public ReportData generatePopularityReport(LocalDate startDate, LocalDate endDate) {
        List<String> headers = List.of("Мероприятие", "Дата проведения", "Аудитория", "Количество регистраций");

        List<Event> allEvents = eventRepository.findByStatus(EventStatus.APPROVED);

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> isEventInPeriod(event, startDate, endDate))
                .sorted(Comparator.comparing(Event::getStartDateTime).reversed())
                .collect(Collectors.toList());

        // Формируем строки отчета
        List<List<String>> rows = filteredEvents.stream().map(event -> {
            int registrationsCount = eventRegistrationRepository.countByEventAndStatus(
                    event,
                    RegistrationStatus.CONFIRMED
            );
            return List.of(
                    event.getTitle(),
                    event.getStartDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    event.getClassroom().getName(),
                    String.valueOf(registrationsCount)
            );
        }).collect(Collectors.toList());

        return new ReportData(headers, rows);
    }

    private boolean isEventInPeriod(Event event, LocalDate startDate, LocalDate endDate) {
        LocalDateTime eventStart = event.getStartDateTime();

        // Если обе даты не указаны - включаем все мероприятия
        if (startDate == null && endDate == null) {
            return true;
        }

        LocalDateTime rangeStart = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime rangeEnd = endDate != null ? endDate.atTime(23, 59, 59) : null;

        boolean afterStart = (rangeStart == null) ||
                !eventStart.isBefore(rangeStart);

        boolean beforeEnd = (rangeEnd == null) ||
                !eventStart.isAfter(rangeEnd);

        return afterStart && beforeEnd;
    }

    private ReportData generateUtilizationReport(LocalDate startDate, LocalDate endDate) {
        List<String> headers = List.of("Аудитория", "Вместимость", "Количество мероприятий", "Занято часов");
        List<Classroom> classrooms = classroomRepository.findAll();

        List<List<String>> rows = classrooms.stream().map(classroom -> {
            List<Schedule> schedules = classroom.getSchedules().stream()
                    .filter(schedule -> isWithinPeriod(schedule, startDate, endDate))
                    .collect(Collectors.toList());

            long totalHours = schedules.stream()
                    .mapToLong(schedule ->
                            Duration.between(schedule.getStartDateTime(), schedule.getEndDateTime()).toHours()
                    )
                    .sum();

            return List.of(
                    classroom.getName(),
                    String.valueOf(classroom.getCapacity()),
                    String.valueOf(schedules.size()),
                    String.valueOf(totalHours)
            );
        }).collect(Collectors.toList());

        return new ReportData(headers, rows);
    }

    private boolean isWithinPeriod(Schedule schedule, LocalDate start, LocalDate end) {
        LocalDateTime eventStart = schedule.getStartDateTime();
        return (start == null || eventStart.toLocalDate().isAfter(start.minusDays(1))) &&
                (end == null || eventStart.toLocalDate().isBefore(end.plusDays(1)));
    }

    private ReportData generateEquipmentReport(LocalDate startDate, LocalDate endDate) {
        List<String> headers = List.of("Оборудование", "Количество использований");

        List<Event> allEvents = eventRepository.findByStatus(EventStatus.APPROVED);

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> isEventInPeriod(event, startDate, endDate))
                .collect(Collectors.toList());

        Set<Long> eventIds = filteredEvents.stream()
                .map(Event::getEventId)
                .collect(Collectors.toSet());

        List<EventEquipmentRequirement> requirements =
                eventEquipmentRequirementRepository.findByEvent_EventIdIn(eventIds);

        Map<Equipment, Long> equipmentUsage = requirements.stream()
                .collect(Collectors.groupingBy(
                        EventEquipmentRequirement::getEquipment,
                        Collectors.summingLong(EventEquipmentRequirement::getQuantity)
                ));

        List<List<String>> rows = equipmentRepository.findAll().stream()
                .map(equipment -> List.of(
                        equipment.getName(),
                        String.valueOf(equipmentUsage.getOrDefault(equipment, 0L))
                ))
                .collect(Collectors.toList());

        return new ReportData(headers, rows);
    }

    private ReportData generateRegistrationsReport(LocalDate startDate, LocalDate endDate) {
        List<String> headers = List.of("Пользователь", "Количество регистраций");

        List<Event> allEvents = eventRepository.findByStatus(EventStatus.APPROVED);

        List<Event> filteredEvents = allEvents.stream()
                .filter(event -> isEventInPeriod(event, startDate, endDate))
                .collect(Collectors.toList());

        Set<Long> eventIds = filteredEvents.stream()
                .map(Event::getEventId)
                .collect(Collectors.toSet());

        List<EventRegistration> registrations =
                eventRegistrationRepository.findByEvent_EventIdInAndStatus(
                        eventIds, RegistrationStatus.CONFIRMED
                );

        Map<User, Long> registrationsPerUser = registrations.stream()
                .collect(Collectors.groupingBy(
                        EventRegistration::getUser,
                        Collectors.counting()
                ));

        List<List<String>> rows = registrationsPerUser.entrySet().stream()
                .map(entry -> List.of(
                        entry.getKey().getFullName(),
                        String.valueOf(entry.getValue())
                ))
                .collect(Collectors.toList());

        return new ReportData(headers, rows);
    }
}
