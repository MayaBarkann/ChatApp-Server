package chatApp.controller;

import chatApp.controller.entities.OutputMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class ChatUtil {
    /*
     * This class is used to send messages to the main room and the private channels.
     *
     */
    public ChatUtil() {
    }

    @SendTo("/topic/mainChat")
    public OutputMessage writeMessageToMainRoom(OutputMessage message) {
        System.out.println("Sending message to main room" + message.getContent());
        return message;
    }
    @SendToUser("")
    public OutputMessage writeMessageToPrivateChannel(OutputMessage message) {
        return message;
    }


    static class HelloMessage {

        private String name;

        public HelloMessage() {
        }

        public HelloMessage(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "HelloMessage{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}