package chatApp.controller.entities;

import chatApp.Entities.User;
import chatApp.Entities.UserProfile;
import javax.persistence.Entity;
import java.time.LocalDate;

public class UserProfileToPresent {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String description;
    private String imageUrl;

    public UserProfileToPresent(){}

    public static UserProfileToPresent createFromUserProfileAndUser(UserProfile userProfile, User user){
        return new UserProfileToPresent(user.getUsername(),user.getEmail(), userProfile.getFirstName(),
                userProfile.getLastName(), userProfile.getDateOfBirth(), userProfile.getDescription(), userProfile.getImageUrl());
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
