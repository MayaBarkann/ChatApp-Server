package chatApp.Entities;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Objects;

public class Message {
    private static final int MainRoomId=-1;
    Message()
    {

    }
    private Message(int senderId,int receiverId,String content,LocalDate sendTime)
    {
        this.senderId=senderId;
        this.receiverId=receiverId;
        this.content=content;
        this.time=sendTime;
    }
    public static Message createPersonalMessage(int senderId,int receiverId,String content,LocalDate sendTime)
    {
        Message message = new Message(senderId, receiverId, content, sendTime);
        message.messageType =MessageType.PERSONAL;
        return message;
    }
    public static Message createMainRoomMessage(int senderId,String content,LocalDate sendTime)
    {
        Message message = new Message(senderId, MainRoomId, content, sendTime);
        message.messageType=MessageType.MAIN_ROOM;
        return message;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(nullable = false)
    private String content;
    private int senderId; //TODO: add not null to column?
    private int receiverId;
    private LocalDate time; //TODO: add not null to column?
    private MessageType messageType;

    public int getId() {
        return id;
    }
    public MessageType getType(){return messageType;}

    public String getContent() {
        return content;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public LocalDate getTime() {
        return time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setTime(LocalDate time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", time=" + time +
                '}';
    }
}
