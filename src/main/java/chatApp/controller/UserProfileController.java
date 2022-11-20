package chatApp.controller;

import chatApp.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @PostMapping("/pic")
    public Object upload(@RequestParam("file") MultipartFile multipartFile, @RequestHeader("id") int id) {
        //logger.info("HIT -/upload | File Name : {}", multipartFile.getOriginalFilename());
        return userProfileService.upload(multipartFile, id);
    }

//    @PostMapping("/pic/{fileName}")
//    public Object download(@PathVariable String fileName) throws IOException {
//        //logger.info("HIT -/download | File Name : {}", fileName);
//        return userProfileService.download(fileName);
//    }

}
