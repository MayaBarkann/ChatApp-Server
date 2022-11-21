package chatApp.controller;

import chatApp.Entities.Response;
import chatApp.Entities.UserProfile;
import chatApp.controller.entities.UserProfileToPresent;
import chatApp.service.UserProfileService;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import chatApp.Entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserService userService;

//    @PostMapping("/upload")
//    public Response<UserProfile> uploadProfilePhoto(@RequestParam("path") String path, @RequestParam("id") int id){
//        return userProfileService.upload(path, id);
//    }

    @PostMapping("/edit")
    public Response<UserProfile> editUserProfile(@RequestBody UserProfile userProfile, @RequestParam("path") String localImagePath){
        //todo: validate with permissiom that this operation is valid then no need to check if user exists
        if (userProfile == null || !userService.userExistsById(userProfile.getId())){
            return Response.createFailureResponse("can not update user profile, user does not exists");
        }

        return userProfileService.editUserProfile(userProfile, localImagePath);
    }

    @GetMapping("/download")
    public Response<UserProfileToPresent> getUserProfileById(@RequestParam("id") int userId){

        //todo: validate with permissiom that this operation is valid then no need to check if user exists
        if (!userService.userExistsById(userId)){
            return Response.createFailureResponse("user does not exists");
        } else {
            UserProfile userProfile = userProfileService.getUserProfileById(userId).getData();
            if(userProfile == null){
                return Response.createFailureResponse("");
            }
            User user = userService.findUserById(userId).getData();
        }

    }

    private UserProfileToPresent createUserProfileToPresentFromUserAndUserProfile(User user, UserProfile userProfile){

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
