package ru.mivlgu.ReservationSystem.dto;

import lombok.Data;
import ru.mivlgu.ReservationSystem.Entities.Classroom;
import ru.mivlgu.ReservationSystem.Entities.ClassroomEquipment;
import ru.mivlgu.ReservationSystem.Entities.Equipment;
import java.util.List;

@Data
public class ClassroomDto {
    private Long classroomId;
    private String name;
    private Integer capacity;
    private String location;
    private Boolean status;
    private List<Equipment> equipment;

    public ClassroomDto(Long classroomId, String name, Integer capacity, String location, Boolean status, List<Equipment> equipment) {
        this.classroomId = classroomId;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.status = status;
        this.equipment = equipment;
    }

    public ClassroomDto(Classroom classroom) {
        this.classroomId = classroom.getClassroomId();
        this.name = classroom.getName();
        this.capacity = classroom.getCapacity();
        this.location = classroom.getLocation();
        this.status = classroom.getStatus();
        this.equipment = classroom.getEquipmentList().stream()
                .map(ClassroomEquipment::getEquipment)
                .toList();
    }
}