package ru.mivlgu.ReservationSystem.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mivlgu.ReservationSystem.Entities.Event;
import ru.mivlgu.ReservationSystem.Entities.EventComments;
import ru.mivlgu.ReservationSystem.Repositories.EventCommentsRepository;
import ru.mivlgu.ReservationSystem.Repositories.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventCommentsRepository eventCommentsRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<EventComments> getCommentsByEventId(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event != null) {
            return eventCommentsRepository.findByEvent(event);
        }
        return new ArrayList<>();
    }

    public EventComments saveComment(EventComments comment) {
        return eventCommentsRepository.save(comment);
    }
}