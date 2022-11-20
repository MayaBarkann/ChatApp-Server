package chatApp.repository;

import chatApp.Entities.Message;
import chatApp.Entities.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    public List<Message> findBySenderIdAndReceiverIdAndMessageType(int senderId, int reciverId,MessageType type);
    public List<Message> findByMessageType(MessageType type);
    public List<Message> findBySenderId(int senderId);
    public List<Message> findByReceiverId(int reciverId);
}
