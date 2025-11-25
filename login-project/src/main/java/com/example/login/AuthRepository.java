package com.example.login;

public class AuthRepository {

    public boolean authenticate(String username, String password) {
        return username.equals("admin") && password.equals("password123");
    }
}
