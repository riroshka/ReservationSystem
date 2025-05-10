package ru.mivlgu.ReservationSystem.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "classroomequipment")
public class ClassroomEquipment {
    @EmbeddedId
    private ClassroomEquipmentId id;

    @ManyToOne
    @MapsId("classroomId")
    @JoinColumn(name = "classroomid")
    private Classroom classroom;

    @ManyToOne
    @MapsId("equipmentId")
    @JoinColumn(name = "equipmentid")
    private Equipment equipment;

    @Column(nullable = false)
    private Integer quantity = 1;

    public ClassroomEquipment(ClassroomEquipmentId id, Classroom classroom, Equipment equipment, Integer quantity) {
        this.id = id;
        this.classroom = classroom;
        this.equipment = equipment;
        this.quantity = quantity;
    }

    public ClassroomEquipment() {
    }

    public ClassroomEquipmentId getId() {
        return id;
    }

    public void setId(ClassroomEquipmentId id) {
        this.id = id;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
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
}