package chatApp.controller.entities;

import chatApp.Entities.Message;
import chatApp.Entities.MessageType;
import java.time.LocalDateTime;


public class OutputMessage {
    private String sender;
    private String content;
    private String time;
    private String receiver;

    public static OutputMessage createPublicMessage(Message message, String sender) {
        return new OutputMessage(sender,message.getContent(), message.getTime().toString(), null);
    }
    public  static OutputMessage createPrivateMessage(Message message, String sender, String receiver) {
        return new OutputMessage(sender,message.getContent(), message.getTime().toString(), receiver);
    }
    private OutputMessage(String sender, String content, String time, String receiver) {
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
