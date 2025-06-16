package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mivlgu.ReservationSystem.Entities.Event;
import ru.mivlgu.ReservationSystem.Entities.EventRegistration;
import ru.mivlgu.ReservationSystem.Entities.User;
import ru.mivlgu.ReservationSystem.Enums.EventStatus;
import ru.mivlgu.ReservationSystem.Enums.RegistrationStatus;
import ru.mivlgu.ReservationSystem.dto.RegistrationStats;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    int countByEvent_EventId(Long eventId);
    boolean existsByEvent_EventIdAndUser_UserId(Long eventId, Long userId);
    Optional<EventRegistration> findByEvent_EventIdAndUser_UserId(Long eventId, Long userId);
    List<Event> findByStatus(EventStatus status);
    List<EventRegistration> findByEvent_EventId(Long eventId);
    List<EventRegistration> findByEvent(Event event);
    int countByEventAndStatus(Event event, RegistrationStatus status);
    int countByUser(User user);
    int countByUserAndStatus(User user, RegistrationStatus status);
    List<EventRegistration> findByEvent_EventIdInAndStatus(Collection<Long> eventIds, RegistrationStatus status);
}