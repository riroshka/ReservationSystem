package ru.mivlgu.ReservationSystem.Services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mivlgu.ReservationSystem.Entities.*;
        import ru.mivlgu.ReservationSystem.Enums.EventStatus;
import ru.mivlgu.ReservationSystem.Enums.RegistrationStatus;
import ru.mivlgu.ReservationSystem.Repositories.*;
import ru.mivlgu.ReservationSystem.dto.EventCardDto;
import ru.mivlgu.ReservationSystem.dto.EventDto;
import ru.mivlgu.ReservationSystem.dto.TimeSlot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventService {
    private final EventRepository eventRepository;
    private final ScheduleRepository scheduleRepository;
    private final EquipmentRepository equipmentRepository;
    private final EventEquipmentRequirementRepository requirementRepository;
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final EventCommentRepository eventCommentRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final EmailService emailService;

    public EventService(EventRepository eventRepository, ScheduleRepository scheduleRepository, EquipmentRepository equipmentRepository, EventEquipmentRequirementRepository requirementRepository, ClassroomRepository classroomRepository, UserRepository userRepository, EventCommentRepository eventCommentRepository, EventRegistrationRepository eventRegistrationRepository, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.scheduleRepository = scheduleRepository;
        this.equipmentRepository = equipmentRepository;
        this.requirementRepository = requirementRepository;
        this.classroomRepository = classroomRepository;
        this.userRepository = userRepository;
        this.eventCommentRepository = eventCommentRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.emailService = emailService;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(EventDto eventDto, String email, byte[] photo) {
        // Получаем полный объект пользователя из базы
        System.out.println("Creating event for user: " + email);
        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        System.out.println("Found user: " + creator.getUserId() + " " + creator.getEmail());

        Classroom classroom = classroomRepository.findById(eventDto.getClassroomId())
                .orElseThrow(() -> new EntityNotFoundException("Аудитория не найдена"));

        // Создаем мероприятие
        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setStartDateTime(LocalDateTime.of(eventDto.getDate(), eventDto.getStartTime()));
        event.setEndDateTime(LocalDateTime.of(eventDto.getDate(), eventDto.getEndTime()));
        event.setStatus(EventStatus.PENDING);
        event.setCreator(creator);
        event.setClassroom(classroom);
        event.setPhoto(photo);
        event = eventRepository.save(event);

        // Добавляем в расписание
        Schedule schedule = new Schedule();
        schedule.setClassroom(classroom);
        schedule.setEvent(event);
        schedule.setStartDateTime(event.getStartDateTime());
        schedule.setEndDateTime(event.getEndDateTime());
        scheduleRepository.save(schedule);

        // Добавляем оборудование
        if (eventDto.getEquipmentIds() != null) {
            for (Long equipmentId : eventDto.getEquipmentIds()) {
                Equipment equipment = equipmentRepository.findById(equipmentId)
                        .orElseThrow(() -> new EntityNotFoundException("Оборудование не найдено"));

                EventEquipmentRequirement requirement = new EventEquipmentRequirement();
                requirement.setEvent(event);
                requirement.setEquipment(equipment);
                requirement.setQuantity(1);

                EventEquipmentRequirementId id = new EventEquipmentRequirementId(
                        event.getEventId(),
                        equipment.getEquipmentId()
                );
                requirement.setId(id);
                requirementRepository.save(requirement);
            }
        }
        // Сохраняем комментарий, если он есть
        if (eventDto.getComment() != null && !eventDto.getComment().isBlank()) {
            EventComment comment = new EventComment();
            comment.setEvent(event);
            comment.setUser(creator);
            comment.setCommentText(eventDto.getComment());
            eventCommentRepository.save(comment);
        }

        return event;
    }
    /*
    public List<EventCardDto> getApprovedEventsForCards() {
        List<Event> events = eventRepository.findByStatus(EventStatus.APPROVED);
        return events.stream()
                .map(EventCardDto::new)
                .collect(Collectors.toList());
    }
*/
    public List<Event> getApprovedEventsForCards() {
        return eventRepository.findByStatus(EventStatus.APPROVED);
    }

    // Регистрация на мероприятие
    public boolean registerForEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено"));

        // Проверка роли пользователя
        if (user.getRole().name().equalsIgnoreCase("guest")) {
            throw new IllegalStateException("Только студенты и преподаватели могут регистрироваться на мероприятия");
        }

        // Проверка существующей регистрации
        if (eventRegistrationRepository.existsByEvent_EventIdAndUser_UserId(eventId, user.getUserId())) {
            throw new IllegalStateException("Вы уже зарегистрированы на это мероприятие");
        }

        // Проверка вместимости аудитории
        int registeredCount = eventRegistrationRepository.countByEvent_EventId(eventId);
        if (registeredCount >= event.getClassroom().getCapacity()) {
            throw new IllegalStateException("Все места заняты");
        }

        // Создание регистрации
        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus(RegistrationStatus.CONFIRMED);
        registration.setStudent(true);

        // Генерация QR-кода
        String qrContent = String.format("%s;%s;%d;%s;%s",
                user.getFullName(),
                user.getStudentGroup(),
                registration.getEvent().getEventId(),
                registration.getEvent().getTitle(),
                registration.getEvent().getStartDateTime().toString());

        registration.setQrCode(generateQRCodeBase64(qrContent, 200, 200));

        eventRegistrationRepository.save(registration);
        return true;
    }

    // Отмена регистрации
    public boolean cancelRegistration(Long eventId, Long userId) {
        EventRegistration registration = eventRegistrationRepository
                .findByEvent_EventIdAndUser_UserId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Регистрация не найдена"));

        eventRegistrationRepository.delete(registration);
        return true;
    }

    // Проверка регистрации пользователя
    public boolean isUserRegistered(Long eventId, Long userId) {
        return eventRegistrationRepository.existsByEvent_EventIdAndUser_UserId(eventId, userId);
    }

    public List<TimeSlot> getBusySlotsForClassroom(Long classroomId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Schedule> schedules = scheduleRepository.findByClassroom_ClassroomIdAndStartDateTimeBetween(
                classroomId, startOfDay, endOfDay
        );

        return schedules.stream()
                .map(schedule -> new TimeSlot(
                        schedule.getStartDateTime().toLocalTime(),
                        schedule.getEndDateTime().toLocalTime()
                ))
                .collect(Collectors.toList());
    }

    // Отмена мероприятия
    public String cancelEvent(Long eventId, RedirectAttributes redirectAttributes) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        // Удаляем связанное расписание
        Schedule schedule = scheduleRepository.findByEvent_EventId(eventId);
        if (schedule != null) {
            scheduleRepository.delete(schedule); // Удаляем запись расписания
        }

        // Обновляем статус мероприятия на "отменено"
        event.setStatus(EventStatus.REJECTED);
        eventRepository.save(event);

        sendCancellationNotifications(event);

        redirectAttributes.addFlashAttribute("error", "Мероприятие отменено и расписание удалено!");
        return "redirect:/admin/events/requests";
    }
    private String generateQRCodeBase64(String text, int width, int height) {
        try {
            // Передаем текст в QR-код в формате UTF-8
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name()); // Устанавливаем кодировку UTF-8

            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

            // Создаем BufferedImage
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);  // Черный и белый пиксели
                }
            }

            // Преобразуем изображение в Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (WriterException | IOException e) {
            // Логируем ошибку
            System.err.println("Ошибка при генерации QR-кода: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка генерации QR-кода", e);
        }
    }



    private void sendCancellationNotifications(Event event) {
        List<EventRegistration> registrations = eventRegistrationRepository.findByEvent(event);

        for (EventRegistration registration : registrations) {
            User user = registration.getUser();
            String subject = "Мероприятие отменено";
            String text = "Здравствуйте, " + user.getFullName() + "!\n\n" +
                    "Мероприятие \"" + event.getTitle() + "\", на которое вы зарегистрировались, было отменено.\n" +
                    "Дата: " + event.getStartDateTime().toLocalDate() + "\n" +
                    "Время: " + event.getStartDateTime().toLocalTime() + " - " + event.getEndDateTime().toLocalTime() + "\n" +
                    "Аудитория: " + event.getClassroom().getName() + "\n\n" +
                    "С уважением,\nСистема бронирования";

            emailService.sendEventStatusNotification(user.getEmail(), subject, text);
        }

        // Отправка уведомления организатору
        sendRejectionNotificationToOrganizer(event);
    }

    private void sendRejectionNotificationToOrganizer(Event event) {
        User organizer = event.getCreator();
        String subject = "Ваше мероприятие отменено";
        String text = "Здравствуйте, " + organizer.getFullName() + "!\n\n" +
                "Ваше мероприятие \"" + event.getTitle() + "\" было отменено администратором.\n" +
                "Дата: " + event.getStartDateTime().toLocalDate() + "\n" +
                "Время: " + event.getStartDateTime().toLocalTime() + " - " + event.getEndDateTime().toLocalTime() + "\n" +
                "Аудитория: " + event.getClassroom().getName() + "\n\n" +
                "С уважением,\nСистема бронирования";

        emailService.sendEventStatusNotification(organizer.getEmail(), subject, text);
    }

    // Получение количества зарегистрированных
    public int getRegisteredCount(Long eventId) {
        return eventRegistrationRepository.countByEvent_EventId(eventId);
    }
}