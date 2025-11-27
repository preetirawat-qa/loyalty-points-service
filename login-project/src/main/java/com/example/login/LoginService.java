package com.example.login;

/**
 * LoginService coordinates user login logic, including:
 * - validating input
 * - checking network connectivity
 * - handling authentication attempts
 * - locking out the user after too many failures
 * - supporting a simple "remember me" token mechanism
 */

public class LoginService {

    private final AuthRepository authRepository;
    private final NetworkMonitor networkMonitor;
    private final RememberMeStore rememberMeStore;

    private int failedAttempts = 0;

       
    private static final int LOCK_THRESHOLD = 3; // Maximum number of failed attempts before lockout

    
    /**
     * Constructs a LoginService with required dependencies.
     *
     * @param authRepository   Authentication provider
     * @param networkMonitor   Checks if the system is online
     * @param rememberMeStore  Stores persistent login tokens
     */
    public LoginService(AuthRepository authRepository,
                        NetworkMonitor networkMonitor,
                        RememberMeStore rememberMeStore) {

        this.authRepository = authRepository;
        this.networkMonitor = networkMonitor;
        this.rememberMeStore = rememberMeStore;
    }

    public LoginResult login(String username, String password, boolean rememberMe) {
        
        // Validate that username and password are not null or empty
    	if (username == null || username.isEmpty() ||
    		    password == null || password.isEmpty()) {
    		    return LoginResult.INVALID_CREDENTIALS;
    		}

    	if (failedAttempts >= LOCK_THRESHOLD) {
            return LoginResult.LOCKED_OUT;
        }
// Ensure the system is online before attempting authentication
        if (!networkMonitor.isOnline()) {
            return LoginResult.OFFLINE;
        }

        boolean ok = authRepository.authenticate(username, password);

        if (ok) {
            failedAttempts = 0;
            if (rememberMe) {
                rememberMeStore.saveToken("TOKEN_ABC_123");
            }
            return LoginResult.SUCCESS;
        } else {
            failedAttempts++;
            return LoginResult.INVALID_CREDENTIALS;
        }
        
    }
    

    public int getFailedAttempts() {
        return failedAttempts;
    }
    
}
