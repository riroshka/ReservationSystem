package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mivlgu.ReservationSystem.Entities.Classroom;
import ru.mivlgu.ReservationSystem.Entities.Event;
import ru.mivlgu.ReservationSystem.Entities.Schedule;
import ru.mivlgu.ReservationSystem.Services.ClassroomService;
import ru.mivlgu.ReservationSystem.Services.EventService;
import ru.mivlgu.ReservationSystem.Services.ScheduleService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ClassroomService classroomService;
    private final EventService eventService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService, ClassroomService classroomService, EventService eventService) {
        this.scheduleService = scheduleService;
        this.classroomService = classroomService;
        this.eventService = eventService;
    }
/*
    // Страница с календарем расписания
    @GetMapping
    public String showSchedule(@RequestParam(value = "date", required = false) String date, Model model) {
        if (date == null) {
            date = LocalDate.now().toString();  // Если дата не указана, показываем текущую
        }

        LocalDate selectedDate = LocalDate.parse(date);

        // Получаем расписания для выбранной даты
        List<Schedule> schedules = scheduleService.getSchedulesForDate(selectedDate);

        model.addAttribute("schedules", schedules);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("classrooms", classroomService.getAllClassrooms());

        return "schedule/calendar";  // Страница с календарем и расписанием
    }
*/
    // Страница для добавления нового расписания
    @GetMapping("/create")
    public String createScheduleForm(@RequestParam("classroomId") Long classroomId,
                                     @RequestParam("date") String date,
                                     Model model) {
        Classroom classroom = classroomService.getClassroomById(classroomId).orElseThrow();
        model.addAttribute("classroom", classroom);
        model.addAttribute("events", eventService.getAllEvents());  // Все мероприятия
        model.addAttribute("date", date);  // Выбранная дата
        return "schedule/create";  // Форма добавления нового расписания
    }

    // Обработка создания расписания
    @PostMapping("/create")
    public String createSchedule(@RequestParam("classroomId") Long classroomId,
                                 @RequestParam("eventId") Long eventId,
                                 @RequestParam("startDateTime") String startDateTime,
                                 @RequestParam("endDateTime") String endDateTime,
                                 @RequestParam("date") String date) {

        // Преобразуем строки в LocalDateTime
        LocalDateTime start = LocalDateTime.parse(date + "T" + startDateTime);
        LocalDateTime end = LocalDateTime.parse(date + "T" + endDateTime);

        // Создаем объект Schedule
        Schedule schedule = new Schedule();
        Classroom classroom = classroomService.getClassroomById(classroomId).orElseThrow();
        Event event = eventService.getEventById(eventId).orElseThrow();

        schedule.setClassroom(classroom);
        schedule.setEvent(event);
        schedule.setStartDateTime(start);
        schedule.setEndDateTime(end);

        // Сохраняем расписание
        scheduleService.saveSchedule(schedule);

        return "redirect:/schedule?date=" + date;  // Перенаправляем обратно на страницу с расписанием
    }

    // Страница для редактирования расписания
    @GetMapping("/edit/{id}")
    public String editScheduleForm(@PathVariable Long id, @RequestParam("date") String date, Model model) {
        Schedule schedule = scheduleService.getScheduleById(id);
        model.addAttribute("schedule", schedule);
        model.addAttribute("classrooms", classroomService.getAllClassrooms());
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("date", date);
        return "schedule/edit";  // Форма редактирования расписания
    }

    // Обработка редактирования расписания
    @PostMapping("/edit/{id}")
    public String editSchedule(@PathVariable Long id,
                               @RequestParam("classroomId") Long classroomId,
                               @RequestParam("eventId") Long eventId,
                               @RequestParam("startDateTime") String startDateTime,
                               @RequestParam("endDateTime") String endDateTime,
                               @RequestParam("date") String date) {

        // Преобразуем строки в LocalDateTime
        LocalDateTime start = LocalDateTime.parse(date + "T" + startDateTime);
        LocalDateTime end = LocalDateTime.parse(date + "T" + endDateTime);

        Schedule schedule = scheduleService.getScheduleById(id);
        Classroom classroom = classroomService.getClassroomById(classroomId).orElseThrow();
        Event event = eventService.getEventById(eventId).orElseThrow();

        schedule.setClassroom(classroom);
        schedule.setEvent(event);
        schedule.setStartDateTime(start);
        schedule.setEndDateTime(end);

        // Сохраняем изменения
        scheduleService.saveSchedule(schedule);

        return "redirect:/schedule?date=" + date;  // Перенаправляем обратно на страницу с расписанием
    }

    // Удаление расписания
    @GetMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, @RequestParam("date") String date) {
        scheduleService.deleteSchedule(id);  // Удаляем расписание
        return "redirect:/schedule?date=" + date;  // Перенаправляем на страницу с расписанием
    }
}
