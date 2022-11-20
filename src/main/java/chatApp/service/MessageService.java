package chatApp.service;

import chatApp.Entities.Message;
import chatApp.Entities.MessageType;
import chatApp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private MessageRepository messageRepo;

    @Autowired
    public MessageService(MessageRepository repository) {
        messageRepo = repository;
    }

    public Message createPrivateMessage(int senderId, int reciverId, LocalDate sendTime, String content) {
        Message personalMessage = Message.createPersonalMessage(senderId, reciverId, content, sendTime);
        messageRepo.save(personalMessage);
        return personalMessage;
    }

    public Message createPublicMessage(int senderId, LocalDate sendTime, String content) {
        Message mainRoomMessage = Message.createMainRoomMessage(senderId, content, sendTime);
        messageRepo.save(mainRoomMessage);
        return mainRoomMessage;
    }
    public List<Message> loadPublicMessages(LocalDate startDate)
    {
        return loadPublicMessages(startDate,LocalDate.now());
    }
    public List<Message> loadPublicMessages(LocalDate startDate,LocalDate endDate)
    {
        return messageRepo.findByMessageType(MessageType.MAIN_ROOM).stream()
                .filter(message->message.getTime().isAfter(startDate) && message.getTime().isAfter(endDate))
                .collect(Collectors.toList());
    }
    public List<Message> loadPublicMessages()
    {
        return messageRepo.findByMessageType(MessageType.MAIN_ROOM);
    }
    public File exportUserMessages(String fileName,int userId,LocalDate startDate,LocalDate endDate)
    {
        return null;
    }
    public List<Message> getChannelMessages(int senderId,int reciverId)
    {
        return messageRepo.findBySenderIdAndReceiverIdAAndMessageType(senderId,reciverId,MessageType.PERSONAL);
    }
}
