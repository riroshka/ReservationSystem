package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mivlgu.ReservationSystem.Entities.Classroom;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
}
