package chatApp.controller.entities;

import chatApp.entities.Message;

public class OutputMessage {
    private String sender;
    private String content;
    private String time;
    private String receiver;

    /**
     * Creates and returns a public OutputMessage- representing in Strings an outgoing message in the Main Chat Room.
     *
     * @param message - Message object, containing data: sender id, content, sending time.
     * @param sender - String, username of the sender.
     * @return - OutputMessage object, containing strings of the following data: sender id, content, date and time the message was sent.
     */
    public static OutputMessage createPublicMessage(Message message, String sender) {
        return new OutputMessage(sender,message.getContent(), message.getTime().toString(), null);
    }

    /**
     * Creates and returns a private OutputMessage- representing in Strings a personal outgoing message between two users.
     *
     * @param message - Message object, containing data: sender id, content, sending time.
     * @param sender - String, username of the sender.
     * @return - OutputMessage object, containing strings of the following data: sender id, content, date and time the message was sent.
     */
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

    public String getTime() {
        return time;
    }

    public String getReceiver() {
        return receiver;
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
