package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mivlgu.ReservationSystem.Entities.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
