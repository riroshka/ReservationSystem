package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mivlgu.ReservationSystem.Entities.EventComment;

public interface EventCommentRepository extends JpaRepository<EventComment, Long> {
}