package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mivlgu.ReservationSystem.Entities.*;
import ru.mivlgu.ReservationSystem.Repositories.*;

import java.time.LocalDateTime;
import java.util.List;

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

    // Обработка исключений
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleInvalidDateTimeException(IllegalArgumentException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "create-event";
    }
}
