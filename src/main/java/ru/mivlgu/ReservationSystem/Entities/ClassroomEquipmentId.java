package ru.mivlgu.ReservationSystem.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
class ClassroomEquipmentId implements Serializable {
    @Column(name = "classroomid")
    private Long classroomId;

    @Column(name = "equipmentid")
    private Long equipmentId;

    public ClassroomEquipmentId(Long classroomId, Long equipmentId) {
        this.classroomId = classroomId;
        this.equipmentId = equipmentId;
    }

    public ClassroomEquipmentId() {
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
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
        ClassroomEquipmentId that = (ClassroomEquipmentId) o;
        return Objects.equals(classroomId, that.classroomId) && Objects.equals(equipmentId, that.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classroomId, equipmentId);
    }
}
