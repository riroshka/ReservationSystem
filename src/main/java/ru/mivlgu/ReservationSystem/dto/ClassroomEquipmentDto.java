package ru.mivlgu.ReservationSystem.dto;

import lombok.Data;
import ru.mivlgu.ReservationSystem.Entities.Equipment;

@Data
public class ClassroomEquipmentDto {
    private Equipment equipment;
    private Integer quantity;

    public ClassroomEquipmentDto(Equipment equipment, Integer quantity) {
        this.equipment = equipment;
        this.quantity = quantity;
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
