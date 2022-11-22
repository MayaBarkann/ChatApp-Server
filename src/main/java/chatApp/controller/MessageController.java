package chatApp.controller;

import chatApp.Entities.Message;
import chatApp.Entities.Response;
import chatApp.Entities.UserActions;
import chatApp.service.MessageService;
import chatApp.service.PermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
public class MessageController {
    private final  MessageService messageService;
    private final PermissionService permissionService;
     @Autowired
    public MessageController(MessageService messageService,PermissionService permissionService) {
        this.messageService = messageService;
        this.permissionService=permissionService;
    }

    @PostMapping("MainRoom/Send")
    public ResponseEntity<String> sendPublicMessage(@RequestBody String message,@RequestHeader(HttpHeaders.FROM) int senderId)
    {
        Response<Boolean> response = permissionService.checkPermission(senderId, UserActions.SendMainRoomMessage);
        if(response.isSucceed())
        {
            if(response.getData()) {
                messageService.createPublicMessage(senderId, message);
                return ResponseEntity.ok("The message was sent successfully.");
            }
            return ResponseEntity.status(401).body("You don't have permission to send a message to the main room.");
        }
        return ResponseEntity.badRequest().body("user not found.");
    }
    @MessageMapping("/app")
    @SendTo("/MainRoom/Get")
    public ResponseEntity<List<Message>> getAllMainRoomMessages(@RequestParam int userID,@RequestParam(required = false) LocalDateTime start,@RequestParam(required = false) LocalDateTime end)
    {
        Response<Boolean> response = permissionService.checkPermission(userID, UserActions.ReceiveMainRoomMessage);
        if(response.isSucceed())
        {
            if(response.getData()) {
                return ResponseEntity.ok(messageService.loadPublicMessages(Optional.ofNullable(start),Optional.ofNullable(end)));
            }
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.badRequest().body(null);
    }
    @PostMapping("channel/send")
    public ResponseEntity<String> sendPersonalMessage(@RequestParam int senderId,@RequestParam int reciverID,@RequestBody String message)
    {
        Response<Boolean> response = permissionService.checkPermission(senderId, UserActions.SendPersonalMessage);
        Response<Boolean> response2 = permissionService.checkPermission(reciverID, UserActions.ReceivePersonalMessage);
        if(response.isSucceed() && response2.isSucceed())
        {
            if(response.getData() && response2.getData()) {
                messageService.createPrivateMessage(senderId, reciverID, message);
                return ResponseEntity.ok("The message was sent successfully.");
            }
           if(response.getData()) return ResponseEntity.status(401).body("You don't have permission to send a personal message.");
              return ResponseEntity.status(401).body("The reciver doesn't have permission to receive a personal message.");
        }
       if(!response.isSucceed()) return ResponseEntity.badRequest().body("sender not found.");
       return ResponseEntity.badRequest().body("reciver not found.");
    }
    @GetMapping("channel/get")
    public ResponseEntity<List<Message>> getPersonalMessages(@RequestParam int senderId,@RequestParam int reciverId)
    {
        List<Message> result;
        result=messageService.getChannelMessages(senderId,reciverId);
        return  ResponseEntity.ok(result);
    }
    @GetMapping("channel/getAll")
    public ResponseEntity<List<Message>> getAllUserChannels(@RequestParam int userId)
    {
        Response<Boolean> response = permissionService.checkPermission(userId, UserActions.ReceivePersonalMessage);
        if(response.isSucceed())
        {
            if(response.getData()) {
                return ResponseEntity.ok(messageService.getAllUserChannels(userId));
            }
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.badRequest().body(null);
    }

}
