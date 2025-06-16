package ru.mivlgu.ReservationSystem.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "classroomequipment")
public class ClassroomEquipment {

    @EmbeddedId
    private ClassroomEquipmentId id;  // Составной ключ

    @ManyToOne
    @MapsId("classroomId")  // Использование составного ключа для связывания с Classroom
    @JoinColumn(name = "classroomid")
    //@JsonBackReference
    @JsonIgnore
    private Classroom classroom;

    @ManyToOne
    @MapsId("equipmentId")  // Использование составного ключа для связывания с Equipment
    @JsonIgnore
    //@JsonBackReference
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassroomEquipment that = (ClassroomEquipment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
