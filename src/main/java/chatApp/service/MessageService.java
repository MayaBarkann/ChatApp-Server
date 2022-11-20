package chatApp.service;

import chatApp.Entities.Message;
import chatApp.Entities.MessageType;
import chatApp.Entities.Response;
import chatApp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private MessageRepository messageRepo;

    @Autowired
    public MessageService(MessageRepository repository) {
        messageRepo = repository;
    }

    public Message createPrivateMessage(int senderId, int reciverId, String content) {
        Message personalMessage = Message.createPersonalMessage(senderId, reciverId, content, LocalDateTime.now());
        messageRepo.save(personalMessage);
        return personalMessage;
    }

    public Message createPublicMessage(int senderId, String content) {
        Message mainRoomMessage = Message.createMainRoomMessage(senderId, content, LocalDateTime.now());
        messageRepo.save(mainRoomMessage);
        return mainRoomMessage;
    }
    public List<Message> loadPublicMessages(LocalDateTime startDate)
    {
        return loadPublicMessages(startDate,LocalDateTime.now());
    }
    public List<Message> loadPublicMessages(LocalDateTime startDate,LocalDateTime endDate)
    {
        return messageRepo.findByMessageType(MessageType.MAIN_ROOM).stream()
                .filter(message->message.getTime().isAfter(startDate) && message.getTime().isAfter(endDate))
                .collect(Collectors.toList());
    }
    public List<Message> loadPublicMessages()
    {
        return messageRepo.findByMessageType(MessageType.MAIN_ROOM);
    }
    public Response<File> exportUserMessages(String fileName, int userId, LocalDate startDateTime, LocalDateTime endDate)
    {
        List<Message> result=new ArrayList<>();
        result.addAll(messageRepo.findBySenderId(userId));
        result.addAll(messageRepo.findByReceiverId(userId));
        return FileWriter.writeListToFile(fileName,result);
    }
    public List<Message> getChannelMessages(int senderId,int reciverId)
    {
        return messageRepo.findBySenderIdAndReceiverIdAndMessageType(senderId,reciverId,MessageType.PERSONAL);
    }

}
