package ru.mivlgu.ReservationSystem.Services;

import org.springframework.stereotype.Service;
import ru.mivlgu.ReservationSystem.Entities.Schedule;
import ru.mivlgu.ReservationSystem.Repositories.ScheduleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    // Получение расписаний для конкретной даты
    public List<Schedule> getSchedulesForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay(); // Начало дня
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay(); // Конец дня
        return scheduleRepository.findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(startOfDay, endOfDay);
    }

    // Сохранение расписания
    public Schedule saveSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    // Удаление расписания
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    // Получение расписания по аудитории
    public List<Schedule> getSchedulesByClassroomId(Long classroomId) {
        return scheduleRepository.findByClassroom_ClassroomId(classroomId);
    }

    // Получение расписания по ID
    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid schedule ID: " + id));
    }
}
