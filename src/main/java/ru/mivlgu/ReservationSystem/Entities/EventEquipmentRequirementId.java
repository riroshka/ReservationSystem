package ru.mivlgu.ReservationSystem.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EventEquipmentRequirementId implements Serializable {
    @Column(name = "eventid")
    private Long eventId;

    @Column(name = "equipmentid")
    private Long equipmentId;

    public EventEquipmentRequirementId(Long eventId, Long equipmentId) {
        this.eventId = eventId;
        this.equipmentId = equipmentId;
    }

    public EventEquipmentRequirementId() {
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EventEquipmentRequirementId that = (EventEquipmentRequirementId) o;
        return Objects.equals(eventId, that.eventId) && Objects.equals(equipmentId, that.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, equipmentId);
    }
}
