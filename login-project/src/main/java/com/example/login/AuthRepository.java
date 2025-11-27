package com.example.login;
// AuthRepository handles user authentication.//
public class AuthRepository {

     /**
     * Authenticates a user based on username and password.
     *
     * @param username the username provided by the user
     * @param password the password provided by the user
     * @return true if credentials match the allowed values, false otherwise
     */
    public boolean authenticate(String username, String password) {
        return username.equals("admin") && password.equals("password123"); // Checks whether the given username and password match the fixed credentials.
        // This is for demonstration only — do NOT use hardcoded credentials in production.
    }
}
