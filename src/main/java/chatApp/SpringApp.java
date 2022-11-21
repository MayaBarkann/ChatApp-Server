package chatApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;


@SpringBootApplication

public class SpringApp {
    public static void main(String[] args) throws MessagingException, GeneralSecurityException, IOException {
        SpringApplication.run(SpringApp.class, args);

    }
}