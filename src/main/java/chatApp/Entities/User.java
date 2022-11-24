package chatApp.Entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @Enumerated(EnumType.STRING)
    private MessageAbility messageAbility = MessageAbility.UNMUTED;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    private LocalDateTime registerDateTime;
    private LocalDateTime lastLoginDateTime;

    @Transient
    private String authToken;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setMessageAbility(MessageAbility messageAbility) {
        this.messageAbility = messageAbility;
    }

    public MessageAbility getMessageAbility() {
        return messageAbility;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public LocalDateTime getRegisterDateTime() {
        return registerDateTime;
    }

    public LocalDateTime getLastLoginDateTime() {
        return lastLoginDateTime;
    }

    public void setLastLoginDateTime(LocalDateTime lastLoginDateTime) {
        this.lastLoginDateTime = lastLoginDateTime;
    }

    public void setRegisterDateTime(LocalDateTime registerDateTime) {
        this.registerDateTime = registerDateTime;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (!Objects.equals(username, user.username)) return false;
        if (!Objects.equals(email, user.email)) return false;
        return Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                '}';
    }

    public void toggleMessageAbility() {
        if (this.messageAbility == MessageAbility.MUTED) {
            this.messageAbility = MessageAbility.UNMUTED;
        } else {
            this.messageAbility = MessageAbility.MUTED;
        }
    }
}