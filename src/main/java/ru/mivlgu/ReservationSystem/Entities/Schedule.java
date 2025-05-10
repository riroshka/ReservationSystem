package ru.mivlgu.ReservationSystem.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduleid")
    private Long scheduleID;

    @ManyToOne
    @JoinColumn(name = "classroomid", nullable = false)
    private Classroom classroom;

    @ManyToOne
    @JoinColumn(name = "eventid", nullable = false)
    private Event event;

    @Column(name = "startdatetime", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "enddatetime", nullable = false)
    private LocalDateTime endDateTime;

    public Schedule(Long scheduleID, Classroom classroom, Event event, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.scheduleID = scheduleID;
        this.classroom = classroom;
        this.event = event;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public Schedule() {
    }

    public Long getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(Long scheduleID) {
        this.scheduleID = scheduleID;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
}
