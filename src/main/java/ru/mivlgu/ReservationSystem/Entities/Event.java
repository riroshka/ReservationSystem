package ru.mivlgu.ReservationSystem.Entities;

import jakarta.persistence.*;
import ru.mivlgu.ReservationSystem.Enums.EventStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventid")
    private Long eventId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "startdatetime", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "enddatetime", nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "creatorid", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "classroomid", nullable = false)
    private Classroom classroom;

    @Column(name = "qrcodepath")
    private String qrCodePath;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventEquipmentRequirement> equipmentRequirements = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "templateid", nullable = false)
    private EventTemplate eventTemplate;

    public Event(Long eventId, String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime, EventStatus status, User creator, Classroom classroom, String qrCodePath, List<EventEquipmentRequirement> equipmentRequirements) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.creator = creator;
        this.classroom = classroom;
        this.qrCodePath = qrCodePath;
        this.equipmentRequirements = equipmentRequirements;
    }

    public Event() {
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public String getQrCodePath() {
        return qrCodePath;
    }

    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }

    public List<EventEquipmentRequirement> getEquipmentRequirements() {
        return equipmentRequirements;
    }

    public void setEquipmentRequirements(List<EventEquipmentRequirement> equipmentRequirements) {
        this.equipmentRequirements = equipmentRequirements;
    }

    public EventTemplate getEventTemplate() {
        return eventTemplate;
    }

    public void setEventTemplate(EventTemplate eventTemplate) {
        this.eventTemplate = eventTemplate;
    }
}
