package chatApp.controller;

import chatApp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    private MessageService messageService;
     @Autowired
    public MessageController(MessageService messageService)
    {
        this.messageService=messageService;
    }

}
