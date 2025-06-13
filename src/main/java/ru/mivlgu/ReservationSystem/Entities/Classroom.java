package ru.mivlgu.ReservationSystem.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classrooms")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroomid")
    private Long classroomId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Boolean status = true;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ClassroomEquipment> equipmentList = new ArrayList<>();

    public Classroom(Long classroomId, String name, Integer capacity, String location, Boolean status, List<ClassroomEquipment> equipmentList) {
        this.classroomId = classroomId;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.status = status;
        this.equipmentList = equipmentList;
    }

    public Classroom() {
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<ClassroomEquipment> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<ClassroomEquipment> equipmentList) {
        this.equipmentList = equipmentList;
    }
}
