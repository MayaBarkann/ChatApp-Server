package chatApp.service;

import chatApp.controller.entities.UserToPresent;
import chatApp.entities.*;
import chatApp.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Checks if user email and username are not null or empty, and aren't already taken by other users.
     *
     * @param email String, inputted email to be checked.
     * @param username String, inputted username to be checked.
     * @return Response<String> object, if user input is valid returns the email, if user input is invalid returns error message.
     */
    public Response<String> validateUserInput(String email, String username){
        if (email == null || email.isEmpty()) {
            return Response.createFailureResponse("Email can't be null or empty.");
        }
        if (username == null || username.isEmpty()) {
            return Response.createFailureResponse("Username can't be null or empty.");
        }
        if (userRepository.findByEmail(email) != null) {
            return Response.createFailureResponse(String.format("Email %s exists in users table", email));
        }
        if (userRepository.findByUsername(username) != null) {
            return Response.createFailureResponse(String.format("Username %s exists in users table", username));
        }
        return Response.createSuccessfulResponse(email);
    }

    /**
     * Adds a user to the database if it has a unique email and unique username.
     *
     * @param email - user email inputted during registration.
     * @param password - user password inputted during registration.
     * @param username - user username inputted during registration.
     * @return a Response, contains: if action succeeded - data=saved user, isSucceeded=true, message=null; if action failed - data = null. isSucceeded = false, message=reason for failure
     */
    public Response<User> addUser(String email,String password,String username) {
        Response<String> response = validateUserInput(email,username);
        if(!response.isSucceed()){
            return Response.createFailureResponse(response.getMessage());
        }
        User newUser = new User(username,email,password);
        newUser.setPassword(ServiceUtil.encryptPassword(newUser.getPassword()));
        newUser.setUserType(UserType.NOT_ACTIVATED);
        newUser.setUserStatus(UserStatus.OFFLINE);
        newUser.setMessageAbility(MessageAbility.UNMUTED);
        newUser.setRegisterDateTime(LocalDateTime.now());
        User user = userRepository.save(newUser);
        return Response.createSuccessfulResponse(user);
    }

    /**
     * Searches for a User, with the given id, in the user table.
     *
     * @param id User's id to search for.
     * @return Response<User> object, if user was found - contains the User's object, if user doesn't exist - contains the failure message.
     */
    public Response<User> findUserById(int id){
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            return Response.createSuccessfulResponse(user.get());
        }
        return Response.createFailureResponse("User with id: " + id + "not found");
    }

    /**
     * Toggles the message ability of the user(mute or unmute).
     *
     * @param id - id of user we want to toggle
     * @return Successful response if the user exists and toggling operation succeeded,
     * returns failure response if the user does not exist.
     */
    private User getUserById(int id){
        return userRepository.findById(id).orElse(null);
    }

    /**
     *
     * @param user Object contains user data that needs to be updated.
     */
    private void updateUser(User user){
        userRepository.save(user);
    }

    /**
     * toggles the message ability (mute or unmute)
     * @param id - id of user we want to toggle
     * @return Successful response if the user exists and toggling operation succeeded,
     * returns failure response if the user does not exist.
     */
    public Response<User> toggleMessageAbility(int id){
        User user = getUserById(id);

        if(user == null){
            return Response.createFailureResponse("user does not exists");
        }
        user.toggleMessageAbility();
        updateUser(user);

        return Response.createSuccessfulResponse(user);
    }

    /**
     * Changes user status from online to away, or from away to online.
     *
     * @param id - int, id of user whose status we want to change.
     * @return Successful response if the user exists and change status operation succeeded,
     * returns failure response if the user does not exist or user is offline.
     */
    public Response<User> changeStatus(int id){
        User user = getUserById(id);
        if(user == null){
            return Response.createFailureResponse("user does not exists");
        }
        if(user.getUserStatus() == UserStatus.ONLINE){
            user.setUserStatus(UserStatus.AWAY);
        } else if(user.getUserStatus() == UserStatus.AWAY){
            user.setUserStatus(UserStatus.ONLINE);
        } else {
            return Response.createFailureResponse("can not change status to user");
        }
        updateUser(user);
        return Response.createSuccessfulResponse(user);
    }

    /**
     * Delete user with given email from table user in DB.
     *
     * @param email String user's email to delete him by
     */
    public void deleteUserByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    /**
     * Finds and returns a list of all the Registered and Admin users.
     *
     * @return List<User>, a list of all the registered users.
     */
    public List<User> getAllRegisteredUser(){
        return userRepository.findAll().stream().filter(
                user->user.getUserType().equals(UserType.REGISTERED) || user.getUserType().equals(UserType.ADMIN)).collect(Collectors.toList());
    }

    public String getUserNameById(int userId){
        User user = getUserById(userId);
        if ( user != null) {
            return user.getUsername();
        }
        return null;
    }
}
