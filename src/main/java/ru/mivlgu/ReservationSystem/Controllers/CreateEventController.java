package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mivlgu.ReservationSystem.Entities.Event;
import ru.mivlgu.ReservationSystem.Entities.Classroom;
import ru.mivlgu.ReservationSystem.Entities.User;
import ru.mivlgu.ReservationSystem.Repositories.EventRepository;
import ru.mivlgu.ReservationSystem.Repositories.ClassroomRepository;
import ru.mivlgu.ReservationSystem.Repositories.UserRepository;

import java.time.LocalDateTime;

@Controller
public class CreateEventController {

    private final EventRepository eventRepository;
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;

    @Autowired
    public CreateEventController(EventRepository eventRepository, ClassroomRepository classroomRepository,
                                 UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.classroomRepository = classroomRepository;
        this.userRepository = userRepository;
    }

    // Страница создания мероприятия
    @GetMapping("/create-event")
    public String createEventPage(Model model) {
        model.addAttribute("classrooms", classroomRepository.findAll()); // Передаем список аудиторий
        return "create-event"; // Страница для создания мероприятия
    }

    // Обработка создания мероприятия
    @PostMapping("/create-event")
    public String createEvent(@RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @RequestParam("classroomId") Long classroomId,
                              @RequestParam("startDateTime") String startDateTime,
                              @RequestParam("endDateTime") String endDateTime) {
        // Получаем текущего аутентифицированного пользователя
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername(); // Получаем email из аутентификации

        // Получаем вашего User (сущность User), если email существует в базе данных
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Создаем мероприятие и устанавливаем значения
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setStartDateTime(LocalDateTime.parse(startDateTime));
        event.setEndDateTime(LocalDateTime.parse(endDateTime));
        event.setClassroom(classroomRepository.findById(classroomId).orElse(null));
        event.setCreator(currentUser); // Устанавливаем текущего пользователя как создателя мероприятия

        // Сохраняем мероприятие
        eventRepository.save(event);

        return "redirect:/events"; // Перенаправляем на страницу с событиями
    }
}
