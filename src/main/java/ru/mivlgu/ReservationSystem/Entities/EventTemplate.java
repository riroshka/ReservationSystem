package ru.mivlgu.ReservationSystem.Entities;

import jakarta.persistence.*;

import java.util.Base64;

@Entity
@Table(name = "eventtemplates")
public class EventTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "templateid")
    private Long templateId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "defaultduration", nullable = false)
    private Long defaultDuration;  // Продолжительность по умолчанию в минутах

    @Column(name = "photo", columnDefinition = "BYTEA")
    private byte[] photo;

    @Transient
    private String photoBase64;

    public EventTemplate() {
    }

    public EventTemplate(Long templateId, String name, String description, Long defaultDuration, byte[] photo) {
        this.templateId = templateId;
        this.name = name;
        this.description = description;
        this.defaultDuration = defaultDuration;
        this.photo = photo;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
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

    public Long getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(Long defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoBase64() {
        if (this.photo != null) {
            return Base64.getEncoder().encodeToString(this.photo);
        }
        return null;
    }
}
