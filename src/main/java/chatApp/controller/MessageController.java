package chatApp.controller;

import chatApp.entities.Message;
import chatApp.entities.MessageType;
import chatApp.entities.Response;
import chatApp.entities.UserActions;
import chatApp.controller.entities.OutputMessage;
import chatApp.service.MessageService;
import chatApp.service.PermissionService;


import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class MessageController {
    private final MessageService messageService;
    private final PermissionService permissionService;
    private final UserService userService;
    private final SocketsUtil chatUtil;

    /**
     * Constructor for MessageController.
     *
     * @param messageService    MessageService object.
     * @param permissionService PermissionService object.
     * @param userService       UserService object.
     * @param chatUtil          ChatUtil object.
     */
    @Autowired
    public MessageController(MessageService messageService, PermissionService permissionService, UserService userService, SocketsUtil chatUtil) {
        ControllerUtil.logger.info("MessageController constructor");
        this.messageService = messageService;
        this.permissionService = permissionService;
        this.userService = userService;
        this.chatUtil = chatUtil;
    }

    /**
     * Sends a message to the main room.
     *
     * @param message  object contains message content.
     * @param senderId int representing the sender's id.
     *                 if sender has permission to send messages to main room then the message is sent.
     *                 else the function returned a response  with a message that the user does not have permission.
     * @return ResponseEntity<String> contains sending status.
     */
    @PostMapping("/MainRoom/Send")
    public ResponseEntity<String> sendPublicMessage(@RequestBody String message, @RequestAttribute("userId") int senderId) {
        Response<Boolean> response = permissionService.checkPermission(senderId, UserActions.SendMainRoomMessage);
        if (response.isSucceed()) {
            if (response.getData()) {
                Message publicMessage = messageService.createPublicMessage(senderId, message);
                String username = userService.findUserById(senderId).getData().getUsername();
                chatUtil.writeMessageToMainRoom(OutputMessage.createPublicMessage(publicMessage, username));
                return ResponseEntity.ok("The message was sent successfully.");
            }
            return ResponseEntity.status(401).body("You don't have permission to send a message to the main room.");
        }
        return ResponseEntity.badRequest().body(response.getMessage());
    }

    /**
     * get messages from the main room from given range.
     * if not given returns all main room messages.
     * @param userID int representing the user's id.
     * @param start optional parameter that represents the start of the time range.
     * @param end optional parameter that represents the end of the time range.
     * @return ResponseEntity<List<Message>> contains the messages from the main room.
     */
    @GetMapping("/MainRoom/Get")
    public ResponseEntity<List<OutputMessage>> getAllMainRoomMessages(@RequestAttribute("userId") int userID, @RequestParam(required = false) String start, @RequestParam(required = false) String end) {
        Response<Boolean> response = permissionService.checkPermission(userID, UserActions.ReceiveMainRoomMessage);
        if (response.isSucceed()) {
            if (response.getData()) {
                List<Message> messageList = messageService.loadPublicMessages(ControllerUtil.convertOffsetToLocalDateTime(start), ControllerUtil.convertOffsetToLocalDateTime(end));
                List<OutputMessage> result = messageList.stream().map(message -> OutputMessage.createPublicMessage(message, userService.getUserNameById(message.getSenderId()))).collect(Collectors.toList());
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.badRequest().body(null);
    }

    /**
     * Sends a message to a private room.
     *
     * @param message      object contains message content.
     * @param senderId     int representing the sender's id.
     * @param reciverName  String representing the receiver's name.
     *                     if sender and receiver have permission to send messages to private room then the message is sent.
     *                     else the function returned a response  with a message that the user does not have permission.
     * @return ResponseEntity<String> contains sending status.
     */
    @PostMapping("/channel/send")
    public ResponseEntity<String> sendPersonalMessage(@RequestAttribute("userId") int senderId, @RequestParam String reciverName, @RequestBody String message) {
        int reciverID;
        Response<Integer> reciverIdResponse = userService.getUserIdByName(reciverName);
        if (reciverIdResponse.isSucceed()) reciverID = reciverIdResponse.getData();
        else return ResponseEntity.badRequest().body(reciverIdResponse.getMessage());
        Response<Boolean> response = permissionService.checkPermission(senderId, UserActions.SendPersonalMessage);
        Response<Boolean> response2 = permissionService.checkPermission(reciverID, UserActions.ReceivePersonalMessage);
        if (response.isSucceed() && response2.isSucceed()) {
            if (response.getData() && response2.getData()) {
                String senderName = userService.findUserById(senderId).getData().getUsername();
                Message privateMessage = messageService.createPrivateMessage(senderId, reciverID, message);
                chatUtil.writeMessageToPrivateChannel(OutputMessage.createPrivateMessage(privateMessage, senderName, reciverName));
                return ResponseEntity.ok("The message was sent successfully.");
            }
            if (response.getData())
                return ResponseEntity.status(401).body("You don't have permission to send a personal message.");
            return ResponseEntity.status(401).body("The reciver doesn't have permission to receive a personal message.");
        }
        if (!response.isSucceed()) return ResponseEntity.badRequest().body("sender not found.");
        return ResponseEntity.badRequest().body("reciver not found.");
    }

    /**
     * get all messages from a specific channel (private conversation).
      @param senderId the id of the user who wants to get the messages
      @param reciverName the name of the other user in the private channel
      @return a map of messages between the two users
     */
    @GetMapping("/channel/get")
    public ResponseEntity<List<OutputMessage>> getPersonalMessages(@RequestAttribute("userId") int senderId, @RequestParam String reciverName) {
        int reciverId;
        String senderName = userService.getUserNameById(senderId);
        Response<Integer> reciverIdResponse = userService.getUserIdByName(reciverName);
        if (reciverIdResponse.isSucceed()) reciverId = reciverIdResponse.getData();
        else return ResponseEntity.badRequest().body(null);
        List<OutputMessage> result;
        result = messageService.getChannelMessages(senderId, reciverId)
                .stream().sorted(Comparator.comparing(Message::getTime))
                .map(message -> OutputMessage.createPrivateMessage(message, senderName, reciverName))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
   /**
     * get all messages from all specific user channels (private conversations).
      @param userId the id of the user who wants to get the messages
      @return a map with other participant name as a key and a list of messages between the two users as a value.
     */
    @GetMapping("/channel/getAll")
    public ResponseEntity<Map<String,List<OutputMessage>>> getAllUserChannels(@RequestAttribute("userId") int userId) {
        Response<Boolean> response = permissionService.checkPermission(userId, UserActions.ReceivePersonalMessage);
        if (response.isSucceed()) {
            if (response.getData()) {
                List<Message> userChannels = messageService.getAllUserChannels(userId);
                Map<String, List<OutputMessage>> result = getMessagesMapFromList(userChannels);
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.badRequest().body(null);
    }

    /**
     * get all messages from all specific user channels (private conversations).
     * @param userId the user id
     * @return all the private messages that the user has sent or received
     */
    @GetMapping("/channel/get-all-private-messages")
    public ResponseEntity<Map<String, List<OutputMessage>>> getAllPrivateMessagesByUserId(@RequestAttribute("userId") int userId) {
        Response<Boolean> response = permissionService.checkPermission(userId, UserActions.ReceivePersonalMessage);
        if (response.isSucceed()) {
            if (response.getData()) {
                Map<Integer, List<Message>> sortedChannelMessagesById = messageService.getAllPrivateMessagesByUserIdSortedByTime(userId);
                Map<String, List<OutputMessage>> sortedChannelMessagesByUserName = new HashMap<>();
                //todo; change this to be more readable

                // maps the user id (key) to its userName and converts each message in the lists (value) to output message .
                sortedChannelMessagesById.forEach(
                        (k, v) -> sortedChannelMessagesByUserName.put(userService.getUserNameById(k),
                                v.stream().map(m -> OutputMessage.createPrivateMessage(
                                        m, userService.getUserNameById(m.getSenderId()), userService.getUserNameById(m.getReceiverId()))).collect(Collectors.toList())));

                return ResponseEntity.ok(sortedChannelMessagesByUserName);
            }
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.badRequest().body(null);

    }

    /**
     * Creates and returns Map of messages where key is receiver username, and value is the message data.
     *
     * @param messages - List<Message>, contains list of messages (each message contains- content, senderId, receiverId, time when wast sent)
     * @return Map<String, OutputMessage>, key: receiverName (for private message), MAIN_ROOM (for public message), value - OutputMessage.
     */
    private Map<String,List< OutputMessage>> getMessagesMapFromList(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return new HashMap<>();
        }
        messages = messages.stream().sorted(Comparator.comparing(Message::getTime)).collect(Collectors.toList());
        Map<String,List<OutputMessage>> result = new HashMap<>();
        String mainRoom = MessageType.MAIN_ROOM.toString();
        for (Message message : messages) {
            String senderName = userService.getUserNameById(message.getSenderId());
            switch (message.getType()) {
                case PERSONAL: {
                    String reciverName = userService.getUserNameById(message.getReceiverId());
                    if(!result.containsKey(reciverName)) {
                        result.put(reciverName, new ArrayList<>());
                    }
                        result.get(reciverName).add(OutputMessage.createPrivateMessage(message, senderName, reciverName));
                    break;
                }
                case MAIN_ROOM:
                    if(!result.containsKey(mainRoom)) {
                        result.put(mainRoom, new ArrayList<>());
                    }
                    result.get(mainRoom).add(OutputMessage.createPublicMessage(message, senderName));
                    break;
                default:
                    return null;
            }
        }
        return result;
    }
    /**
     * given id of a user, returns all the messages that he has sent or received in file.
     * If the given start date is empty, minimal date and time is the earliest possible of LocalDateTime.
     * If the given end date is empty, max date and time is the LocalDateTime.now()
     *
     * @param userId -  int, id of the user who is the sender or the receiver of the messages we want to save.
     * @param startDateArgument - Optional<LocalDateTime>, contains the minimal date and time of the messages we want to write to the file (or Optional.empty()).
     * @param endDateArgument - Optional<LocalDateTime>, contains the max date and time of the messages we want to write to the file (or Optional.empty()).
     * @return server response with the file name and content.
     */
    @GetMapping(value = "/exportMessages")
    public void exportUserMessages(@RequestAttribute("userId") int userId, @RequestParam (value = "from",required = false) String startDateArgument, @RequestParam (value = "to",required = false) String  endDateArgument, HttpServletResponse response ) throws IOException {
        String fileName = "messages.txt";
        List<Message> result = new ArrayList<>();
        Optional<LocalDateTime> startOptional = ControllerUtil.convertOffsetToLocalDateTime(startDateArgument);
        Optional<LocalDateTime> endOptional = ControllerUtil.convertOffsetToLocalDateTime(endDateArgument);
        result.addAll(messageService.loadPublicMessages(startOptional, endOptional));
        result.addAll(messageService.getAllUserChannels(userId));
        String outputString = getMessagesMapFromList(result).entrySet().stream().map(entry -> entry.getKey().toString() + " " + ControllerUtil.printList(entry.getValue())).collect(Collectors.joining("\n"));
            response.setContentType("text/plain; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition","attachment;filename="+fileName);
            PrintWriter out = response.getWriter();
            out.println(outputString);
    }

}
