package chatApp.controller.entities;

public class LoginCredentials {
    private String email;
    private String password;

    public LoginCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LoginCredentials() {

    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
