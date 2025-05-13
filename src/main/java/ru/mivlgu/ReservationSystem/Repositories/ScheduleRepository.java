package ru.mivlgu.ReservationSystem.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.mivlgu.ReservationSystem.Entities.Schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // Метод для поиска расписания по диапазону времени (для проверки конфликтов)
    // Метод для поиска расписания по диапазону времени
    List<Schedule> findByStartDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // Метод для поиска расписания по дате (весь день)
    List<Schedule> findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(LocalDateTime startOfDay, LocalDateTime endOfDay);

    // Метод для поиска расписания по аудитории (по Classroom)
    List<Schedule> findByClassroom_ClassroomId(Long classroomId);

    // Метод для получения расписания по конкретному времени
    Optional<Schedule> findByClassroom_ClassroomIdAndStartDateTime(Long classroomId, LocalDateTime startDateTime);
}