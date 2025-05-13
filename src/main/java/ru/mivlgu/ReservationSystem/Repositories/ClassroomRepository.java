package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mivlgu.ReservationSystem.Entities.Classroom;
import ru.mivlgu.ReservationSystem.Entities.ClassroomEquipment;

import java.util.List;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    List<Classroom> findByCapacityLessThanEqual(Integer capacity);

    List<Classroom> findByEquipmentList_Equipment_EquipmentId(Long equipmentId);

    List<Classroom> findByCapacityLessThanEqualAndEquipmentList_Equipment_EquipmentId(Integer capacity, Long equipmentId);
}
