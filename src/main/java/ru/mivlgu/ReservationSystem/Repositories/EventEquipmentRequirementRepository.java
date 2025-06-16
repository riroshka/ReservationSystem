package ru.mivlgu.ReservationSystem.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.mivlgu.ReservationSystem.Entities.EventEquipmentRequirement;
import ru.mivlgu.ReservationSystem.Entities.EventEquipmentRequirementId;

import java.util.Collection;
import java.util.List;

public interface EventEquipmentRequirementRepository extends JpaRepository<EventEquipmentRequirement, EventEquipmentRequirementId> {
    List<EventEquipmentRequirement> findByEvent_EventIdIn(Collection<Long> eventIds);
}
