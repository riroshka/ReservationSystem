package ru.mivlgu.ReservationSystem.Entities;

import jakarta.persistence.*;

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

    public Equipment(Long equipmentId, String name, String description) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.description = description;
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
}