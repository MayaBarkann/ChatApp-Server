package chatApp.controller;

import chatApp.Entities.Response;
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

    @PostMapping("/upload")
    public Response<String> upload(@PathVariable(name="path") String filePath, @PathVariable(name="id") String id) throws Exception{
        return userProfileService.upload(filePath, id);
    }

//    @PostMapping("/upload")
//    @ResponseBody
//    public Response<String> upload(@RequestParam("file") MultipartFile multipartFile, @RequestHeader("id") int id) {
//        //logger.info("HIT -/upload | File Name : {}", multipartFile.getOriginalFilename());
//        return userProfileService.upload(multipartFile, id);
//    }

//    @PostMapping("/pic/{fileName}")
//    public Object download(@PathVariable String fileName) throws IOException {
//        //logger.info("HIT -/download | File Name : {}", fileName);
//        return userProfileService.download(fileName);
//    }

}
