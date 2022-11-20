package chatApp;

import chatApp.Entities.User;
import chatApp.Entities.UserType;
import chatApp.repository.UserRepository;
import chatApp.service.EmailSenderService;
import chatApp.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLDataException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@SpringBootApplication
public class SpringApp {
    public static void main(String[] args) throws MessagingException, GeneralSecurityException, IOException {
        SpringApplication.run(SpringApp.class, args);

        //EmailSenderService.sendMail("new Message","my new message.","itamar124812@gmail.com");
    }
}