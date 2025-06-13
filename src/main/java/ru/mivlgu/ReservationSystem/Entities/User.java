package ru.mivlgu.ReservationSystem.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import ru.mivlgu.ReservationSystem.Enums.UserRole;

import java.io.Serializable;

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private Long userId;

    @NotBlank(message = "ФИО обязательно")
    @Column(name = "fullname", nullable = false)
    private String fullName;

    @Email(message = "Некорректный email")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Логин обязателен")
    @Column(nullable = false, unique = true)
    private String login;

    @Column(name = "passwordhash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studentgroup")
    private Group studentGroup;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некорректный номер телефона")
    @Column(name = "phonenumber", nullable = false)
    private String phoneNumber;


    public User(Long userId, String fullName, String email, String login, String passwordHash, UserRole role, Group studentGroup, String phoneNumber) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
        this.studentGroup = studentGroup;
        this.phoneNumber = phoneNumber;
    }

    public User() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Group getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(Group studentGroup) {
        this.studentGroup = studentGroup;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}
