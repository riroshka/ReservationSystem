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
import java.util.List;

@Controller
public class EventController {

    private final EventRepository eventRepository;
    private final ClassroomRepository classroomRepository;

    @Autowired
    public EventController(EventRepository eventRepository, ClassroomRepository classroomRepository) {
        this.eventRepository = eventRepository;
        this.classroomRepository = classroomRepository;
    }
    // Страница со списком мероприятий
    @GetMapping("/events")
    public String listEvents(Model model) {
        // Получаем список всех мероприятий
        List<Event> events = eventRepository.findAll();

        // Проверка, что данные действительно загружены
        if (events.isEmpty()) {
            System.out.println("Нет мероприятий в базе данных.");
        } else {
            System.out.println("Найдено " + events.size() + " мероприятий.");
        }

        model.addAttribute("events", events); // Добавляем список мероприятий в модель
        return "event-list"; // Название страницы с событием
    }


}
