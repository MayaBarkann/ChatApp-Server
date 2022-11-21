package chatApp.controller;

import chatApp.Entities.Response;
import chatApp.Entities.UserActions;
import chatApp.Entities.UserProfile;
import chatApp.controller.entities.UserProfileToPresent;
import chatApp.controller.entities.UserToPresent;
import chatApp.service.PermissionService;
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
    @Autowired
    private PermissionService permissionService;

    @PostMapping("/edit")
    public Response<UserProfile> editUserProfile(@RequestBody UserProfile userProfile, @RequestParam("path") String localImagePath){
        if(userProfile == null){
            return Response.createFailureResponse("can not update user profile, user does not exists");
        }
        Response<Boolean> response = permissionService.checkPermission(userProfile.getId(), UserActions.HasProfile);
        if (!response.isSucceed()){
            return Response.createFailureResponse("can not update user profile, this user does not have permissions for editing profile");
        }
        return userProfileService.editUserProfile(userProfile, localImagePath);
    }

    @GetMapping("/loadProfile")
    public Response<UserProfileToPresent> getUserProfileById(@RequestParam("id") int userId){

        //todo: validate with permissiom that this operation is valid then no need to check if user exists
        if (!userService.userExistsById(userId)){
            return Response.createFailureResponse("user does not exists");
        } else {
            Response<UserProfile> responseProfile = userProfileService.getUserProfileById(userId);
            Response<User> responseUser = userService.findUserById(userId);

            if(!responseProfile.isSucceed() || !responseUser.isSucceed()){
                return Response.createFailureResponse("could not load user profile");
            }

            return Response.createSuccessfulResponse(UserProfileToPresent.createFromUserProfileAndUser(responseProfile.getData(),
                    responseUser.getData()));
        }

    }



}
