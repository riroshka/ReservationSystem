package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mivlgu.ReservationSystem.Entities.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}
