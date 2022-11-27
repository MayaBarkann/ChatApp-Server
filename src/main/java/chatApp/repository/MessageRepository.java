package chatApp.repository;

import chatApp.entities.Message;
import chatApp.entities.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findBySenderIdAndReceiverIdAndMessageType(int senderId, int reciverId, MessageType type);
    List<Message> findByMessageType(MessageType type);
    List<Message> findBySenderIdOrReceiverIdAndMessageType(int senderId, int reciverId, MessageType type);
    List<Message> findBySenderId(int senderId);
    List<Message> findByReceiverId(int reciverId);
}
