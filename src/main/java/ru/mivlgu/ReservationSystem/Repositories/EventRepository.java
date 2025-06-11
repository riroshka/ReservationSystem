package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mivlgu.ReservationSystem.Entities.Event;
import ru.mivlgu.ReservationSystem.Entities.User;
import ru.mivlgu.ReservationSystem.Enums.EventStatus;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.creator WHERE e.status = :status")
    List<Event> findByStatusWithCreator(@Param("status") EventStatus status);
    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.creator = :creator")
    List<Event> findByStatusAndCreator(@Param("status") EventStatus status, @Param("creator") User creator);
}
