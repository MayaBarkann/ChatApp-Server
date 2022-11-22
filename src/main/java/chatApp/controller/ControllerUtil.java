package chatApp.controller;

import chatApp.Entities.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerUtil {
    public static final Pattern VALID_EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    //public static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\."+"[a-zA-Z0-9_+&*-]+)*@"+"(?:[a-zA-Z0-9-]+\\.)+[a-z"+"A-Z]{2,20}$");
    public static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$");

    /**
     * Checks if user email and password are in valid format.
     *
     * @param email user email inputted during registration.
     * @param password user password inputted during registration
     * @return Response<String> object, if action successful - contains user email, if action failed - contains error message.
     */
    public static Response<String> validateUserCredentials(String email, String password){
        Response<String> isValidResponse = isEmailValid(email);
        if(!isValidResponse.isSucceed()){
            return Response.createFailureResponse(isValidResponse.getMessage());
        }
        //isValidResponse = isPasswordValid(password);
        if(!isValidResponse.isSucceed()){
            return Response.createFailureResponse(isValidResponse.getMessage());
        }
        return Response.createSuccessfulResponse(email);
    }

    /**
     * Checks if user password is in valid format.
     *
     * @param password user password inputted during registration
     * @return Response<String> object, if action successful - contains user password, if action failed - contains error message.
     */
    public static Response<String> isPasswordValid(String password) {
        if (password == null || password.isEmpty()) {
            return Response.createFailureResponse("Password can't be null or empty.");
        }
        Matcher matcher = VALID_PASSWORD_REGEX.matcher(password);
        if(!matcher.find()) {
            return Response.createFailureResponse(passwordConstraints());
        }
        return Response.createSuccessfulResponse(password);
    }

    /**
     * Checks if user email is in valid format.
     *
     * @param email user email inputted during registration.
     * @return Response<String> object, if action successful - contains user email, if action failed - contains error message.
     */
    public static Response<String> isEmailValid(String email) {
        if (email == null || email.isEmpty()) {
            return Response.createFailureResponse("Email can't be null or empty.");
        }
        Matcher matcher = VALID_EMAIL_REGEX.matcher(email);
        if(!matcher.find()){
            return Response.createFailureResponse("Email format is invalid.");
        }
        return Response.createSuccessfulResponse(email);
    }

    /**
     * Checks if user username in valid format.
     *
     * @param username user's username inputted during registration.
     * @return Response<String> object, if action successful - contains user's username, if action failed - contains error message.
     */
    public static Response<String> isUsernameValid(String username) {
        if (username == null || username.isEmpty()) {
            return Response.createFailureResponse("Username can't be null or empty.");
        }
        return Response.createSuccessfulResponse(username);
    }

    /**
     * Returns string that describes valid password conditions.
     *
     * @return String that describes valid password conditions.
     */
    private static String passwordConstraints(){
        return "\n Password must contain:\n" +
                "At least 8 characters and at most 20 characters.\n" +
                "At least one digit.\n" +
                "At least one upper case letter.\n" +
                "At least one lower case letter.\n" +
                "At least one special character which includes !@#$%&*()-+=^.\n" +
                "Must not contain any white spaces.";
    }
}