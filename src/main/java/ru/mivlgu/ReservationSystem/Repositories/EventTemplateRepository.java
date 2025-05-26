package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mivlgu.ReservationSystem.Entities.EventTemplate;

public interface EventTemplateRepository extends JpaRepository<EventTemplate, Long> {
}