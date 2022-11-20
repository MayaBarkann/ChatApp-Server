package chatApp.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailActivationService {

    private final EmailSender emailSender;

    public EmailActivationService(){
        this.emailSender=new EmailSender();
    }

    private String newActivationToken(String email){
        String temp = email+"#"+ LocalDate.now();

        return "";
    }

    public void sendActivationMail(String toEmail){
        String activationLink="http://localhost:8080"+this.newActivationToken(toEmail);
        String content = "To activate your ChatApp account, please press the following link: \n" + activationLink;
        String subject = "Activation Email for ChatApp";
        emailSender.sendMail(subject,content,toEmail);
    }


}
