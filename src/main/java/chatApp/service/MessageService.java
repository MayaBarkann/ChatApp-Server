package chatApp.service;

import chatApp.Entities.Message;
import chatApp.Entities.MessageType;
import chatApp.Entities.Response;
import chatApp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepo;

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
                .filter(message->ServiceUtil.dateIsBetween(message.getTime(),startDate,endDate))
                .collect(Collectors.toList());
    }
    public List<Message> loadPublicMessages()
    {
        return messageRepo.findByMessageType(MessageType.MAIN_ROOM);
    }
    public Response<File> exportUserMessages(String fileName, int userId, LocalDateTime startDateTime, LocalDateTime endDate)
    {
        if(startDateTime==null)startDateTime=LocalDateTime.MIN;
        if(endDate==null) endDate=LocalDateTime.now();
        List<Message> result=new ArrayList<>();
        result.addAll(messageRepo.findBySenderId(userId));
        result.addAll(messageRepo.findByReceiverId(userId));
        LocalDateTime finalStartDateTime = startDateTime;
        LocalDateTime finalEndDate = endDate;
        result=result.stream().filter(message -> ServiceUtil.dateIsBetween(message.getTime(), finalStartDateTime, finalEndDate)).collect(Collectors.toList());
        return FileWriter.writeListToFile(fileName,result);

    }
    public List<Message> getChannelMessages(int senderId,int reciverId)
    {
        return messageRepo.findBySenderIdAndReceiverIdAndMessageType(senderId,reciverId,MessageType.PERSONAL);
    }
    public List<Message> getAllUserChannels(int senderId)
    {
        return messageRepo.findBySenderIdOrReceiverIdAndMessageType(senderId,senderId,MessageType.PERSONAL);
    }

}
