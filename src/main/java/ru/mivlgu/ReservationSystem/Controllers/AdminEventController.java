package ru.mivlgu.ReservationSystem.Controllers;

import jakarta.persistence.EntityNotFoundException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mivlgu.ReservationSystem.Entities.Event;
import ru.mivlgu.ReservationSystem.Entities.EventTemplate;
import ru.mivlgu.ReservationSystem.Entities.Schedule;
import ru.mivlgu.ReservationSystem.Entities.User;
import ru.mivlgu.ReservationSystem.Enums.EventStatus;
import ru.mivlgu.ReservationSystem.Enums.UserRole;
import ru.mivlgu.ReservationSystem.Repositories.*;
import ru.mivlgu.ReservationSystem.Services.EmailService;
import ru.mivlgu.ReservationSystem.Services.EventService;
import ru.mivlgu.ReservationSystem.Services.VKService;
import ru.mivlgu.ReservationSystem.dto.EventCardDto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventRepository eventRepository;
    private final EventTemplateRepository eventTemplateRepository;
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final ClassroomRepository classroomRepository;
    private final ScheduleRepository scheduleRepository;
    private final VKService vkService;
    private final EventService eventService;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final EmailService emailService;

    @Autowired
    public AdminEventController(EventRepository eventRepository, EventTemplateRepository eventTemplateRepository, UserRepository userRepository, EquipmentRepository equipmentRepository, ClassroomRepository classroomRepository, ScheduleRepository scheduleRepository, VKService vkService, EventService eventService, EventRegistrationRepository eventRegistrationRepository, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.eventTemplateRepository = eventTemplateRepository;
        this.userRepository = userRepository;
        this.equipmentRepository = equipmentRepository;
        this.classroomRepository = classroomRepository;
        this.scheduleRepository = scheduleRepository;
        this.vkService = vkService;
        this.eventService = eventService;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.emailService = emailService;
    }


    // Страница для списка заявок
    @GetMapping("/requests")
    public String showEventRequests(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> optionalUser = userRepository.findByEmail(userDetails.getUsername());

        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();

        List<Event> events;
        if (user.getRole().equals(UserRole.ADMIN)) {
            events = eventRepository.findByStatusWithCreator(EventStatus.PENDING);
        } else if (user.getRole().equals(UserRole.TEACHER)) {
            events = eventRepository.findByStatusAndCreator(EventStatus.PENDING, user);
        } else {
            return "redirect:/access-denied";
        }

        // Load the schedule for each event
        for (Event event : events) {
            Schedule schedule = scheduleRepository.findByEvent_EventId(event.getEventId());
            event.setSchedule(schedule); // Set schedule for the event
        }

        model.addAttribute("events", events);
        return "admin/event-requests";
    }

    @GetMapping("/registrations/{eventId}")
    public ResponseEntity<InputStreamResource> downloadRegistrationList(@PathVariable Long eventId) throws IOException {
        // Получаем список зарегистрированных пользователей для конкретного мероприятия
        List<User> registeredUsers = eventRegistrationRepository.findByEvent_EventId(eventId)
                .stream()
                .map(eventRegistration -> eventRegistration.getUser())
                .collect(Collectors.toList());

        // Создаем новый Word документ
        XWPFDocument document = new XWPFDocument();
        XWPFTable table = document.createTable();

        // Заголовки таблицы
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("Имя");
        headerRow.addNewTableCell().setText("Группа");

        // Добавляем данные студентов
        for (User user : registeredUsers) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(user.getFullName());
            row.getCell(1).setText(user.getStudentGroup() != null ? user.getStudentGroup().getName() : "-");
        }

        // Сохраняем в ByteArrayOutputStream для отправки
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.write(out);
        byte[] documentBytes = out.toByteArray();

        // Отправляем документ как ответ
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(documentBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=registrations.docx");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    // Страница для рассмотрения заявки
    @GetMapping("/request/{eventId}")
    public String reviewEventRequest(@PathVariable Long eventId, Model model) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found"));
        if (event.getPhoto() != null) {
            String base64Photo = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(event.getPhoto());
            model.addAttribute("existingPhoto", base64Photo);
        }
        model.addAttribute("event", event);
        model.addAttribute("equipment", equipmentRepository.findAll());
        return "admin/event-review";  // Страница для подробного просмотра заявки
    }

    // Подтверждение заявки
    @PostMapping("/approve/{eventId}")
    public String approveEvent(@PathVariable Long eventId,
                               @RequestParam(value = "photo", required = false) MultipartFile photo,
                               @RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "description", required = false) String description,
                               @RequestParam(value = "postToVK", defaultValue = "false") boolean postToVK,
                               RedirectAttributes redirectAttributes) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        // Создание шаблона мероприятия
        EventTemplate template = new EventTemplate();
        template.setName(name != null && !name.isEmpty() ? name : event.getTitle());
        template.setDescription(description != null && !description.isEmpty() ? description : event.getDescription());
        template.setDefaultDuration(90L); // Устанавливаем значение по умолчанию

        try {
            // Если фото было выбрано, сохраняем его, иначе оставляем существующее фото
            if (photo != null && !photo.isEmpty()) {
                template.setPhoto(photo.getBytes());
            } else if (event.getPhoto() != null) {
                template.setPhoto(event.getPhoto());
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("warning", "Ошибка загрузки фото: " + e.getMessage());
            e.printStackTrace();
        }

        // Сохраняем шаблон
        eventTemplateRepository.save(template);

        // Связываем шаблон с мероприятием
        event.setEventTemplate(template);

        // Обновление статуса заявки
        event.setStatus(EventStatus.APPROVED);
        eventRepository.save(event); // Сохраняем обновленное событие

        // Публикуем пост в ВКонтакте с помощью VKService
        if (postToVK) {
            EventCardDto eventCardDto = new EventCardDto(event, false, null);
            vkService.postToVK(eventCardDto); // Отправляем пост с актуальными данными
        }

        sendApprovalNotification(event);

        redirectAttributes.addFlashAttribute("success", "Заявка утверждена и шаблон создан!");
        return "redirect:/admin/events/requests";
    }

    // Отмена заявки
    @PostMapping("/reject/{eventId}")
    public String rejectEvent(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found"));

        eventService.cancelEvent(eventId, redirectAttributes);

        sendRejectionNotification(event);

        redirectAttributes.addFlashAttribute("error", "Заявка отклонена!");
        return "redirect:/admin/events/requests";
    }

    private void sendApprovalNotification(Event event) {
        String email = event.getCreator().getEmail();
        String subject = "Ваша заявка на мероприятие утверждена";
        String text = "Здравствуйте, " + event.getCreator().getFullName() + "!\n\n" +
                "Ваша заявка на мероприятие \"" + event.getTitle() + "\" была утверждена.\n" +
                "Дата: " + event.getStartDateTime().toLocalDate() + "\n" +
                "Время: " + event.getStartDateTime().toLocalTime() + " - " + event.getEndDateTime().toLocalTime() + "\n" +
                "Аудитория: " + event.getClassroom().getName() + "\n\n" +
                "С уважением,\nСистема бронирования";

        emailService.sendEventStatusNotification(email, subject, text);
    }

    private void sendRejectionNotification(Event event) {
        String email = event.getCreator().getEmail();
        String subject = "Ваша заявка на мероприятие отклонена";
        String text = "Здравствуйте, " + event.getCreator().getFullName() + "!\n\n" +
                "К сожалению, ваша заявка на мероприятие \"" + event.getTitle() + "\" была отклонена.\n" +
                "Причина: решение администратора\n\n" +
                "Вы можете создать новую заявку с учетом замечаний.\n\n" +
                "С уважением,\nСистема бронирования";

        emailService.sendEventStatusNotification(email, subject, text);
    }
}
