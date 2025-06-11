package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mivlgu.ReservationSystem.Entities.User;
import ru.mivlgu.ReservationSystem.Enums.UserRole;
import ru.mivlgu.ReservationSystem.Services.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. Список всех пользователей
    /*
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
     */

    // 2. Форма создания пользователя
    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/create_user";
    }

    // 3. Обработка создания пользователя
    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user,
                             BindingResult result,
                             Model model) {

        // Валидация: для студентов обязательно указание группы
        if (user.getRole() == UserRole.STUDENT &&
                (user.getStudentGroup() == null || user.getStudentGroup().isBlank())) {
            result.rejectValue("studentGroup", "error.user", "Для студента необходимо указать учебную группу");
        }

        // Проверка уникальности email
        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Пользователь с таким email уже существует");
        }

        // Проверка уникальности логина
        if (userService.loginExists(user.getLogin())) {
            result.rejectValue("login", "error.user", "Пользователь с таким логином уже существует");
        }

        if (result.hasErrors()) {
            return "admin/create_user";
        }

        userService.createUser(user);
        return "redirect:/admin/users";
    }

    // 4. Форма редактирования пользователя
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/edit_user";
    }

    // 5. Обработка редактирования пользователя
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("user") User updatedUser,
                             BindingResult result) {

        // Валидация: для студентов обязательно указание группы
        if (updatedUser.getRole() == UserRole.STUDENT &&
                (updatedUser.getStudentGroup() == null || updatedUser.getStudentGroup().isBlank())) {
            result.rejectValue("studentGroup", "error.user", "Для студента необходимо указать учебную группу");
        }

        // Проверка уникальности email (исключая текущего пользователя)
        User existingUser = userService.getUserById(id);
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
                userService.emailExists(updatedUser.getEmail())) {
            result.rejectValue("email", "error.user", "Пользователь с таким email уже существует");
        }

        // Проверка уникальности логина (исключая текущего пользователя)
        if (!existingUser.getLogin().equals(updatedUser.getLogin()) &&
                userService.loginExists(updatedUser.getLogin())) {
            result.rejectValue("login", "error.user", "Пользователь с таким логином уже существует");
        }

        if (result.hasErrors()) {
            return "admin/edit_user";
        }

        userService.updateUser(id, updatedUser);
        return "redirect:/admin/users";
    }

    // 6. Удаление пользователя
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
    @GetMapping
    public String listUsers(@RequestParam(name = "role", required = false) UserRole role, Model model) {
        List<User> users;
        if (role != null) {
            users = userService.getUsersByRole(role);
        } else {
            users = userService.getAllUsers();
        }

        model.addAttribute("users", users);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("currentRole", role);
        return "admin/users";
    }
}