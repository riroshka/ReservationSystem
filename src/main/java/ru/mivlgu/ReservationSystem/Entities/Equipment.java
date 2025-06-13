package ru.mivlgu.ReservationSystem.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipmentid")
    private Long equipmentId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(mappedBy = "equipment")
    @JsonIgnore
    private List<ClassroomEquipment> classroomEquipments;

    public Equipment(Long equipmentId, String name, String description, List<ClassroomEquipment> classroomEquipments) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.description = description;
        this.classroomEquipments = classroomEquipments;
    }

    public Equipment() {
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ClassroomEquipment> getClassroomEquipments() {
        return classroomEquipments;
    }

    public void setClassroomEquipments(List<ClassroomEquipment> classroomEquipments) {
        this.classroomEquipments = classroomEquipments;
    }
}