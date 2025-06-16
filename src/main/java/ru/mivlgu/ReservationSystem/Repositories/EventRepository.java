package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mivlgu.ReservationSystem.Entities.Event;
import ru.mivlgu.ReservationSystem.Entities.User;
import ru.mivlgu.ReservationSystem.Enums.EventStatus;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.creator WHERE e.status = :status")
    List<Event> findByStatusWithCreator(@Param("status") EventStatus status);
    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.creator = :creator")
    List<Event> findByStatusAndCreator(@Param("status") EventStatus status, @Param("creator") User creator);

    @Query("SELECT e FROM Event e WHERE e.status = 'APPROVED' "/* +
            "AND (:start IS NULL OR e.startDateTime >= :start) " +
            "AND (:end IS NULL OR e.startDateTime <= :end)"*/)
    List<Event> findApprovedEventsInPeriod(
            @Param("start") LocalDateTime  start,
            @Param("end") LocalDateTime  end
    );


    /*
       @Query(value = "SELECT * FROM events e " +
               "WHERE e.status = 'APPROVED' " +
               "AND (:start IS NULL OR e.start_datetime >= CAST(:startStartOfDay AS TIMESTAMP)) " +
               "AND (:end IS NULL OR e.start_datetime <= CAST(:endEndOfDay AS TIMESTAMP))",
               nativeQuery = true)
       List<Event> findApprovedEventsInPeriodNative(
               @Param("start") LocalDate start,
               @Param("end") LocalDate end,
               @Param("startStartOfDay") LocalDateTime startStartOfDay,
               @Param("endEndOfDay") LocalDateTime endEndOfDay);
*/
}
