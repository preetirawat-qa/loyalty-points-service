package com.example.login;

public class RememberMeStore {

    private String storedToken;

    public void saveToken(String token) {
        this.storedToken = token;
    }

    public String getToken() {
        return storedToken;
    }
}
