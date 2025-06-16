package ru.mivlgu.ReservationSystem.dto;

import lombok.Data;
import ru.mivlgu.ReservationSystem.Entities.Classroom;
import ru.mivlgu.ReservationSystem.Entities.ClassroomEquipment;
import ru.mivlgu.ReservationSystem.Entities.Equipment;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ClassroomDto {
    private Long classroomId;
    private String name;
    private Integer capacity;
    private String location;
    private Boolean status;
    //private List<Equipment> equipment;
    private List<ClassroomEquipmentDto> equipmentList;
    /*
    public ClassroomDto(Long classroomId, String name, Integer capacity, String location, Boolean status, List<Equipment> equipment) {
        this.classroomId = classroomId;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.status = status;
        this.equipment = equipment;
    }
     */

    public ClassroomDto(Classroom classroom) {
        this.classroomId = classroom.getClassroomId();
        this.name = classroom.getName();
        this.capacity = classroom.getCapacity();
        this.location = classroom.getLocation();
        this.status = classroom.getStatus();
/*
        this.equipment = classroom.getEquipmentList().stream()
                .map(ClassroomEquipment::getEquipment)
                .toList();

 */
        this.equipmentList = classroom.getEquipmentList().stream()
                .map(ce -> new ClassroomEquipmentDto(ce.getEquipment(), ce.getQuantity()))
                .collect(Collectors.toList());
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
/*
    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }
 */

    public List<ClassroomEquipmentDto> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<ClassroomEquipmentDto> equipmentList) {
        this.equipmentList = equipmentList;
    }
}