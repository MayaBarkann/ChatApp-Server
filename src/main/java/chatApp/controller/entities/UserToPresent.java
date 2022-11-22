package chatApp.controller.entities;

import chatApp.Entities.User;
import chatApp.Entities.UserType;


public class UserToPresent {
    private final String username;
    private final String email;
    private final UserType userType;

    @Override
    public String toString() {
        return "{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", user type='" + userType + '\'' +
                '}';
    }

    public static UserToPresent createFromUser(User user) {
        return new UserToPresent(user.getUsername(), user.getEmail(), user.getUserType());
    }

    private UserToPresent(String username, String email, UserType userType) {
        this.username = username;
        this.email = email;
        this.userType = userType;
    }
}
