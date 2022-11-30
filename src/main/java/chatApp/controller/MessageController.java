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
    private final ChatUtil chatUtil;

    @Autowired
    public MessageController(MessageService messageService, PermissionService permissionService, UserService userService, ChatUtil chatUtil) {
        ControllerUtil.logger.info("MessageController constructor");
        this.messageService = messageService;
        this.permissionService = permissionService;
        this.userService = userService;
        this.chatUtil = chatUtil;
    }

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

    @GetMapping("/MainRoom/Get")
    public ResponseEntity<List<OutputMessage>> getAllMainRoomMessages(@RequestAttribute("userId") int userID, @RequestParam(required = false) LocalDateTime start, @RequestParam(required = false) LocalDateTime end) {
        Response<Boolean> response = permissionService.checkPermission(userID, UserActions.ReceiveMainRoomMessage);
        if (response.isSucceed()) {
            if (response.getData()) {
                List<Message> messageList = messageService.loadPublicMessages(Optional.ofNullable(start), Optional.ofNullable(end));
                List<OutputMessage> result = messageList.stream().map(message -> OutputMessage.createPublicMessage(message, userService.getUserNameById(message.getId()))).collect(Collectors.toList());
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.badRequest().body(null);
    }

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

    /*
      @param userId the id of the user who wants to get the messages
      @param reciverName the name of the other user in the private channel
      @return a map of messages between the two users
     */
    @GetMapping("/channel/get")
    public ResponseEntity<Map<String, OutputMessage>> getPersonalMessages(@RequestAttribute("userId") int senderId, @RequestParam String reciverName) {
        int reciverId;
        Response<Integer> reciverIdResponse = userService.getUserIdByName(reciverName);
        if (reciverIdResponse.isSucceed()) reciverId = reciverIdResponse.getData();
        else return ResponseEntity.badRequest().body(null);
        List<Message> result;
        result = messageService.getChannelMessages(senderId, reciverId);
        return ResponseEntity.ok(getMessagesMapFromList(result));
    }

    @GetMapping("/channel/getAll")
    public ResponseEntity<Map<String, OutputMessage>> getAllUserChannels(@RequestAttribute("userId") int userId) {
        Response<Boolean> response = permissionService.checkPermission(userId, UserActions.ReceivePersonalMessage);
        if (response.isSucceed()) {
            if (response.getData()) {
                List<Message> userChannels = messageService.getAllUserChannels(userId);
                Map<String, OutputMessage> result = getMessagesMapFromList(userChannels);
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.badRequest().body(null);
    }

    /**
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

    private Map<String, OutputMessage> getMessagesMapFromList(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return new HashMap<>();
        }
        messages = messages.stream().sorted(Comparator.comparing(Message::getTime)).collect(Collectors.toList());
        Map<String, OutputMessage> outputMessages = new HashMap<>();
        for (Message message : messages) {
            String senderName = userService.getUserNameById(message.getSenderId());
            switch (message.getType()) {
                case PERSONAL: {
                    String reciverName = userService.getUserNameById(message.getReceiverId());
                    outputMessages.put(reciverName, OutputMessage.createPrivateMessage(message, senderName, reciverName));
                    break;
                }
                case MAIN_ROOM:
                    outputMessages.put(MessageType.MAIN_ROOM.toString(), OutputMessage.createPublicMessage(message, senderName));
                    break;
                default:
                    return null;
            }
        }
        return outputMessages;
    }

}
