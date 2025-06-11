package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mivlgu.ReservationSystem.Entities.*;
import ru.mivlgu.ReservationSystem.Enums.EventStatus;
import ru.mivlgu.ReservationSystem.Repositories.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class CreateEventController {

    private final EventRepository eventRepository;
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final EventTemplateRepository eventTemplateRepository;
    private final EquipmentRepository equipmentRepository;
    private final EventEquipmentRequirementRepository eventEquipmentRequirementRepository; // Добавляем репозиторий для EventEquipmentRequirement

    @Autowired
    public CreateEventController(EventRepository eventRepository, ClassroomRepository classroomRepository,
                                 UserRepository userRepository, EventTemplateRepository eventTemplateRepository,
                                 EquipmentRepository equipmentRepository, EventEquipmentRequirementRepository eventEquipmentRequirementRepository,
                                 ScheduleRepository scheduleRepository) {
        this.eventRepository = eventRepository;
        this.classroomRepository = classroomRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.eventTemplateRepository = eventTemplateRepository;
        this.equipmentRepository = equipmentRepository;
        this.eventEquipmentRequirementRepository = eventEquipmentRequirementRepository; // Инициализируем репозиторий
    }


    // Страница создания мероприятия
    @GetMapping("/create-event")
    public String createEventPage(Model model) {
        model.addAttribute("classrooms", classroomRepository.findAll());
        model.addAttribute("eventTemplates", eventTemplateRepository.findAll());
        model.addAttribute("equipments", equipmentRepository.findAll());
        return "create-event";
    }

    // Обработка создания мероприятия
    @PostMapping("/create-event")
    public String createEvent(@RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @RequestParam("classroomId") Long classroomId,
                              @RequestParam("startDateTime") String startDateTime,
                              @RequestParam("endDateTime") String endDateTime,
                              @RequestParam(value = "equipmentIds", required = false) List<Long> equipmentIds) {

        // Получаем текущего аутентифицированного пользователя
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        // Получаем вашего User
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Преобразуем startDateTime и endDateTime в LocalDateTime
        LocalDateTime start = LocalDateTime.parse(startDateTime);
        LocalDateTime end = LocalDateTime.parse(endDateTime);

        // Проверка на корректность дат
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Дата и время окончания мероприятия не могут быть раньше даты начала.");
        }

        // Создание шаблона мероприятия
        EventTemplate eventTemplate = new EventTemplate();
        eventTemplate.setName(title);
        eventTemplate.setDescription(description);
        eventTemplate.setDefaultDuration(java.time.Duration.between(start, end).toMinutes());

        // Сохраняем шаблон
        eventTemplate = eventTemplateRepository.save(eventTemplate);

        // Создаем мероприятие
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setStartDateTime(start);
        event.setEndDateTime(end);
        event.setClassroom(classroomRepository.findById(classroomId).orElse(null));
        event.setCreator(currentUser);
        event.setEventTemplate(eventTemplate);

        // Сохраняем мероприятие
        event = eventRepository.save(event); // теперь eventId доступен

        // Сохранение информации о занятом времени в таблице расписания
        Schedule schedule = new Schedule();
        schedule.setClassroom(classroomRepository.findById(classroomId).orElse(null));
        schedule.setEvent(event);
        schedule.setStartDateTime(start);
        schedule.setEndDateTime(end);

        // Сохраняем расписание
        scheduleRepository.save(schedule);

        // Добавляем оборудование
        if (equipmentIds != null && !equipmentIds.isEmpty()) {
            for (Long equipmentId : equipmentIds) {
                Equipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
                if (equipment != null) {
                    EventEquipmentRequirement requirement = new EventEquipmentRequirement();
                    EventEquipmentRequirementId id = new EventEquipmentRequirementId();
                    id.setEventId(event.getEventId());
                    id.setEquipmentId(equipment.getEquipmentId());
                    requirement.setId(id);
                    requirement.setEquipment(equipment);
                    requirement.setQuantity(1);
                    requirement.setEvent(event);
                    eventEquipmentRequirementRepository.save(requirement);
                } else {
                    throw new IllegalArgumentException("Оборудование с ID " + equipmentId + " не найдено.");
                }
            }
        }

        return "redirect:/events";
    }

    @GetMapping("/create_event")
    public String showCreateForm(
            @RequestParam(value = "minCapacity", required = false, defaultValue = "1") Integer minCapacity,
            @RequestParam(value = "equipmentIds", required = false) List<Long> equipmentIds,
            Model model) {

        // Фильтрация аудиторий
        List<Classroom> filteredClassrooms = classroomRepository.findByCapacityGreaterThanEqual(minCapacity)
                .stream()
                .filter(c -> equipmentIds == null || c.getEquipmentList().stream()
                        .map(ce -> ce.getEquipment().getEquipmentId())
                        .collect(Collectors.toSet())
                        .containsAll(equipmentIds))
                .collect(Collectors.toList());

        model.addAttribute("classrooms", filteredClassrooms);

        model.addAttribute("classrooms", filteredClassrooms);
        model.addAttribute("allEquipment", equipmentRepository.findAll());
        model.addAttribute("timePairs", Map.of(
                1, "8:30 - 10:00",
                2, "10:15 - 11:45",
                3, "12:30 - 14:00",
                4, "14:15 - 15:45",
                5, "16:00 - 17:30",
                6, "17:45 - 19:15",
                7, "19:30 - 21:00"
        ));
        return "create_event";
    }

    @PostMapping("/create_event")
    public String createEvent(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("classroomId") Long classroomId,
            @RequestParam("eventDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate,
            @RequestParam("selectedPair") int selectedPair,
            @RequestParam(value = "equipmentIds", required = false) List<Long> equipmentIds,
            RedirectAttributes redirectAttributes) {

        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User creator = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            PairTime pairTime = getPairTime(selectedPair);
            LocalDateTime startDateTime = LocalDateTime.of(eventDate, pairTime.start());
            LocalDateTime endDateTime = LocalDateTime.of(eventDate, pairTime.end());

            Classroom classroom = classroomRepository.findById(classroomId)
                    .orElseThrow(() -> new IllegalArgumentException("Аудитория не найдена"));

            //checkTimeSlotAvailability(classroom, startDateTime, endDateTime);

            if (equipmentIds != null && !equipmentIds.isEmpty()) {
                validateEquipmentAvailability(classroom, equipmentIds);
            }

            Event event = new Event();
            event.setTitle(title);
            event.setDescription(description);
            event.setStartDateTime(startDateTime);
            event.setEndDateTime(endDateTime);
            event.setStatus(EventStatus.PENDING);
            event.setCreator(creator);
            event.setClassroom(classroom);

            if (equipmentIds != null) {
                addEquipmentRequirements(event, equipmentIds);
            }

            eventRepository.save(event);
            createScheduleEntry(event, classroom, startDateTime, endDateTime);

            redirectAttributes.addFlashAttribute("success", "Мероприятие успешно создано!");
            return "redirect:/events";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/create_event";
        }
    }
