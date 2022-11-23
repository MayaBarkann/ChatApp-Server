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
import java.util.HashMap;
import java.util.Map;

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
    private Map<String,String> decodeToken(String encodedToken){
        byte[] decodedBytes = Base64.getDecoder().decode(encodedToken);
        String decodedString = new String(decodedBytes);
        String[] decodedParts = decodedString.split("#");
        Map<String,String> partsMap = new HashMap<>();
        partsMap.put("email",decodedParts[0]);
        partsMap.put("date",decodedParts[1]);
        return partsMap;
    }

    /**
     * Checks if user can be activated, if he exists in database and isn't activated yet.
     *
     * @param email String user email by which user is searched in user table.
     * @return Response<String> object, if user can be activated returns the email, if user can't be activated returns error message.
     */
    private Response<String> canUserBeActivated(String email){
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return Response.createFailureResponse(String.format("Error activating user: Email %s doesn't exist in users table", email));
        }
        if (user.getUserType() != UserType.NOT_ACTIVATED) {
            return Response.createFailureResponse(String.format("User with email %s is already activated", email));
        }
        return Response.createSuccessfulResponse(email);
    }

    /**
     * Validates the activation token, checks if email for activation exists in database and if token is not expired.
     *
     * @param activationToken String token that needs to be validated.
     * @return Response<String></String>, if action was successful - contains the user's email, if action failed - contains the failure message.
     */
    private Response<String> validateActivationToken(String activationToken){
        Map<String,String> decodedParts = decodeToken(activationToken);
        if (activationToken == null || decodedParts.get("email")==null || decodedParts.get("date")==null) {
            return Response.createFailureResponse(String.format("Invalid activation token"));
        }
        Response<String> response = canUserBeActivated(decodedParts.get("email"));
        if(!response.isSucceed()){
            return response;
        }
        if (LocalDateTime.parse(decodedParts.get("date")).isBefore(LocalDateTime.now())) {
            userRepository.deleteByEmail(decodedParts.get("email"));
            return Response.createFailureResponse(String.format("Activation token expired. Please register again."));
        }
        return Response.createSuccessfulResponse(activationToken);
    }

    /**
     * Creates a user profile in the userprofile table with the given id.
     *
     * @param id the id of the profile to be created.
     * @return Response<UserProfile> object, if action successful - contains the user profile object, if action failed - contains message of failure.
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
     * @return Response<User> object, contains: if action successful - data=activated user data, isSucceed=true, message=null; if action failed - data=null, isSucceed=false, message=reason for failure.
     */
    public Response<User> activateUser(String activationToken) {
        Response<String> responseTokenValidate = validateActivationToken(activationToken);
        if(!responseTokenValidate.isSucceed()) {
            return Response.createFailureResponse(responseTokenValidate.getMessage());
        }
        User user = userRepository.findByEmail(decodeToken(activationToken).get("email"));
        user.setUserType(UserType.REGISTERED);
        userRepository.save(user);
        Response responseProfileCreate = createUserProfile(user.getId());
        if(!responseProfileCreate.isSucceed()){
            return responseProfileCreate;
        }
        return Response.createSuccessfulResponse(user);
    }

    /**
     * Sends an activation email to the given email address, containing the activation link with the activation token.
     *
     * @param toEmail email address that needs to receive the activation email.
     * @return Response<String> object, contains: if action successful - data=user's email, isSucceed=true, message=null; if action failed - data=null, isSucceed=false, message=reason for failure.
     */
    public Response<String> sendActivationEmail(String toEmail){
        Response<String> response = canUserBeActivated(toEmail);
        if(!response.isSucceed()){
            return response;
        }
        String activationLink="http://localhost:8080/user/activate/"+this.newActivationToken(toEmail);
        String content = "To activate your ChatApp account, please press the following link: \n" + activationLink +"\n" + "The link will be active for the next 24 hours.";
        String subject = "Activation Email for ChatApp";
        Response<String> emailSenderResponse = emailSender.sendMail(subject,content,toEmail);
        if(!emailSenderResponse.isSucceed()){
            return Response.createFailureResponse("Failed to send activation email to: " + toEmail + "." + emailSenderResponse.getMessage());
        }
        return Response.createSuccessfulResponse(toEmail);
    }
}
