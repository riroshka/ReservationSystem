package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mivlgu.ReservationSystem.Entities.Classroom;
import ru.mivlgu.ReservationSystem.Entities.ClassroomEquipment;
import ru.mivlgu.ReservationSystem.Entities.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    List<Classroom> findByCapacityLessThanEqual(Integer capacity);

    List<Classroom> findByEquipmentList_Equipment_EquipmentId(Long equipmentId);

    List<Classroom> findByCapacityLessThanEqualAndEquipmentList_Equipment_EquipmentId(Integer capacity, Long equipmentId);

    @Query("SELECT c FROM Classroom c WHERE c.capacity >= :minCapacity")
    List<Classroom> findByMinCapacity(int minCapacity);
    List<Classroom> findByCapacityGreaterThanEqual(int minCapacity);

    @Query("SELECT s FROM Schedule s WHERE "
            + "s.classroom.classroomId = :classroomId AND "
            + "((s.startDateTime < :end) AND (s.endDateTime > :start))")
    List<Schedule> findConflictingSchedules(
            @Param("classroomId") Long classroomId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<Classroom> findByStatus(Boolean status);

    List<Classroom> findByCapacityBetween(int minCapacity, int maxCapacity);

    @Query("SELECT DISTINCT c FROM Classroom c " +
            "JOIN c.equipmentList ce " +
            "WHERE c.capacity BETWEEN :minCapacity AND :maxCapacity " +
            "AND ce.equipment.equipmentId IN :equipmentIds")
    List<Classroom> findByCapacityAndEquipment(
            @Param("minCapacity") int minCapacity,
            @Param("maxCapacity") int maxCapacity,
            @Param("equipmentIds") List<Long> equipmentIds);
}
