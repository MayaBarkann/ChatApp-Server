package chatApp.service;

import chatApp.Entities.Response;
import chatApp.Entities.User;
import chatApp.Entities.UserProfile;
import chatApp.Entities.UserType;
import chatApp.repository.UserProfileRepository;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class UserActivationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    private final EmailSender emailSender;

    public UserActivationService(){
        this.emailSender = new EmailSender();
    }

    /**
     * Encodes the given String using Base64.
     *
     * @param email email address as String used in encoding.
     * @return encoded String
     */
    private String newActivationToken(String email){
        String strToEncode = email+"#"+ LocalDateTime.now().plusHours(24);
        return Base64.getEncoder().encodeToString(strToEncode.getBytes());
    }

    /**
     * Decodes the given String using Base64.
     *
     * @param encodedToken the encoded String that needs to be decoded.
     * @return Decoded String
     */
    private String decodeToken(String encodedToken){
        byte[] decodedBytes = Base64.getDecoder().decode(encodedToken);
        return new String(decodedBytes);
    }

    /**
     * Validates the activation token, checks if email for activation exists in database and if token is not expired.
     *
     * @param activationToken String token that needs to be validated.
     * @return Response, if action was successful - contains the user's email, if action failed - contains the failure message.
     */
    private Response<String> validateActivationToken(String activationToken){
        String decodedString = decodeToken(activationToken);
        String email = decodedString.split("#")[0];
        String dateTime = decodedString.split("#")[1];

        if (activationToken == null || email==null || dateTime==null) {
            return Response.createFailureResponse(String.format("Invalid activation token"));
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return Response.createFailureResponse(String.format("Error validating token: Email %s doesn't exist in users table", email));
        }
        if (user.getUserType() != UserType.NOT_ACTIVATED) {
            return Response.createFailureResponse(String.format("User with email %s is already activated", email));
        }
        if (LocalDateTime.parse(dateTime).isBefore(LocalDateTime.now())) {
            return Response.createFailureResponse(String.format("Activation token expired"));
        }
        return Response.createSuccessfulResponse(activationToken);
    }

    /**
     * Creates a user profile in the userprofile table with the given id.
     *
     * @param id the id of the profile to be created.
     * @return Response object, if action successful - contains the user profile object, if action failed - contains message of failure.
     */
    private Response<UserProfile> createUserProfile(int id){
        UserProfile userProfile = null;
        try {
            userProfile = new UserProfile(id);
            userProfileRepository.save(userProfile);
        }catch(Exception e){
            return Response.createFailureResponse("Failed to create user profile with id: " + id);
        }
        return Response.createSuccessfulResponse(userProfile);
    }

    /**
     * Turns user's userType from NOT_ACTIVATED to REGISTERED in the database, if the activation token is valid.
     *
     * @param activationToken the token used for verifying if the account can undergo the activation process.
     * @return Response object, contains: if action successful - data=activated user data, isSucceed=true, message=null; if action failed - data=null, isSucceed=false, message=reason for failure.
     */
    public Response<User> activateUser(String activationToken) {
        Response responseTokenValidate = validateActivationToken(activationToken);
        String decodedString = decodeToken(activationToken);
        String email = decodedString.split("#")[0];
        if(!responseTokenValidate.isSucceed()) {
            return Response.createFailureResponse(responseTokenValidate.getMessage());
        }
        User user = userRepository.findByEmail(email);
        user.setUserType(UserType.REGISTERED);
        userRepository.save(user);
        Response responseProfileCreate = createUserProfile(user.getId());
        if(!responseProfileCreate.isSucceed()){
            return Response.createFailureResponse(responseProfileCreate.getMessage());
        }
        return Response.createSuccessfulResponse(user);
    }

    /**
     * Sends an activation email to the given email address, containing the activation link with the activation token.
     *
     * @param toEmail email address that needs to receive the activation email.
     * @return Response object, contains: if action successful - data=user's email, isSucceed=true, message=null; if action failed - data=null, isSucceed=false, message=reason for failure.
     */
    public Response<String> sendActivationEmail(String toEmail){
        String activationLink="http://localhost:8080/user/activate/"+this.newActivationToken(toEmail);
        String content = "To activate your ChatApp account, please press the following link: \n" + activationLink +"\n" + "The link will be active for the next 24 hours.";
        String subject = "Activation Email for ChatApp";
        Response emailSenderResponse = emailSender.sendMail(subject,content,toEmail);
        if(!emailSenderResponse.isSucceed()){
            return Response.createFailureResponse("Failed to send activation email to: " + toEmail + "." + emailSenderResponse.getMessage());
        }
        return Response.createSuccessfulResponse(toEmail);
    }
}
