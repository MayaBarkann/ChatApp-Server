package serviceTests;

import chatApp.entities.Response;
import chatApp.service.EmailSender;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailSenderTests {
    private EmailSender emailSender;

    @Test
    public void sendEmail_validEmailAddress_returnsSuccessfulResponse(){
        emailSender=new EmailSender();
        Response<String> response = emailSender.sendMail("subject","message","chat.app3000@gmail.com");
        assertTrue(response.isSucceed(), "Email address format is valid but email couldn't be sent.");
    }

    @Test
    public void sendEmail_invalidEmailAddressFormat_returnsFailureResponse(){
        emailSender=new EmailSender();
        Response<String> response = emailSender.sendMail("subject","message","gjfyjgfy");
        assertTrue(!response.isSucceed(), "Email address format is invalid, but method didn't return failure response.");
    }

    @Test
    public void sendEmail_emailAddressNull_returnsFailureResponse(){
        emailSender=new EmailSender();
        Response<String> response = emailSender.sendMail("subject","message",null);
        assertTrue(!response.isSucceed(),"Email address is null, but method didn't return failure response");
    }

    @Test
    public void sendEmail_emailAddressEmpty_returnsFailureResponse(){
        emailSender=new EmailSender();
        Response<String> response = emailSender.sendMail("subject","message","");
        assertTrue(!response.isSucceed(),"Email address is empty, but method didn't return failure response");
    }

    @Test
    public void sendEmail_bodyNull_returnsFailureResponse(){
        emailSender=new EmailSender();
        Response<String> response = emailSender.sendMail("subject",null,"chat.app3000@gmail.com");
        assertTrue(!response.isSucceed(), "Email body is null, but method didn't return failure response");
    }

}
