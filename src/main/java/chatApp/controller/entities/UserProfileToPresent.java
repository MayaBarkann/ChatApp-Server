package chatApp.controller.entities;

import chatApp.Entities.User;
import chatApp.Entities.UserProfile;

import java.time.LocalDate;

public class UserProfileToPresent {
    private final String username;
    private final String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String description;
    private String imageUrl;

    public static UserProfileToPresent createFromUserProfileAndUser(UserProfile userProfile, User user){
        return new UserProfileToPresent(user.getUsername(),user.getEmail(), userProfile.getFirstName(),
                userProfile.getLastName(), userProfile.getDateOfBirth(), userProfile.getDescription(), userProfile.getImageUrl());
    }

    private UserProfileToPresent(String username, String email, String firstName, String lastName, LocalDate dateOfBirth, String description, String imageUrl) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
