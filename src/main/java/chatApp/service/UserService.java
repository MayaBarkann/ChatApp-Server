package chatApp.service;


import chatApp.controller.UserController;
import chatApp.entities.*;
import chatApp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

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

    private static final Logger logger = LogManager.getLogger(UserService.class);

    /**
     * Checks if user email and username are not null or empty, and aren't already taken by other users.
     *
     * @param email    String, inputted email to be checked.
     * @param username String, inputted username to be checked.
     * @return Response<String> object, if user input is valid returns the email, if user input is invalid returns error message.
     */
    public Response<String> validateUserInput(String email, String username) {
        logger.trace("UserService validateUserInput method start");
        logger.debug("validateUserInput method params: String email = " + email + ", String username = " + username);
        logger.trace("Checking if email is null or empty.");
        if (email == null || email.isEmpty()) {
            logger.debug("validateUserInput returns failure response (false). Reason: Email can't be null or empty.");
            return Response.createFailureResponse("Email can't be null or empty.");
        }
        logger.trace("Checking if username is null or empty.");
        if (username == null || username.isEmpty()) {
            logger.debug("validateUserInput returns failure response (false). Reason: Username can't be null or empty.");
            return Response.createFailureResponse("Username can't be null or empty.");
        }
        logger.trace("Checking if user with input email already exists in DB.");
        if (userRepository.findByEmail(email) != null) {
            logger.debug("validateUserInput returns failure response (false). Reason: " + email + "is already taken by another user in DB.");
            return Response.createFailureResponse(String.format("Email %s exists in users table", email));
        }
        logger.trace("Checking if user with input username already exists in DB.");
        if (userRepository.findByUsername(username) != null) {
            logger.debug("validateUserInput returns failure response (false). Reason: " + username + "is already taken by another user in DB.");
            return Response.createFailureResponse(String.format("Username %s exists in users table", username));
        }
        logger.debug("validateUserInput returns successful response (true). Email and password and username are valid.");
        return Response.createSuccessfulResponse(email);
    }

    /**
     * Adds a user to the database if it has a unique email and unique username.
     *
     * @param email    - user email inputted during registration.
     * @param password - user password inputted during registration.
     * @param username - user username inputted during registration.
     * @return a Response, contains: if action succeeded - data=saved user, isSucceeded=true, message=null; if action failed - data = null. isSucceeded = false, message=reason for failure
     */
    public Response<User> addUser(String email, String password, String username) {
        logger.trace("UserService addUser method start.");
        logger.debug("addUser method params: String email = " + email + ",String password = " + password + ",String username = " + username);
        logger.trace("addUser method: Checking if param values are valid.");
        Response<String> response = validateUserInput(email, username);
        if (!response.isSucceed()) {
            logger.debug("addUser returns failure response (false). Reason: " + response.getMessage());
            return Response.createFailureResponse(response.getMessage());
        }
        User newUser = User.createNotActivatedUser(username, email, ServiceUtil.encryptPassword(password));
        try {
            newUser = userRepository.save(newUser);
        } catch (DataAccessException e) {
            logger.error("Error occurred while trying to write user data to DB. User wasn't added." + e);
            return Response.createFailureResponse("Error occurred while trying to register user to database.");
        }
        logger.debug("addUser returns successful response (true). user was added to DB.");
        return Response.createSuccessfulResponse(newUser);
    }

    /**
     * Searches for a User, with the given id, in the user table.
     *
     * @param id User's id to search for.
     * @return Response<User> object, if user was found - contains the User's object, if user doesn't exist - contains the failure message.
     */
    public Response<User> findUserById(int id) {
        logger.trace("UserService findUserById method start.");
        logger.debug("findUserById method param: int id = " + id);
        logger.trace("attempting to find user in DB.");
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.debug("findUserById returns successful response with the found user Object: " + user.get());
            return Response.createSuccessfulResponse(user.get());
        }
        logger.debug("findUserById returns successful response with the found user Object: " + user.get());
        return Response.createFailureResponse("User with id: " + id + "not found");
    }

    /**
     * Searches for a User, with the given id, in the user table.
     *
     * @param id - User's id to search for.
     * @return User object response if the user, returns null otherwise.
     */
    private User getUserById(int id) {
        logger.trace("UserService getUserById method start.");
        logger.debug("getUserById method param: int id = " + id);
        logger.debug("getUserById returns User object if user was found or null if user wasn't found.");
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Updates user's data in the DB.
     *
     * @param user Object contains user data that needs to be updated.
     */
    private void updateUser(User user) {
        userRepository.save(user);
    }

    /**
     * toggles the message ability (mute or unmute)
     *
     * @param id - id of user we want to toggle
     * @return Successful response if the user exists and toggling operation succeeded,
     * returns failure response if the user does not exist.
     */
    public Response<User> toggleMessageAbility(int id) {
        User user = getUserById(id);

        if (user == null) {
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
    public Response<User> changeStatus(int id) {
        User user = getUserById(id);
        if (user == null) {
            return Response.createFailureResponse("user does not exists");
        }
        if (user.getUserStatus() == UserStatus.ONLINE) {
            user.setUserStatus(UserStatus.AWAY);
        } else if (user.getUserStatus() == UserStatus.AWAY) {
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
     * @param fromDateOptional - Optional, if present - returns all users registered after the given date.
     * @return List<User>, a list of all the registered users.
     */
    public List<User> getRegisteredUser(Optional<LocalDateTime> fromDateOptional) {
        LocalDateTime fromDate;
        if (!fromDateOptional.isPresent()) fromDate = LocalDateTime.MIN;
        else fromDate = fromDateOptional.get();
        return userRepository.findAll().stream().filter(
                user -> user.getRegisterDateTime() !=null && user.getRegisterDateTime().isAfter(fromDate) && (user.getUserType().equals(UserType.REGISTERED) || user.getUserType().equals(UserType.ADMIN))).collect(Collectors.toList());
    }

    /**
     * Searches for a User's id, with the given username, in the user table.
     *
     * @param name, User's username to search for.
     * @return Response<Integer> object, contains user's id if action successful, otherwise - contains failure message.
     */
    public Response<Integer> getUserIdByName(String name) {
        User user = userRepository.findByUsername(name);
        if (user != null) {
            return Response.createSuccessfulResponse(user.getId());
        }
        return Response.createFailureResponse("User with name: " + name + "not found");
    }

    /**
     * Searches for a User's username, with the given id, in the user table.
     * If user with given id doesn't exist returns null.
     *
     * @param userId, User's id to search by.
     * @return String, user's username if user exists, otherwise - null.
     */
    public String getUserNameById(int userId) {
        User user = getUserById(userId);
        return user != null ? user.getUsername() : null;
    }

    /**
     * Searches for a User's id using the given username, in the user table.
     * If user with given username doesn't exist returns null.
     *
     * @param username, User's username search by.
     * @return Integer, user's id if user exists, otherwise - null.
     */
    public Integer getUserIdByUserName(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? user.getId() : null;
    }
    /**
     * Searches for a User's with the given email, in the user table.
     * If user with given email doesn't exist returns Failure response.
     *
     * @param email, User's email search by.
     * @return Response<User>, user's object if user exists, otherwise - Failure response.
     */
    public Response<User> getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return Response.createSuccessfulResponse(user);
        }
        else return Response.createFailureResponse("User with email: " + email + "not found");
    }


    /**
     * returns user if exists by given id, else return null
     * @param userId user id
     * @return
     */
    public UserType getUserTypeById(int userId) {
        User user = getUserById(userId);
        return user != null ? user.getUserType() : null;
    }


}
