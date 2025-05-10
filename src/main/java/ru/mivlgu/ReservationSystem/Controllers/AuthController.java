package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mivlgu.ReservationSystem.Entities.User;
import ru.mivlgu.ReservationSystem.Enums.UserRole;
import ru.mivlgu.ReservationSystem.Repositories.UserRepository;
import ru.mivlgu.ReservationSystem.Services.UserService;

@Controller
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        // Шифруем пароль перед сохранением
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        // Устанавливаем роль GUEST по умолчанию
        user.setRole(UserRole.GUEST);

        // Сохраняем нового пользователя
        userRepository.save(user);

        return "redirect:/login";  // После регистрации перенаправляем на страницу логина
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

}
