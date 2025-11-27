package com.example.login;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link LoginService}.
 * 
 * Uses TestNG for test structure and Mockito for mocking dependencies.
 * 
 * Each test verifies specific authentication behaviors such as:
 * - successful login
 * - failed login increments counter
 * - lockout after repeated failures
 * - offline behavior
 * - remember-me token storage
 * - counter reset after success
 * - empty credential validation
 */

public class LoginServiceTest {

    private AuthRepository authRepo;
    private NetworkMonitor networkMonitor;
    private RememberMeStore rememberMeStore;
    private LoginService service;

    @BeforeMethod
    public void setup() {
        authRepo = Mockito.mock(AuthRepository.class);
        networkMonitor = new NetworkMonitor();
        rememberMeStore = new RememberMeStore();
        service = new LoginService(authRepo, networkMonitor, rememberMeStore);
    }
 
    @Test    /**
     * Verifies that a successful authentication returns SUCCESS.
     */
    public void testSuccessLogin() {
        Mockito.when(authRepo.authenticate("admin", "pass")).thenReturn(true);

        LoginResult result = service.login("admin", "pass", false);

        Assert.assertEquals(result, LoginResult.SUCCESS);
    }

    @Test   /**
     * Ensures that a failed login increments the failedAttempts counter.
     */
    public void testFailureIncrementsCounter() {
        Mockito.when(authRepo.authenticate("a", "b")).thenReturn(false);

        service.login("a", "b", false);
        Assert.assertEquals(service.getFailedAttempts(), 1);
    }

    @Test   
    /**
     * Ensures that after three failed login attempts, the user becomes locked out.
     */
    public void testLockoutAfterThreeFails() {
        Mockito.when(authRepo.authenticate("a", "b")).thenReturn(false);

        service.login("a", "b", false);
        service.login("a", "b", false);
        service.login("a", "b", false);

        LoginResult result = service.login("a", "b", false);

        Assert.assertEquals(result, LoginResult.LOCKED_OUT);
    }

    @Test     /**
     * Validates that when the system is offline:
     * - authentication is not attempted
     * - the result is OFFLINE
     */
    public void testOfflineShowsMessageAndNoServiceCall() {
        networkMonitor.setOnline(false);

        LoginResult result = service.login("admin", "pass", false);

        Mockito.verify(authRepo, Mockito.never()).authenticate(Mockito.any(), Mockito.any());
        Assert.assertEquals(result, LoginResult.OFFLINE);
    }

    @Test     /**
     * Confirms that enabling "remember me" stores a persistent token.
     */
    public void testRememberMeStoresToken() {
        Mockito.when(authRepo.authenticate("admin", "pass")).thenReturn(true);

        service.login("admin", "pass", true);

        Assert.assertNotNull(rememberMeStore.getToken());
    }
    @Test   /**
     * Ensures that after a successful login, the failedAttempts counter resets to zero.
     */
    public void testFailureCountResetsAfterSuccess() {
        Mockito.when(authRepo.authenticate("admin", "pass")).thenReturn(false);
        service.login("admin", "pass", false);

        Mockito.when(authRepo.authenticate("admin", "pass")).thenReturn(true);
        service.login("admin", "pass", false);

        Assert.assertEquals(service.getFailedAttempts(), 0);
    }
    
    @Test   /**
     * Ensures that empty username/password inputs:
     * - return INVALID_CREDENTIALS
     * - do not call the authentication repository
     */
    public void testEmptyCredentialsValidation() {
        LoginResult result = service.login("", "", false);
        Mockito.verify(authRepo, Mockito.never()).authenticate(Mockito.any(), Mockito.any());
        Assert.assertEquals(result, LoginResult.INVALID_CREDENTIALS);
    }


}
