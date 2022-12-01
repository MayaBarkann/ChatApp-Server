package chatApp.controller.entities;

import chatApp.entities.User;
import chatApp.entities.UserProfile;
import java.time.LocalDate;
import java.util.Objects;

public class UserProfileToPresent {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String description;
    private String imageUrl;
    private boolean isPublic;

    public UserProfileToPresent(){}

    public static UserProfileToPresent createFromUserProfileAndUser(UserProfile userProfile, User user){

        return new UserProfileToPresent(user.getUsername(),user.getEmail(), userProfile.getFirstName(),
                userProfile.getLastName(), userProfile.getDateOfBirth(), userProfile.getDescription(), userProfile.getImageUrl(), userProfile.isPublic());
    }

    private UserProfileToPresent(String username, String email, String firstName, String lastName, LocalDate dateOfBirth, String description, String imageUrl, boolean isPublic) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isPublic = isPublic;
    }

    public boolean isPublic() { return isPublic; }

    public void setPublic(boolean aPublic) { isPublic = aPublic; }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfileToPresent that = (UserProfileToPresent) o;
        return isPublic == that.isPublic &&
                Objects.equals(username, that.username) &&
                Objects.equals(email, that.email) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(dateOfBirth, that.dateOfBirth) &&
                Objects.equals(description, that.description) &&
                Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, firstName, lastName, dateOfBirth, description, imageUrl, isPublic);
    }
}
