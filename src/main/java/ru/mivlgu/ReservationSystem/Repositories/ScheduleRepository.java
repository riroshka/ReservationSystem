package ru.mivlgu.ReservationSystem.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mivlgu.ReservationSystem.Entities.Schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s WHERE s.classroom.classroomId = :classroomId AND DATE(s.startDateTime) = :date")
    List<Schedule> findByClassroomIdAndDate(
            @Param("classroomId") Long classroomId,
            @Param("date") LocalDate date);
    Schedule findByEvent_EventId(Long eventId);
    List<Schedule> findByClassroom_ClassroomIdAndStartDateTimeBetween(
            Long classroomId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );
}