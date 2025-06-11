package ru.mivlgu.ReservationSystem.dto;

import ru.mivlgu.ReservationSystem.Entities.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventCardDto {
    private Long eventId;
    private String title;
    private String date;
    private String time;
    private String location;
    private String description;
    private String photoBase64;
    private boolean userRegistered;
    private int registeredCount;
    private int capacity;
    private boolean isFull;
    private String qrCode;
    private boolean isPast;
    private boolean upcoming;

    /*
    public EventCardDto(Event event) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        this.eventId = event.getEventId();
        this.title = event.getEventTemplate() != null ? event.getEventTemplate().getName() : event.getTitle();
        this.date = event.getStartDateTime().format(dateFormatter);
        this.time = event.getStartDateTime().format(timeFormatter);
        this.location = event.getClassroom().getLocation();
        this.description = event.getEventTemplate() != null ?
                event.getEventTemplate().getDescription() :
                event.getDescription();

        this.photoBase64 = event.getEventTemplate() != null && event.getEventTemplate().getPhoto() != null ?
                java.util.Base64.getEncoder().encodeToString(event.getEventTemplate().getPhoto()) :
                null;
    }
     */
    public EventCardDto(Event event, boolean userRegistered, String qrCode) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        this.eventId = event.getEventId();
        this.title = event.getEventTemplate() != null ? event.getEventTemplate().getName() : event.getTitle();
        this.date = event.getStartDateTime().format(dateFormatter);
        this.time = event.getStartDateTime().format(timeFormatter);
        this.location = event.getClassroom().getLocation();
        this.description = event.getEventTemplate() != null ?
                event.getEventTemplate().getDescription() :
                event.getDescription();

        this.photoBase64 = event.getEventTemplate() != null && event.getEventTemplate().getPhoto() != null ?
                java.util.Base64.getEncoder().encodeToString(event.getEventTemplate().getPhoto()) :
                null;

        // Добавляем информацию о регистрации
        this.userRegistered = userRegistered;
        this.registeredCount = event.getRegistrations() != null ? event.getRegistrations().size() : 0;
        this.capacity = event.getClassroom().getCapacity();
        this.isFull = this.registeredCount >= this.capacity;
        this.qrCode = qrCode;
        this.isPast = event.getEndDateTime().isBefore(LocalDateTime.now());
        this.upcoming = event.getEndDateTime().isAfter(LocalDateTime.now());
    }

    public Long getEventId() { return eventId; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getPhotoBase64() { return photoBase64; }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public boolean isUserRegistered() {
        return userRegistered;
    }

    public void setUserRegistered(boolean userRegistered) {
        this.userRegistered = userRegistered;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(int registeredCount) {
        this.registeredCount = registeredCount;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    public boolean isPast() {
        return isPast;
    }

    public void setPast(boolean past) {
        isPast = past;
    }

    public boolean isUpcoming() {
        return upcoming;
    }

    public void setUpcoming(boolean upcoming) {
        this.upcoming = upcoming;
    }
}
