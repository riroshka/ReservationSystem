package ru.mivlgu.ReservationSystem.dto;

public class RegistrationStats {
    private int userCount;
    private int maxCount;

    public RegistrationStats(int userCount, int maxCount) {
        this.userCount = userCount;
        this.maxCount = maxCount;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
}
