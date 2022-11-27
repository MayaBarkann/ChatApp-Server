package chatApp.controller;

import chatApp.controller.entities.OutputMessage;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class ChatUtil {
    /*
     * This class is used to send messages to the main room and the private channels.
     *
     */
    private final SimpMessagingTemplate template;
    public ChatUtil(SimpMessagingTemplate template) {
        this.template= template;
    }
    public OutputMessage writeMessageToMainRoom(OutputMessage message) {
        System.out.println("Sending message to main room" + message.getContent());
        template.convertAndSend("/topic/mainChat", message);
        return message;
    }

    public OutputMessage writeMessageToPrivateChannel(OutputMessage message,int userId) {
        System.out.println("Sending message to private channel" + message.getContent());
        template.convertAndSendToUser(String.valueOf(userId),"/",message);
        return message;
    }



}