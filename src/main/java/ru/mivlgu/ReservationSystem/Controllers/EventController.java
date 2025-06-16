package ru.mivlgu.ReservationSystem.Controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mivlgu.ReservationSystem.Entities.Event;
import ru.mivlgu.ReservationSystem.Entities.Classroom;
import ru.mivlgu.ReservationSystem.Entities.Schedule;
import ru.mivlgu.ReservationSystem.Entities.User;
import ru.mivlgu.ReservationSystem.Repositories.EquipmentRepository;
import ru.mivlgu.ReservationSystem.Repositories.EventRepository;
import ru.mivlgu.ReservationSystem.Repositories.ClassroomRepository;
import ru.mivlgu.ReservationSystem.Repositories.ScheduleRepository;
import ru.mivlgu.ReservationSystem.Services.EventService;
import ru.mivlgu.ReservationSystem.dto.EventDto;
import ru.mivlgu.ReservationSystem.dto.TimeSlot;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {
    private final ClassroomRepository classroomRepository;
    private final ScheduleRepository scheduleRepository;
    private final EquipmentRepository equipmentRepository;
    private final EventService eventService;

    public EventController(ClassroomRepository classroomRepository, ScheduleRepository scheduleRepository, EquipmentRepository equipmentRepository, EventService eventService) {
        this.classroomRepository = classroomRepository;
        this.scheduleRepository = scheduleRepository;
        this.equipmentRepository = equipmentRepository;
        this.eventService = eventService;
    }

    @GetMapping("/new")
    public String showNewEventForm(Model model) {
        List<Classroom> classrooms = classroomRepository.findByStatus(true);
        model.addAttribute("classrooms", classrooms);

        // Добавляем пустой DTO для привязки данных
        model.addAttribute("eventDto", new EventDto());

        return "new-event";
    }
    @GetMapping("/busy-slots")
    @ResponseBody
    public List<TimeSlot> getBusySlots(
            @RequestParam Long classroomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return eventService.getBusySlotsForClassroom(classroomId, date);
    }

    @GetMapping("/available-slots")
    @ResponseBody
    public List<TimeSlot> getAvailableSlots(
            @RequestParam Long classroomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Schedule> existingSchedules = scheduleRepository.findByClassroomIdAndDate(classroomId, date);
        return calculateAvailableSlots(date, existingSchedules);
    }

    @GetMapping("/create-details")
    public String showEventDetailsForm(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
            @RequestParam Long classroomId,
            Model model) {

        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow();

        EventDto eventDto = new EventDto();
        eventDto.setDate(date);
        eventDto.setStartTime(startTime);
        eventDto.setEndTime(endTime);
        eventDto.setClassroomId(classroomId);

        model.addAttribute("event", eventDto);
        model.addAttribute("equipments", equipmentRepository.findAll());
        model.addAttribute("classroom", classroom);

        return "event-details";
    }

    private List<TimeSlot> calculateAvailableSlots(LocalDate date, List<Schedule> existingSchedules) {
        List<TimeSlot> allSlots = Arrays.asList(
                new TimeSlot(LocalTime.of(8,30), LocalTime.of(10,0)),
                new TimeSlot(LocalTime.of(10,15), LocalTime.of(11,45)),
                new TimeSlot(LocalTime.of(12,30), LocalTime.of(14,0)),
                new TimeSlot(LocalTime.of(14,15), LocalTime.of(15,45)),
                new TimeSlot(LocalTime.of(16,0), LocalTime.of(17,30)),
                new TimeSlot(LocalTime.of(17,45), LocalTime.of(19,15)),
                new TimeSlot(LocalTime.of(19,30), LocalTime.of(21,0))
        );

        List<TimeSlot> availableSlots = new ArrayList<>();

        for (TimeSlot slot : allSlots) {
            boolean isOccupied = existingSchedules.stream().anyMatch(s -> {
                LocalTime slotStart = s.getStartDateTime().toLocalTime();
                LocalTime slotEnd = s.getEndDateTime().toLocalTime();
                return slotStart.equals(slot.getStartTime()) && slotEnd.equals(slot.getEndTime());
            });

            if (!isOccupied) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }

    @PostMapping("/create")
    public String createEvent(
            @ModelAttribute("event") EventDto eventDto,
            @RequestParam("photo") MultipartFile photo,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            if (userDetails == null) {
                throw new EntityNotFoundException("Пользователь не аутентифицирован");
            }

            // Преобразуем MultipartFile в byte[]
            //byte[] photoBytes = photo.getBytes();

            byte[] photoBytes = new byte[0];

            // Проверяем, если фото не загружено, используем изображение по умолчанию
            if (photo.isEmpty()) {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/images/placeholder.jpg");
                if (inputStream != null) {
                    photoBytes = inputStream.readAllBytes();
                }
            } else {
                photoBytes = photo.getBytes();
            }

            // Получаем логин из аутентификационных данных
            String email = userDetails.getUsername();
            Event event = eventService.createEvent(eventDto, email, photoBytes);  // Передаем photoBytes в сервис

            redirectAttributes.addFlashAttribute("success", "Мероприятие создано!");
            return "redirect:/admin/events/requests";
        } catch (EntityNotFoundException | IOException e) {
            System.err.println("Error creating event: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Ошибка создания мероприятия: " + e.getMessage());
            return "redirect:/events/new";
        }
    }
}

