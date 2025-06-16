package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            // Если возникает ошибка (например, email или логин уже заняты), передаем ошибку в модель
            model.addAttribute("error", e.getMessage());
            return "register"; // Возвращаемся на страницу регистрации с сообщением об ошибке
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
