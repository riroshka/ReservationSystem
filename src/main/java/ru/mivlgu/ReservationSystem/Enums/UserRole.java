package ru.mivlgu.ReservationSystem.Enums;

public enum UserRole {
    ADMIN("Администратор"),
    TEACHER("Преподаватель"),
    STUDENT("Студент"),
    GUEST("Гость");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
