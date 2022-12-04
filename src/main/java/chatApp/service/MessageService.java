package chatApp.service;

import chatApp.entities.Message;
import chatApp.entities.MessageType;
import chatApp.entities.Response;
import chatApp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepo;

    @Autowired
    public MessageService(MessageRepository repository) {
        messageRepo = repository;
    }

    /**
     * Creates a personal Message and saves it in the database.
     *
     * @param senderId - int, id of user that send the message.
     * @param reciverId - int, id of user to whom the message is sent.
     * @param content - String, text body of the message
     * @return Message object, containing message data.
     */
    public Message createPrivateMessage(int senderId, int reciverId, String content) {
        Message personalMessage = Message.createPersonalMessage(senderId, reciverId, content, LocalDateTime.now());
        messageRepo.save(personalMessage);
        return personalMessage;
    }

    /**
     * Creates a public Message for the Main Chat Room and saves it in the database.
     *
     * @param senderId - int, id of user that send the message.
     * @param content - String, text body of the message
     * @return Message object, containing the message data.
     */
    public Message createPublicMessage(int senderId, String content) {
        Message mainRoomMessage = Message.createMainRoomMessage(senderId, content, LocalDateTime.now());
        messageRepo.save(mainRoomMessage);
        return mainRoomMessage;
    }

    /**
     * Finds and returns a list of all the public messages between the two argument dates and times.
     * If the given start date is empty, minimal date and time is the earliest possible of LocalDateTime.
     * If the given end date is empty, max date and time is the LocalDateTime.now()
     *
     * @param startDateArgument - Optional<LocalDateTime>, contains the minimal date and time of the messages we want to save in the list (or Optional.empty()).
     * @param endDateArgument - Optional<LocalDateTime>, contains the max date and time of the messages we want to save in the list (or Optional.empty()).
     * @return List of Message objects, containing all the public messages between the start and end date arguments.
     */
    public List<Message> loadPublicMessages(Optional<LocalDateTime> startDateArgument, Optional<LocalDateTime> endDateArgument) {
        final LocalDateTime startDate, endDate;
        if (!startDateArgument.isPresent()) startDate = LocalDateTime.MIN;
        else startDate = startDateArgument.get();
        if (!endDateArgument.isPresent()) endDate = LocalDateTime.now();
        else endDate = endDateArgument.get();
        return messageRepo.findByMessageType(MessageType.MAIN_ROOM).stream()
                .filter(message -> ServiceUtil.dateIsBetween(message.getTime(), startDate, endDate))
                .collect(Collectors.toList());
    }

    /**
     * Finds message between the given dates, sent/received by user (represented by user id), and writes them to a file.
     * If the given start date is empty, minimal date and time is the earliest possible of LocalDateTime.
     * If the given end date is empty, max date and time is the LocalDateTime.now()
     *
     * @param fileName - String, name of the file we want to write the messages to.
     * @param userId -  int, id of the user who is the sender or the receiver of the messages we want to save.
     * @param startDateArgument - Optional<LocalDateTime>, contains the minimal date and time of the messages we want to write to the file (or Optional.empty()).
     * @param endDateArgument - Optional<LocalDateTime>, contains the max date and time of the messages we want to write to the file (or Optional.empty()).
     * @return Response<File> object containing the File object representing the file to which the messaged were written.
     */
    public Response<File> exportUserMessages(String fileName, int userId, Optional<LocalDateTime> startDateArgument, Optional<LocalDateTime> endDateArgument) {
        final LocalDateTime startDate, endDate;
        if (!startDateArgument.isPresent()) startDate = LocalDateTime.MIN;
        else startDate = startDateArgument.get();
        if (!endDateArgument.isPresent()) endDate = LocalDateTime.now();
        else endDate = endDateArgument.get();
        List<Message> result = new ArrayList<>();
        result.addAll(messageRepo.findBySenderId(userId));
        result.addAll(messageRepo.findByReceiverId(userId));
        result = result.stream().filter(message -> ServiceUtil.dateIsBetween(message.getTime(), startDate, endDate)).collect(Collectors.toList());
        return FileWriter.writeListToFile(fileName, result);

    }

    /**
     * Finds and returns a list of personal messages between specified sender and receiver.
     *
     * @param senderId - int, id of the sender of the messages.
     * @param reciverId - int, id of the receiver of the messages
     * @return List of Message objects, containing messages of Personal type sent between a sender (represented by a sender id) and a receiver (represented by a receiver id)
     */
    public List<Message> getChannelMessages(int senderId, int reciverId) {
        List<Message> channel = messageRepo.findBySenderIdAndReceiverIdAndMessageType(senderId, reciverId, MessageType.PERSONAL);
        channel.addAll(messageRepo.findBySenderIdAndReceiverIdAndMessageType(reciverId, senderId, MessageType.PERSONAL));
        return channel.stream().sorted(Comparator.comparing(Message::getTime)).collect(Collectors.toList());
    }

    /**
     * Finds and returns a list of personal messages send by a specified sender.
     *
     * @param senderId - int, id of the sender of the messages.
     * @return List of Message objects, containing messages of Personal type sent by the sender (represented by a sender id).
     */
    public List<Message> getAllUserChannels(int senderId) {
       return messageRepo.findAll().stream().filter(
               message -> message.getType()==MessageType.PERSONAL &&( message.getSenderId()==senderId || message.getReceiverId()==senderId)).collect(Collectors.toList());
    }

    /**
     * Creates a map where the List of private messages a user sent is paired to the user's id, and each list is ordered by ascending message send time.
     *
     * Creates and returns a map
     * @param userId - int, id of the sender of the private messages.
     * @return Map<Integer,List<Message>>, map pair: key- user's id, value - List of all the private(personal) messages he sent, and each list is ordered by ascending message send timr.
     */
    public Map<Integer, List<Message>> getAllPrivateMessagesByUserIdSortedByTime(int userId) {
        List<Message> allMessages = getAllUserChannels(userId);
        Map<Integer, List<Message>> privateMessages =  allMessages.stream().collect(Collectors.groupingBy(
                message -> message.getReceiverId() != userId ? message.getReceiverId(): message.getSenderId()));
        //todo: check if there is a prettier way
        for(List<Message> messages : privateMessages.values()){
            messages.sort(Comparator.comparing(Message::getTime));
        }
       return privateMessages;
    }

}
