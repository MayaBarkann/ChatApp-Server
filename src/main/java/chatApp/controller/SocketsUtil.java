package chatApp.controller;

import chatApp.controller.entities.OutputMessage;

import chatApp.controller.entities.UserToPresent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
/**
 * This class is used to send messages to the main room and the private channels.
 */
public class SocketsUtil {
    private final SimpMessagingTemplate template;

    public SocketsUtil(SimpMessagingTemplate template) {
        this.template= template;
    }

    /**
     * Sends message to Main Chat Room.
     *
     * @param message - OutputMessage object, contains: text message to be sent, sender, receiver and timestamp
     * @return OutputMessage object, the message that was sent.
     */
    public OutputMessage writeMessageToMainRoom(OutputMessage message) {
        System.out.println("Sending message to main room" + message.getContent());
        template.convertAndSend("/topic/mainChat", message);
        return message;
    }

    /**
     * Sends message to private chat channel.
     *
     * @param message - OutputMessage object, contains: text message to be sent, sender, receiver and timestamp
     * @return OutputMessage object, the message that was sent.
     */
    public OutputMessage writeMessageToPrivateChannel(OutputMessage message) {
        System.out.println("Sending message to private channel" + message.getContent());
        template.convertAndSendToUser(message.getReceiver(),"/",message);
        template.convertAndSendToUser(message.getReceiver(),"/",message);
        return message;
    }
    /**
     * notify all other users when a new user is activated.
     *
     * @param userToPresent - UserToPresent object, contains: user name, user email, user status
     * @return UserToPresent object, the user that was activated.
     */
    public UserToPresent writeUserToPresent(UserToPresent userToPresent) {
        System.out.println("Sending user to present" + userToPresent.getUsername());
        template.convertAndSend("/newUsers", userToPresent);
        return userToPresent;
    }

}