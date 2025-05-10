package ru.mivlgu.ReservationSystem.Entities;

import jakarta.persistence.*;
import ru.mivlgu.ReservationSystem.Enums.NotificationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "socialposts")
public class SocialPosts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postid")
    private Long postID;

    @ManyToOne
    @JoinColumn(name = "eventid", nullable = false)
    private Event event;

    @Column(name = "vkpostid", nullable = false, length = 100)
    private String vkPostID;

    @Column(name = "postdate", nullable = false)
    private LocalDateTime postDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;

    public SocialPosts(Long postID, Event event, String vkPostID, LocalDateTime postDate, NotificationStatus status) {
        this.postID = postID;
        this.event = event;
        this.vkPostID = vkPostID;
        this.postDate = postDate;
        this.status = status;
    }

    public SocialPosts() {
    }

    public Long getPostID() {
        return postID;
    }

    public void setPostID(Long postID) {
        this.postID = postID;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getVkPostID() {
        return vkPostID;
    }

    public void setVkPostID(String vkPostID) {
        this.vkPostID = vkPostID;
    }

    public LocalDateTime getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDateTime postDate) {
        this.postDate = postDate;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
}
