package chatApp;

import chatApp.service.UserProfileService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootApplication

public class SpringApp {
    public static void main(String[] args){
        SpringApplication.run(SpringApp.class, args);

    }

}