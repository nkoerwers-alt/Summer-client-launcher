package com.summerlauncher;

public class UserProfile {

    private final String username;
    private final boolean guest;

    public UserProfile(String username, boolean guest) {
        this.username = username;
        this.guest = guest;
    }

    public String getUsername() {
        return username;
    }

    public boolean isGuest() {
        return guest;
    }
}
