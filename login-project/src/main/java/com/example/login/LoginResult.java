package com.example.login;

/**
 * LoginResult represents the possible outcomes of a login attempt.
 */
public enum LoginResult {

    /** Login succeeded successfully. */
    SUCCESS,

    /** The username or password is incorrect. */
    INVALID_CREDENTIALS,

    /**
     * Authentication could not be performed due to network or
     * connectivity issues.
     */
    OFFLINE,

    /**
     * The user has been locked out after too many failed attempts
     * or due to security restrictions.
     */
    LOCKED_OUT
}

