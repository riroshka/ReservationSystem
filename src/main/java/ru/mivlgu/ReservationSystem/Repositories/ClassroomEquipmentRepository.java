package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mivlgu.ReservationSystem.Entities.ClassroomEquipment;
import ru.mivlgu.ReservationSystem.Entities.ClassroomEquipmentId;

import java.util.List;

public interface ClassroomEquipmentRepository extends JpaRepository<ClassroomEquipment, ClassroomEquipmentId> {
    List<ClassroomEquipment> findByClassroom_ClassroomId(Long classroomId);
}
