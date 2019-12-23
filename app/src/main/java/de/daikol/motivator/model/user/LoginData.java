package de.daikol.motivator.model.user;

/**
 * This class is used to provide login data in a safe manner.
 */
public class LoginData {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
