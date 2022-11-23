package chatApp.controller.entities;

import chatApp.Entities.User;

public class UserRegister {
    private String email;
    private String password;
    private String username;

    public UserRegister(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public UserRegister() {

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername(){
        return username;
    }
}
