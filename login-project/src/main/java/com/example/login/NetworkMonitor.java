
package com.example.login;

/**
 * NetworkMonitor provides a simple way to simulate or check
 * whether the system is currently online.
 * 
 * In a real-world application, this class would likely check
 * network interfaces, ping a server, or use platform APIs.
 */

public class NetworkMonitor {

    private boolean online = true;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean value) {
        this.online = value;
    }
}
