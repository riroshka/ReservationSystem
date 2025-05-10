package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mivlgu.ReservationSystem.Entities.Event;
import ru.mivlgu.ReservationSystem.Entities.EventComments;
import ru.mivlgu.ReservationSystem.Services.EventService;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "events/list";
    }

    @GetMapping("/{id}")
    public String viewEvent(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEventById(id).orElse(null));
        model.addAttribute("comments", eventService.getCommentsByEventId(id));
        return "events/view";
    }

    @GetMapping("/create")
    public String createEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "events/create";
    }

    @PostMapping("/create")
    public String createEvent(@ModelAttribute Event event) {
        eventService.saveEvent(event);
        return "redirect:/events";
    }

    @PostMapping("/{eventId}/comment")
    public String addComment(@PathVariable Long eventId, @ModelAttribute EventComments comment) {
        Event event = eventService.getEventById(eventId).orElse(null);
        if (event != null) {
            comment.setEvent(event);
            eventService.saveComment(comment);
        }
        return "redirect:/events/" + eventId;
    }
}
