package chatApp.entities;

import chatApp.controller.entities.UserProfileToPresent;
import net.bytebuddy.asm.Advice;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "userProfile")
public class UserProfile {
    @Id  //TODO: how do i make sure that the id exists in the user repository
    private int id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String description;
    private boolean isPublic;
    private String imageUrl;

    public UserProfile() { }

//    public static UserProfile createUserProfileFromIdAndUserProfileToPresent(int id, boolean isPublic, UserProfileToPresent userProfileToPresent){
//        UserProfile userProfile = new UserProfile();
//        userProfile.id = id;
//        userProfile.firstName = userProfileToPresent.getFirstName();
//        userProfile.lastName = userProfileToPresent.getLastName();
//        userProfile.dateOfBirth = userProfileToPresent.getDateOfBirth();
//        userProfile.description = userProfileToPresent.getDescription();
//        userProfile.isPublic = isPublic;
//        userProfile.imageUrl = userProfileToPresent.getImageUrl();
//        return userProfile;
//
//    }

    public UserProfile(int id) {
        this.id = id;
    }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String photoUrl) { this.imageUrl = photoUrl; }

    public boolean isPublic() { return isPublic; }

    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public int getId() {
        return id;
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

}
