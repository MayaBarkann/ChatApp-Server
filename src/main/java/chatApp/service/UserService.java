package chatApp.service;

import chatApp.Entities.Response;
import chatApp.Entities.User;
import chatApp.Entities.UserType;
import chatApp.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Checks if user email and username are not null or empty, and aren't already taken by other users.
     *
     * @param user User object, contains data to be validated.
     * @return Response<User> object, if user data is valid returns the user object, if user data is invalid returns error message.
     */
    private Response<User> validateUserInput(User user){
        if (user == null) {
            return Response.createFailureResponse("User can't be null");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return Response.createFailureResponse("Email can't be null or empty.");
        }
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return Response.createFailureResponse("Username can't be null or empty.");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return Response.createFailureResponse(String.format("Email %s exists in users table", user.getEmail()));
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return Response.createFailureResponse(String.format("Username %s exists in users table", user.getUsername()));
        }
        return Response.createSuccessfulResponse(user);
    }

    /**
     * Adds a user to the database if it has a unique email and unique username.
     *
     * @param user - the user's data
     * @return a Response, contains: if action succeeded - data=saved user, isSucceeded=true, message=null; if action failed - data = null. isSucceeded = false, message=reason for failure
     */
    public Response<User> addUser(User user) {
        Response<User> response = validateUserInput(user);
        if(!response.isSucceed()){
            return Response.createFailureResponse(response.getMessage());
        }
        user.setPassword(ServiceUtil.encryptPassword(user.getPassword()));
        user.setUserType(UserType.NOT_ACTIVATED);
        userRepository.save(user);
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

}
