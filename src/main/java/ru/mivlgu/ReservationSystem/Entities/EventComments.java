package ru.mivlgu.ReservationSystem.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventcomments")
public class EventComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentid")
    private Long commentID;

    @ManyToOne
    @JoinColumn(name = "eventid", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Column(name = "commenttext", nullable = false)
    private String commentText;

    @Column(name = "createdat", nullable = false)
    private LocalDateTime createdAt;

    public EventComments(Long commentID, Event event, User user, String commentText, LocalDateTime createdAt) {
        this.commentID = commentID;
        this.event = event;
        this.user = user;
        this.commentText = commentText;
        this.createdAt = createdAt;
    }

    public EventComments() {
    }

    public Long getCommentID() {
        return commentID;
    }

    public void setCommentID(Long commentID) {
        this.commentID = commentID;
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

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
