package chatApp.emailVerification;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailVerification {
    public static void main(String[] args) throws MessagingException {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");

        mailSender.setJavaMailProperties(properties);

        mailSender.setUsername("maya.barkan@post.idc.ac.il");
        mailSender.setPassword("maya2005");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("sender@test.com");
        helper.setSubject("subject");
        helper.setText("verification"); // true to activate multipart
        helper.addTo("mayabarkann@gmail.com");
        mailSender.send(message);

    }

}
