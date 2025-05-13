package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mivlgu.ReservationSystem.Entities.Event;
import ru.mivlgu.ReservationSystem.Entities.Classroom;
import ru.mivlgu.ReservationSystem.Repositories.EventRepository;
import ru.mivlgu.ReservationSystem.Repositories.ClassroomRepository;

import java.time.LocalDateTime;

@Controller
public class EventController {

    private final EventRepository eventRepository;
    private final ClassroomRepository classroomRepository;

    @Autowired
    public EventController(EventRepository eventRepository, ClassroomRepository classroomRepository) {
        this.eventRepository = eventRepository;
        this.classroomRepository = classroomRepository;
    }
/*
    @GetMapping("/create-event")
    public String createEventPage(Model model) {
        model.addAttribute("classrooms", classroomRepository.findAll()); // Отображаем все аудитории
        return "create-event"; // Страница для создания мероприятия
    }

    @PostMapping("/create-event")
    public String createEvent(@RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @RequestParam("classroomId") Long classroomId,
                              @RequestParam("startDateTime") String startDateTime,
                              @RequestParam("endDateTime") String endDateTime) {
        // Логика для создания мероприятия
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setStartDateTime(LocalDateTime.parse(startDateTime));
        event.setEndDateTime(LocalDateTime.parse(endDateTime));
        event.setClassroom(classroomRepository.findById(classroomId).orElse(null));

        // Сохранение мероприятия
        eventRepository.save(event);
        return "redirect:/events"; // После сохранения перенаправление на список мероприятий
    }
 */
}
