package chatApp.controller;

import chatApp.Entities.Message;
import chatApp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Controller
public class MessageController {
    private final  MessageService messageService;
     @Autowired
    public MessageController(MessageService messageService)
    {
        this.messageService=messageService;
    }
    @PostMapping("MainRoom/Send")
    public ResponseEntity<String> sendPublicMessage(@RequestBody String message,@RequestHeader(HttpHeaders.FROM) int senderId)
    {
        messageService.createPublicMessage(senderId,message);
        return ResponseEntity.ok("The message was sent successfully.");
    }
    @GetMapping("MainRoom/Get")
    public ResponseEntity<List<Message>> getAllMainRoomMessages(@RequestParam(required = false) LocalDateTime start,@RequestParam(required = false) LocalDateTime end)
    {
        List<Message> result;
        if(start==null && end==null) result=messageService.loadPublicMessages();
        else if(end==null)result=messageService.loadPublicMessages(start);
        else result=messageService.loadPublicMessages(start, end);
        return  ResponseEntity.ok(result);
    }
    @GetMapping("channel/getSpecific")
    public ResponseEntity<List<Message>> getChannel(int senderId,int reciverId)
    {
        return ResponseEntity.ok(messageService.getChannelMessages(senderId, reciverId));
    }
    @PostMapping("channel/send")
    public ResponseEntity<String> sendPersonalMessage(int senderId,int reciverID,@RequestBody String message)
    {
        Message privateMessage = messageService.createPrivateMessage(senderId, reciverID, message);
        return ResponseEntity.ok("The message was sent successfully.");
    }


}
