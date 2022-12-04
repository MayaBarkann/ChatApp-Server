package chatApp.service;

import chatApp.entities.*;
import chatApp.repository.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final Map<Integer, String> authTokensMap;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        authTokensMap = new HashMap<>();
    }

    /**
     * Creates String authentication token. Consists of user id, random string, and current time in milliseconds.
     *
     * @param id int user id.
     * @return Response<String> object, if action successful - contains the authentication token as String, otherwise -  contains error message.
     */
    private Response<String> createAuthToken(int id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            return Response.createFailureResponse("Error during authentication token creation: User with id: " + id + "doesn't exist in database.");
        }
        long changedId = ServiceUtil.encodeWithReversibleFunction(id);
        String encodedId = Base64.getEncoder().encodeToString(String.valueOf(changedId).getBytes());
        long changedTime = ServiceUtil.encodeWithReversibleFunction(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        String encodedTime = Base64.getEncoder().encodeToString(String.valueOf(changedTime).getBytes());
        String authToken = encodedId + "-" + RandomString.make(64) + "-" + encodedTime;
        return Response.createSuccessfulResponse(authToken);
    }

    /**
     * Checks if the email and password inputted by the user are correct.
     *
     * @param email User's email provided on login.
     * @param password User's password provided on login.
     * @return Response<User> object, if action successful - contains the user's data, otherwise - contains error message.
     */
    private Response<User> loginCredentialsValidate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return Response.createFailureResponse("Wrong input: email doesn't exist in database.");
        }
        if (user.getUserType()==UserType.NOT_ACTIVATED) {
            return Response.createFailureResponse("Before login account must be activated via the activation email.");
        }
        if (!ServiceUtil.isPasswordCorrect(user.getPassword(), password)) {
            return Response.createFailureResponse("Wrong password.");
        }
        return Response.createSuccessfulResponse(user);
    }

    /**
     * Creates authentication token on user login, sets user as online in database.
     *
     * @param email User's email provided on login.
     * @param password User's password provided on login.
     * @return Response<String>, if action successful contains the authentication token, otherwise - contains error message.
     */
    public Response<String> userLogin(String email, String password) {

        Response<User> validateResponse = this.loginCredentialsValidate(email, password);
        if (!validateResponse.isSucceed()) {
            System.out.println("this");
            return Response.createFailureResponse(validateResponse.getMessage());
        }
        User validatedUser = validateResponse.getData();
        Response<String> createTokenResponse = this.createAuthToken(validatedUser.getId());
        if (!createTokenResponse.isSucceed()) {
            return Response.createFailureResponse("Error during login. " + createTokenResponse.getMessage());
        }
        validatedUser.setUserStatus(UserStatus.ONLINE);
        validatedUser.setMessageAbility(MessageAbility.UNMUTED);
        validatedUser.setLastLoginDateTime(LocalDateTime.now());
        try {
            userRepository.save(validatedUser);
        } catch (DataAccessException e) {
            return Response.createFailureResponse("Error during login: Error occurred during Registered User update in database.");
        }
        this.authTokensMap.put(validatedUser.getId(), createTokenResponse.getData());
        return Response.createSuccessfulResponse(createTokenResponse.getData());
    }

    /**
     * Creates a guest user and adds him to the database if he enters a unique username.
     *
     * @param username String username inputted by guest user.
     * @return Response<String>, if action successful contains user's authentication token, otherwise - contains error message.
     */
    public Response<String> createGuestUser(String username) {
        User temp = userRepository.findByUsername(username);
        if (temp!=null) {
            return Response.createFailureResponse("Can't join chat: username " + username + " is taken.");
        }
        User guestUser = User.createGuestUser(username);
        try {
            guestUser = userRepository.save(guestUser);
        } catch (DataAccessException e) {
            return Response.createFailureResponse("Error occurred while trying to add guest user to database.");
        }
        String authToken = this.createAuthToken(guestUser.getId()).getData();
        authTokensMap.put(guestUser.getId(),authToken);
        return Response.createSuccessfulResponse(authToken);
    }

    /**
     * Performs user logout. Erases user's authentication token.
     * Erases Guest User from database. Sets Registered User as Offline in database.
     *
     * @param userId int, id of user that wants to log out.
     * @return Response<String> object, if action succeeded - holds user's username, otherwise - holds error message.
     */
    public Response<String> userLogout(int userId) {
        authTokensMap.remove(userId);
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return Response.createFailureResponse("Error occurred during logout: user doesn't exist in database.");
        }
        if (user.get().getUserType() == UserType.GUEST) {
            try {
                userRepository.deleteById(userId);
            }catch (DataAccessException e){
                return Response.createFailureResponse("Error occurred during logout: guest user with given id couldn't be deleted from database.");
            }
            return Response.createSuccessfulResponse(user.get().getUsername());
        }
        user.get().setUserStatus(UserStatus.OFFLINE);
        userRepository.save(user.get());
        return Response.createSuccessfulResponse(user.get().getUsername());
    }

    /**
     * Checks if given token is the right user authentication token.
     *
     * @param authToken String, user's authentication token.
     * @return Response<String>, if token is correct - holds the token, otherwise - holds error message.
     */
    public Response<Integer> isTokenCorrect(String authToken) {
        int userIdByToken = getIdFromAuthToken(authToken);
        if(userIdByToken==-1){
            return Response.createFailureResponse("Error occurred during logout: token format is invalid.");
        }
        if(authTokensMap.get(userIdByToken)!=null) {
            if (!authTokensMap.get(userIdByToken).equals(authToken)) {
                return Response.createFailureResponse("Wrong token.");
            }
        }else{
            return Response.createFailureResponse("User has no token.");
        }
        return Response.createSuccessfulResponse(getIdFromAuthToken(authToken));
    }

    /**
     * Extracts user id from the authentication token.
     *
     * @param authToken String token that authenticates the user.
     * @return user id as int if token contains user id, otherwise returns -1.
     */
    private int getIdFromAuthToken(String authToken) {
        if (!ServiceUtil.isTokenFormatValid(authToken)) {
            return -1;
        }
        String decodedString = new String(Base64.getDecoder().decode(authToken.split("-")[0]));
        return (int)ServiceUtil.decodeWithReversibleFunction(Long.parseLong(decodedString));
    }

    /**
     * Extracts the time when given authentication token was created during user's login.
     *
     * @param authToken String, user authentication token.
     * @return LocalDateTime when the authentication token what created.
     */
    private Optional<LocalDateTime> getTimeFromAuthToken(String authToken) {
        if (!ServiceUtil.isTokenFormatValid(authToken)) {
            return Optional.empty();
        }
        String decodedString = new String(Base64.getDecoder().decode(authToken.split("-")[2]));
        long decodedTime= ServiceUtil.decodeWithReversibleFunction(Long.parseLong(decodedString));
        return Optional.of(LocalDateTime.ofInstant(Instant.ofEpochSecond(decodedTime), ZoneId.systemDefault()));
    }

    /**
     * Finds user's authentication token by given user id.
     *
     * @param id int id of the user.
     * @return Optional<String> authentication token of the user with given id.
     */
    public Optional<String> getTokenById(int id) {
        String authToken = authTokensMap.get(id);
        if (authToken == null) {
            return Optional.empty();
        }
        return Optional.of(authToken);
    }

    /**
     * Checks if a user with a given id exists in the database.
     *
     * @param id, user's id to search for.
     * @return boolean, true - if user with given id exists in database, false - otherwise.
     */
    public boolean isUserExist(int id) {
        return userRepository.findById(id).isPresent();
    }
}