/*
    private void checkTimeSlotAvailability(Classroom classroom, LocalDateTime start, LocalDateTime end) {
        List<Schedule> conflicts = scheduleRepository.findConflictingSchedules(
                classroom.getClassroomId(), start, end);
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Аудитория занята в выбранное время");
        }
    }
*/
    private void validateEquipmentAvailability(Classroom classroom, List<Long> requiredEquipmentIds) {
        Set<Long> availableEquipmentIds = classroom.getEquipmentList().stream()
                .map(ce -> ce.getEquipment().getEquipmentId())
                .collect(Collectors.toSet());

        List<Long> missingEquipment = requiredEquipmentIds.stream()
                .filter(id -> !availableEquipmentIds.contains(id))
                .toList();

        if (!missingEquipment.isEmpty()) {
            String missingNames = equipmentRepository.findAllById(missingEquipment).stream()
                    .map(Equipment::getName)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Отсутствует оборудование: " + missingNames);
        }
    }

    private void addEquipmentRequirements(Event event, List<Long> equipmentIds) {
        equipmentIds.forEach(equipmentId -> {
            Equipment equipment = equipmentRepository.findById(equipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Оборудование не найдено"));

            EventEquipmentRequirement requirement = new EventEquipmentRequirement();
            requirement.setEvent(event);
            requirement.setEquipment(equipment);
            requirement.setQuantity(1); // Или получать количество из формы

            event.getEquipmentRequirements().add(requirement);
        });
    }

    private void createScheduleEntry(Event event, Classroom classroom,
                                     LocalDateTime start, LocalDateTime end) {
        Schedule schedule = new Schedule();
        schedule.setEvent(event);
        schedule.setClassroom(classroom);
        schedule.setStartDateTime(start);
        schedule.setEndDateTime(end);
        scheduleRepository.save(schedule);
    }

    record PairTime(LocalTime start, LocalTime end) {}

    private PairTime getPairTime(int pairNumber) {
        return switch (pairNumber) {
            case 1 -> new PairTime(LocalTime.of(8, 30), LocalTime.of(10, 0));
            case 2 -> new PairTime(LocalTime.of(10, 15), LocalTime.of(11, 45));
            case 3 -> new PairTime(LocalTime.of(12, 30), LocalTime.of(14, 0));
            case 4 -> new PairTime(LocalTime.of(14, 15), LocalTime.of(15, 45));
            case 5 -> new PairTime(LocalTime.of(16, 0), LocalTime.of(17, 30));
            case 6 -> new PairTime(LocalTime.of(17, 45), LocalTime.of(19, 15));
            case 7 -> new PairTime(LocalTime.of(19, 30), LocalTime.of(21, 0));
            default -> throw new IllegalArgumentException("Неверный номер пары");
        };
    }

    // Обработка исключений
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleInvalidDateTimeException(IllegalArgumentException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "create-event";
    }
}
