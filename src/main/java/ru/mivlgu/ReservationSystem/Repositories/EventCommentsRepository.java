package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mivlgu.ReservationSystem.Entities.Event;
import ru.mivlgu.ReservationSystem.Entities.EventComments;

import java.util.List;

public interface EventCommentsRepository extends JpaRepository<EventComments, Long> {
    List<EventComments> findByEvent(Event event);
}