package com.example.login;
/**
 * RememberMeStore provides a simple in-memory storage mechanism
 * for a "remember me" login token.
 *
 * In a real application, this would typically store encrypted tokens
 * in persistent storage (database, shared preferences, secure vault, etc.)
 * instead of keeping them only in memory.
 */
public class RememberMeStore {

    private String storedToken;
/**
     * Saves the provided token for future retrieval.
     *
     * @param token the authentication token to store
     */
    
    public void saveToken(String token) {
        this.storedToken = token;
    }
/**
     * Retrieves the last stored token.
     *
     * @return the saved token, or null if none exists
     */
    public String getToken() {
        return storedToken;
    }
}
