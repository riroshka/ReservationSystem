package ru.mivlgu.ReservationSystem.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.mivlgu.ReservationSystem.Entities.EventEquipmentRequirement;
import ru.mivlgu.ReservationSystem.Entities.EventEquipmentRequirementId;

public interface EventEquipmentRequirementRepository extends JpaRepository<EventEquipmentRequirement, EventEquipmentRequirementId> {
}
