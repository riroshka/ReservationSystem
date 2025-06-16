package ru.mivlgu.ReservationSystem.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
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

    @JsonIgnore
    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClassroomEquipment> classroomEquipments;

    @JsonIgnore
    @OneToMany(mappedBy = "equipment")
    private List<EventEquipmentRequirement> eventEquipmentRequirements = new ArrayList<>();

    public Equipment(Long equipmentId, String name, String description, List<ClassroomEquipment> classroomEquipments, List<EventEquipmentRequirement> eventEquipmentRequirements) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.description = description;
        this.classroomEquipments = classroomEquipments;
        this.eventEquipmentRequirements = eventEquipmentRequirements;
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

    public List<EventEquipmentRequirement> getEventEquipmentRequirements() {
        return eventEquipmentRequirements;
    }

    public void setEventEquipmentRequirements(List<EventEquipmentRequirement> eventEquipmentRequirements) {
        this.eventEquipmentRequirements = eventEquipmentRequirements;
    }
}