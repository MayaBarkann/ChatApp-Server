package chatApp.service;

import chatApp.Entities.*;
import chatApp.repository.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

//TODO: check all methods
@Service
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     *
     * @param id
     * @return
     */
    private Response<String> createAuthToken(int id) {
        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()){
            return Response.createFailureResponse("Error during authentication token creation: User with id: "+ id + "doesn't exist in database.");
        }
        String authToken = id + "-" + RandomString.make(64);
        user.get().setAuthToken(authToken);
        return Response.createSuccessfulResponse(authToken);
    }

    public Response<String> userLogin(String email, String password) {
        Response<User> validateResponse = this.loginCredentialsValidate(email,password);
        if(!validateResponse.isSucceed()){
            return Response.createFailureResponse(validateResponse.getMessage());
        }
        User validatedUser = validateResponse.getData();
        Response<String> createTokenResponse = this.createAuthToken(validatedUser.getId());
        if(!createTokenResponse.isSucceed()){
            Response.createFailureResponse("Couldn't login: " + createTokenResponse.getMessage());
        }
        validatedUser.setAuthToken(createTokenResponse.getData());
        validatedUser.setUserStatus(UserStatus.ONLINE);
        validatedUser.setMessageAbility(MessageAbility.UNMUTED);
        validatedUser.setLastLoginDateTime(LocalDateTime.now());
        try {
            userRepository.save(validatedUser);
        }catch(DataAccessException e){
            Response.createFailureResponse("Couldn't login: Error occurred during Registered User update in database.");
        }
        validatedUser.setAuthToken(createTokenResponse.getData());
        return Response.createSuccessfulResponse(createTokenResponse.getData());
    }

    private Response<User> loginCredentialsValidate(String email, String password) { //TODO: finish method
        User user = userRepository.findByEmail(email);
        //check if email exists
        //check if password and email match
        return Response.createSuccessfulResponse(user);
    }

    public Response<String> createGuestUser(String username) { //TODO:check method
        String generatedGuestEmail = UUID.randomUUID().toString().replace("-","") + "@chatapp.guest";
        User guestUser = new User("Guest-"+ username,generatedGuestEmail, null);
        guestUser.setUserType(UserType.GUEST);
        guestUser.setUserStatus(UserStatus.ONLINE);
        guestUser.setMessageAbility(MessageAbility.UNMUTED);
        try {
            userRepository.save(guestUser);
        }catch(DataAccessException e){
            Response.createFailureResponse("Error occurred while trying to add guest user to database.");
        }
        guestUser.setAuthToken(this.createAuthToken(guestUser.getId()).getData());
        return Response.createSuccessfulResponse(guestUser.getAuthToken());
    }

    public Response<String> guestLogout(String authToken) {
        //extract id from token
        //find user id
        String username =""; //TODO: find actual username;
        //null user token
        //delete user from database
        return Response.createSuccessfulResponse("User " + username + " successfully logged out.");
    }

    public int getIdFromAuthToken(String authToken){ //TODO: write method
        return 1;
    }
}
