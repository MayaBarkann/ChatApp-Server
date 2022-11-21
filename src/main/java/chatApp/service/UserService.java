package chatApp.service;

import chatApp.Entities.Response;
import chatApp.Entities.User;
import chatApp.Entities.UserType;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Adds a user to the database if it has a unique email and unique username
     *
     * @param user - the user's data
     * @return a Response, contains: if action succeeded - data=saved user, isSucceeded=true, message=null; if action failed - data = null. isSucceeded = false, message=reason for failure
     */
    public Response<User> addUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return Response.createFailureResponse(String.format("Email %s exists in users table", user.getEmail()));
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return Response.createFailureResponse(String.format("Username %s exists in users table", user.getUsername()));
        }
        if (user.getUsername() == null) {
            return Response.createFailureResponse(String.format("Username can't be null"));
        }
        user.setUserType(UserType.NOT_ACTIVATED);
        userRepository.save(user);

        return Response.createSuccessfulResponse(user);
    }

    /**
     * Searches for a User, with the given id, in the user table.
     *
     * @param id User's id to search for.
     * @return Response object, if user was found - contains the User's object, if user doesn't exist - contains the failure message.
     */
    public Response<User> findUserById(int id){
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            return Response.createSuccessfulResponse(user.get());
        }
        return Response.createFailureResponse("User with id: " + id + "not found");
    }

    public boolean userExistsById(int id){
        return userRepository.findById(id).isPresent();
    }

}
