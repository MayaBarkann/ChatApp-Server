package chatApp.entities;

import javax.persistence.*;
import javax.swing.*;
import java.time.LocalDateTime;
import java.util.Objects;
@Entity
@Table(name="Message")
public class Message {
    private static final int MainRoomId=-1;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(nullable = false)
    private String content;
    private int senderId; //TODO: add not null to column?
    private int receiverId;
    private LocalDateTime time; //TODO: add not null to column?
    @Enumerated(EnumType.STRING)
    private MessageType messageType;
    public Message() {

    }//Don't use.
    private Message(int senderId,int receiverId,String content,LocalDateTime sendTime)
    {
        this.senderId=senderId;
        this.receiverId=receiverId;
        this.content=content;
        this.time=sendTime;
    }
    public static Message createPersonalMessage(int senderId,int receiverId,String content,LocalDateTime sendTime)
    {
        Message message = new Message(senderId, receiverId, content, sendTime);
        message.messageType =MessageType.PERSONAL;
        return message;
    }
    public static Message createMainRoomMessage(int senderId,String content,LocalDateTime sendTime)
    {
        Message message = new Message(senderId, MainRoomId, content, sendTime);
        message.messageType=MessageType.MAIN_ROOM;
        return message;
    }


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

    public LocalDateTime getTime() {
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

    public void setTime(LocalDateTime time) {
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
