package ru.mivlgu.ReservationSystem.Entities;

import jakarta.persistence.*;
import ru.mivlgu.ReservationSystem.Enums.RegistrationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventregistrations")
public class EventRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registrationid")
    private Long registrationID;

    @ManyToOne
    @JoinColumn(name = "eventid", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Column(name = "registrationdate", nullable = false)
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RegistrationStatus status = RegistrationStatus.WAITING;

    @Column(name = "isstudent", nullable = false)
    private boolean isStudent;

    @Column(name = "qrcode", columnDefinition = "TEXT")
    private String qrCode;

    public EventRegistration(Long registrationID, Event event, User user, LocalDateTime registrationDate, RegistrationStatus status, boolean isStudent, String qrCode) {
        this.registrationID = registrationID;
        this.event = event;
        this.user = user;
        this.registrationDate = registrationDate;
        this.status = status;
        this.isStudent = isStudent;
        this.qrCode = qrCode;
    }

    public EventRegistration() {
    }

    public Long getRegistrationID() {
        return registrationID;
    }

    public void setRegistrationID(Long registrationID) {
        this.registrationID = registrationID;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public boolean isStudent() {
        return isStudent;
    }

    public void setStudent(boolean student) {
        isStudent = student;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}