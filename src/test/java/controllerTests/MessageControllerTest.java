package controllerTests;

import chatApp.SpringApp;

import chatApp.controller.MessageController;
import chatApp.entities.*;
import chatApp.repository.MessageRepository;
import chatApp.repository.UserRepository;
import chatApp.service.MessageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.ResponseEntity;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringApp.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MessageControllerTest {
    private static final Logger logger = LogManager.getLogger(MessageControllerTest.class);
    private static List<User> users = new ArrayList<>();
    @Autowired
    private MessageController messageController;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private UserRepository userRepo;

    @BeforeAll
    public void setup() {
        logger.info("setup");
        for (int i = 0; i < 5; i++) {
            users.add(new User("UserNum" + i, "Email" + i + "@gmail.com", "Password"));
        }
        users.get(0).setUserType(UserType.ADMIN);
        users.get(1).setUserType(UserType.REGISTERED);
        users.get(2).setUserType(UserType.GUEST);
        User mutedRegister = users.get(3);
        mutedRegister.setUserType(UserType.REGISTERED);
        mutedRegister.setMessageAbility(MessageAbility.MUTED);
        User mutedGuest = users.get(4);
        mutedGuest.setUserType(UserType.GUEST);
        mutedGuest.setMessageAbility(MessageAbility.MUTED);
        logger.info("users rule 1: admin 2: registered 3: guest 4: mutedRegister 5: mutedGuest");
        logger.info("setup done");
        if (userRepo.findByEmail(users.get(0).getEmail()) == null) {
            users = userRepo.saveAll(users);
        } else {
            for (User user : users) {
                user = userRepo.findByEmail(user.getEmail());
            }
        }
    }

    @Test
    public void send_Message_For_Yourself() {
        logger.info("sendMessage_For_Yourself");
        User user = users.get(0);
        assert_user_can_send_private_message(user.getId(), user.getUsername(), "test: message for yourself.");
    }

    @Test
    public void test_Send_Public_Message() {
        User user = users.get(0);
        String messageContent = "Test message";
        logger.info("send regular message as admin");
        assert_user_can_send_Message_Successfully(user, messageContent);
        logger.info("Admin sent message successfully");
        user = users.get(1);
        logger.info("send regular message as registered");
        assert_user_can_send_Message_Successfully(user, messageContent);
        logger.info("Registered sent message successfully");
        user = users.get(2);
        logger.info("send regular message as guest");
        assert_user_can_send_Message_Successfully(user, messageContent);
        logger.info("Guest sent message successfully");
        user = users.get(3);
        logger.info("trying to send public message as muted registered");
        assert_user_cannot_send_public_message(user, messageContent);
        logger.info("Muted registered cannot send public message");
        user = users.get(4);
        logger.info("trying to send public message as muted guest");
        assert_user_cannot_send_public_message(user, messageContent);
        logger.info("Muted guest cannot send public message");
    }

    public void assert_user_can_send_private_message(int senderId, String recipientName, String messageContent) {
        ResponseEntity<String> PMSendingResponse = messageController.sendPersonalMessage(senderId, recipientName, messageContent);
        Assertions.assertEquals(200, PMSendingResponse.getStatusCodeValue(), "An error occurred while sending the message");
        Assertions.assertEquals("The message was sent successfully.", PMSendingResponse.getBody(), "An error occurred while sending the message");
        Message message = messageRepo.findBySenderIdAndReceiverIdAndMessageType(senderId, userRepo.findByUsername(recipientName).getId(), MessageType.PERSONAL).stream().filter(m -> m.getContent().equals(messageContent)).findFirst().get();
        Assertions.assertEquals(messageContent, message.getContent(), "message was not saved in the database");
        messageRepo.delete(message);
    }

    @Test
    public void test_successfully_Send_Private_Message() {
        logger.info("start - tests for Sending Private Message:");
        User admin = users.get(0);
        User RegisteredUser = users.get(1);
        User mutedRegister = users.get(3);
        String messageContent = "Test message to registered";
        logger.info("test: admin send private message to registered");
        assert_user_can_send_private_message(admin.getId(), RegisteredUser.getUsername(), messageContent);
        logger.info("Admin sent private message successfully");
        logger.info("send private message as registered");
        assert_user_can_send_private_message(RegisteredUser.getId(), admin.getUsername(), messageContent);
        logger.info("Registered sent private message successfully");
        logger.info("send private message as muted registered");
        assert_user_can_send_private_message(mutedRegister.getId(), admin.getUsername(), messageContent);
        logger.info("Muted registered sent private message successfully");
        logger.info("send private message to muted registered");
        assert_user_can_send_private_message(RegisteredUser.getId(), mutedRegister.getUsername(), messageContent);
        logger.info("send private message to muted registered successfully");
    }

    private void assert_user_cannot_send_public_message(User user, String messageContent) {
        ResponseEntity response = messageController.sendPublicMessage(messageContent, user.getId());
        logger.info("user id: " + user.getId() + " user type: " + user.getUserType() + " isMuted: " + user.getMessageAbility());
        logger.info("response: " + response);
        Assertions.assertEquals(401, response.getStatusCodeValue(), "User should not be able to send public message.");
    }
    @Test
    public void test_users_not_allowed_sending_private_message()
    {
        logger.info("start - tests for Sending Private Message:");
        User guest = users.get(2);
        User admin = users.get(0);
        String messageContent = "Test message to registered";
        logger.info("test: guest trying to send private message to registered");
        assert_user_cannot_send_private_message(guest, admin, messageContent);
        logger.info("Guest cannot send private message");
        logger.info("test: user trying to send private message to guest");
        assert_user_cannot_send_private_message(admin, guest, messageContent);
        logger.info("User cannot send private message to guest");
    }
    private void assert_user_can_send_Message_Successfully(User user, String messageContent) {
        ResponseEntity<String> response = messageController.sendPublicMessage(messageContent, user.getId());
        logger.info("user id: " + user.getId() + " user type: " + user.getUserType() + " isMuted: " + user.getMessageAbility());
        logger.info("response: " + response);
        Assertions.assertEquals(messageRepo.findBySenderId(user.getId()).stream().findFirst().get().getContent(), messageContent, "There is a problem with message repository. Message was not saved.");
        Assertions.assertEquals(response.getStatusCode().value(), 200, "User should be able to send public message.");
        Assertions.assertEquals(response.getBody(), "The message was sent successfully.");
        messageRepo.deleteAll(messageRepo.findBySenderId(user.getId()));
    }
    private void assert_user_cannot_send_private_message(User sender, User receiver, String messageContent) {
        ResponseEntity<String> response = messageController.sendPersonalMessage(sender.getId(), receiver.getUsername(), messageContent);
        logger.info("sender id: " + sender.getId() + " sender type: " + sender.getUserType() + " isMuted: " + sender.getMessageAbility());
        logger.info("receiver id: " + receiver.getId() + " receiver type: " + receiver.getUserType() + " isMuted: " + receiver.getMessageAbility());
        logger.info("response: " + response);
        Assertions.assertEquals(401, response.getStatusCodeValue(), "User should not be able to send private message.");
    }
    @AfterAll
    public void close() {
        logger.info("tearDown");
        userRepo.deleteAll(users);
    }
}
