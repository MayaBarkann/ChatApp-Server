package chatApp.service;

import chatApp.Entities.Response;
import chatApp.Entities.User;
import chatApp.Entities.UserType;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class EmailActivationService {
    @Autowired
    private UserRepository userRepository;
    private final EmailSender emailSender;

    public EmailActivationService(){
        this.emailSender = new EmailSender();
    }

    /**
     * Turns user's userType from NOT_ACTIVATED to REGISTERED in the database, if activationToken is valid.
     *
     * @param activationToken the token used for verifying if the account can undergo the activation process.
     * @return Response object, contains: if action successful - data=user's email, isSucceed=true, message=null; if action failed - data=null, isSucceed=false, message=reason for failure.
     */
    public Response activateUser(String activationToken) {
        String decodedString = decodeToken(activationToken);
        String email = decodedString.split("#")[0];
        String dateTime = decodedString.split("#")[1];

        if (activationToken == null || email==null || dateTime==null) {
            return Response.createFailureResponse(String.format("Invalid activation token"));
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return Response.createFailureResponse(String.format("Email %s doesn't exist in users table", email));
        }
        if (user.getUserType() != UserType.NOT_ACTIVATED) {
            return Response.createFailureResponse(String.format("User with email %s is already activated", email));
        }
        if (LocalDateTime.parse(dateTime).isBefore(LocalDateTime.now())) {
            Response response = Response.createFailureResponse(String.format("Activation token expired. Another activation email was sent to: " + email));
            this.sendActivationEmail(email);
            return response;
        }

        user.setUserType(UserType.REGISTERED);
        userRepository.save(user);
        return Response.createSuccessfulResponse(email);
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
     * Sends an activation email to the given email address, containing the activation link with the activation token.
     *
     * @param toEmail email address that needs to receive the activation email.
     * @return Response object, contains: if action successful - data=user's email, isSucceed=true, message=null; if action failed - data=null, isSucceed=false, message=reason for failure.
     */
    public Response sendActivationEmail(String toEmail){
        String activationLink="http://localhost:8080/user/activate/"+this.newActivationToken(toEmail);
        String content = "To activate your ChatApp account, please press the following link: \n" + activationLink +"\n" + "The link will be active for the next 24 hours.";
        String subject = "Activation Email for ChatApp";
        return emailSender.sendMail(subject,content,toEmail);
    }
}
