package ru.mivlgu.ReservationSystem.Controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mivlgu.ReservationSystem.Entities.Event;
import ru.mivlgu.ReservationSystem.Entities.EventRegistration;
import ru.mivlgu.ReservationSystem.Entities.User;
import ru.mivlgu.ReservationSystem.Repositories.EventRegistrationRepository;
import ru.mivlgu.ReservationSystem.Repositories.UserRepository;
import ru.mivlgu.ReservationSystem.Services.EventService;
import ru.mivlgu.ReservationSystem.dto.EventCardDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PublicEventController {
    private final EventService eventService;
    private final UserRepository userRepository;
    private final EventRegistrationRepository eventRegistrationRepository;

    public PublicEventController(EventService eventService, UserRepository userRepository, EventRegistrationRepository eventRegistrationRepository) {
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
    }

    @GetMapping("/events")
    public String showEvents(Model model,
                             @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        User currentUser;
        if (userDetails != null) {
            // Получаем пользователя по email из аутентификационных данных
            currentUser = userRepository.findByEmail(userDetails.getUsername())
                    .orElse(null);
        } else {
            currentUser = null;
        }

        // Получаем список всех мероприятий
        List<Event> events = eventService.getApprovedEventsForCards();

        // Разделяем мероприятия на будущие и завершенные
        LocalDateTime now = LocalDateTime.now();
        List<Event> upcomingEvents = events.stream()
                .filter(event -> event.getEndDateTime().isAfter(now)) // Мероприятия, которые еще не закончились
                .sorted((e1, e2) -> e2.getStartDateTime().compareTo(e1.getStartDateTime())) // Сортировка по дате
                .collect(Collectors.toList());

        List<Event> pastEvents = events.stream()
                .filter(event -> event.getEndDateTime().isBefore(now)) // Завершенные мероприятия
                .sorted((e1, e2) -> e2.getStartDateTime().compareTo(e1.getStartDateTime())) // Сортировка по дате
                .collect(Collectors.toList());

        // Создаем DTO с информацией о регистрации
        List<EventCardDto> upcomingEventDtos = upcomingEvents.stream()
                .map(event -> {
                    boolean isRegistered = false;
                    String qrCode = null;
                    if (currentUser != null) {
                        isRegistered = eventService.isUserRegistered(event.getEventId(), currentUser.getUserId());
                        if (isRegistered) {
                            qrCode = eventRegistrationRepository
                                    .findByEvent_EventIdAndUser_UserId(event.getEventId(), currentUser.getUserId())
                                    .map(EventRegistration::getQrCode)
                                    .orElse(null);
                        }
                    }
                    return new EventCardDto(event, isRegistered, qrCode);
                })
                .collect(Collectors.toList());

        // Для завершенных мероприятий
        List<EventCardDto> pastEventDtos = pastEvents.stream()
                .map(event -> {
                    boolean isRegistered = false;
                    String qrCode = null;
                    if (currentUser != null) {
                        isRegistered = eventService.isUserRegistered(event.getEventId(), currentUser.getUserId());
                        if (isRegistered) {
                            qrCode = eventRegistrationRepository
                                    .findByEvent_EventIdAndUser_UserId(event.getEventId(), currentUser.getUserId())
                                    .map(EventRegistration::getQrCode)
                                    .orElse(null);
                        }
                    }
                    return new EventCardDto(event, isRegistered, qrCode);
                })
                .collect(Collectors.toList());

        model.addAttribute("upcomingEvents", upcomingEventDtos);
        model.addAttribute("pastEvents", pastEventDtos);
        model.addAttribute("currentUser", currentUser);
        return "events";
    }


    @PostMapping("/events/register/{eventId}")
    public String registerForEvent(@PathVariable Long eventId,
                                   @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        try {
            eventService.registerForEvent(eventId, user);
            redirectAttributes.addFlashAttribute("success", "Вы успешно зарегистрировались на мероприятие!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/events";
    }

    @PostMapping("/events/cancel/{eventId}")
    public String cancelRegistration(@PathVariable Long eventId,
                                     @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Получаем пользователя по email
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        try {
            eventService.cancelRegistration(eventId, user.getUserId());
            redirectAttributes.addFlashAttribute("success", "Регистрация отменена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/events";
    }
}