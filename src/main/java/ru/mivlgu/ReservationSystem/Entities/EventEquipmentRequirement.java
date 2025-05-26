package ru.mivlgu.ReservationSystem.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "eventequipmentrequirements")
public class EventEquipmentRequirement {
    @EmbeddedId
    private EventEquipmentRequirementId id;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "eventid")
    private Event event;

    @ManyToOne
    @MapsId("equipmentId")
    @JoinColumn(name = "equipmentid")
    private Equipment equipment;

    @Column(nullable = false)
    private Integer quantity = 1;

    public EventEquipmentRequirement() {
    }

    public EventEquipmentRequirement(EventEquipmentRequirementId id, Event event, Equipment equipment, Integer quantity) {
        this.id = id;
        this.event = event;
        this.equipment = equipment;
        this.quantity = quantity;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public EventEquipmentRequirementId getId() {
        return id;
    }

    public void setId(EventEquipmentRequirementId id) {
        this.id = id;
    }
}


