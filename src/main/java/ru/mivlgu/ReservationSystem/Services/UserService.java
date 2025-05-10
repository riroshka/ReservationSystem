package ru.mivlgu.ReservationSystem.Services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mivlgu.ReservationSystem.Entities.User;
import ru.mivlgu.ReservationSystem.Repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Ищем пользователя по email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Возвращаем объект UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())  // Используем email как логин
                .password(user.getPasswordHash()) // Устанавливаем зашифрованный пароль
                .roles(user.getRole().name()) // Преобразуем роль в строку
                .build();
    }
}
