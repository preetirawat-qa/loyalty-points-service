package com.example.login;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.Mockito;

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

    @Test
    public void testSuccessLogin() {
        Mockito.when(authRepo.authenticate("admin", "pass")).thenReturn(true);

        LoginResult result = service.login("admin", "pass", false);

        Assert.assertEquals(result, LoginResult.SUCCESS);
    }

    @Test
    public void testFailureIncrementsCounter() {
        Mockito.when(authRepo.authenticate("a", "b")).thenReturn(false);

        service.login("a", "b", false);
        Assert.assertEquals(service.getFailedAttempts(), 1);
    }

    @Test
    public void testLockoutAfterThreeFails() {
        Mockito.when(authRepo.authenticate("a", "b")).thenReturn(false);

        service.login("a", "b", false);
        service.login("a", "b", false);
        service.login("a", "b", false);

        LoginResult result = service.login("a", "b", false);

        Assert.assertEquals(result, LoginResult.LOCKED_OUT);
    }

    @Test
    public void testOfflineShowsMessageAndNoServiceCall() {
        networkMonitor.setOnline(false);

        LoginResult result = service.login("admin", "pass", false);

        Mockito.verify(authRepo, Mockito.never()).authenticate(Mockito.any(), Mockito.any());
        Assert.assertEquals(result, LoginResult.OFFLINE);
    }

    @Test
    public void testRememberMeStoresToken() {
        Mockito.when(authRepo.authenticate("admin", "pass")).thenReturn(true);

        service.login("admin", "pass", true);

        Assert.assertNotNull(rememberMeStore.getToken());
    }
    @Test
    public void testFailureCountResetsAfterSuccess() {
        Mockito.when(authRepo.authenticate("admin", "pass")).thenReturn(false);
        service.login("admin", "pass", false);

        Mockito.when(authRepo.authenticate("admin", "pass")).thenReturn(true);
        service.login("admin", "pass", false);

        Assert.assertEquals(service.getFailedAttempts(), 0);
    }
    
    @Test
    public void testEmptyCredentialsValidation() {
        LoginResult result = service.login("", "", false);
        Mockito.verify(authRepo, Mockito.never()).authenticate(Mockito.any(), Mockito.any());
        Assert.assertEquals(result, LoginResult.INVALID_CREDENTIALS);
    }


}
