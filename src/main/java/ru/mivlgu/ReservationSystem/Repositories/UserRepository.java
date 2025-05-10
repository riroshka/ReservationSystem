package ru.mivlgu.ReservationSystem.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mivlgu.ReservationSystem.Entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
