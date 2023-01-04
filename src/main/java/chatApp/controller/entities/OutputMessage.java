package chatApp.controller.entities;

import chatApp.entities.Message;

import java.time.format.DateTimeFormatter;

public class OutputMessage {
    private String sender;
    private String content;
    private String time;
    private String receiver;
    private int id;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /**
     * Creates and returns a public OutputMessage- representing in Strings an outgoing message in the Main Chat Room.
     *
     * @param message - Message object, containing data: sender id, content, sending time.
     * @param sender - String, username of the sender.
     * @return - OutputMessage object, containing strings of the following data: sender id, content, date and time the message was sent.
     */
    public static OutputMessage createPublicMessage(Message message, String sender) {
        return new OutputMessage(message.getId(), sender,message.getContent(), message.getTime().format(formatter), null);
    }

    /**
     * Creates and returns a private OutputMessage- representing in Strings a personal outgoing message between two users.
     *
     * @param message - Message object, containing data: sender id, content, sending time.
     * @param sender - String, username of the sender.
     * @return - OutputMessage object, containing strings of the following data: sender id, content, date and time the message was sent.
     */
    public  static OutputMessage createPrivateMessage(Message message, String sender, String receiver) {
        return new OutputMessage(message.getId(), sender,message.getContent(), message.getTime().format(formatter), receiver);
    }

    private OutputMessage(int id,String sender, String content, String time, String receiver) {
        this.id=id;
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.receiver = receiver;
    }

    public int getId() {
        return id;
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
         String result = "Sender: " + sender;
         if(receiver!=null)
             result+=", Receiver: "+receiver;
         result+=", Content: "+content+", Time: "+time;
         return result;
    }
}
