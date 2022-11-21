package chatApp.controller;

import chatApp.Entities.Response;
import chatApp.Entities.UserProfile;
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
    public Response<UserProfile> uploadProfilePhoto(@RequestParam("path") String path, @RequestParam("id") int id){
        return userProfileService.upload(path, id);
    }

    @PostMapping("/edit")
    public Response<UserProfile> editProfile(@RequestBody UserProfile userProfile, @RequestParam("path") String localImagePath){
        //TODO: CHECK FIRST IF THIS USER EXISTS BY ID IF NOT THROW EXCEPTION
        return userProfileService.edit(userProfile, localImagePath);
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
