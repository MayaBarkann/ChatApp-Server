package chatApp.controller;

import chatApp.controller.entities.OutputMessage;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
/**
 * This class is used to send messages to the main room and the private channels.
 */
public class ChatUtil {
    private final SimpMessagingTemplate template;

    public ChatUtil(SimpMessagingTemplate template) {
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
        template.convertAndSend("/user/"+ message.getReceiver() + "/" ,message);
        template.convertAndSend("/user/"+ message.getSender() + "/",message);
        System.out.println("/user/"+ message.getSender() + "/");
        System.out.println("/user/"+ message.getReceiver() + "/");

        return message;
    }

}