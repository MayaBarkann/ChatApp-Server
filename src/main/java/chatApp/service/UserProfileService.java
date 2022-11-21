package chatApp.service;

import chatApp.Entities.Response;
import chatApp.Entities.UserProfile;
import chatApp.repository.UserProfileRepository;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class UserProfileService {

    private final Storage storage;
    private final UserProfileRepository userProfileRepository;
    private static final String URL_TEMPLATE = "https://storage.cloud.google.com/chatapp-ec932.appspot.com/%s";

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, Storage storage){
        this.userProfileRepository = userProfileRepository;
        this.storage = storage;
    }

    /***
     * get user profile by user id
     * @param id user id
     * @return response with the user profile if exists if not a failure response with the correspond message.
     */

    public Response<UserProfile> getUserProfileById(int id){
        UserProfile userProfile = userProfileRepository.findById(id);
        if (userProfile != null){
            return Response.createSuccessfulResponse(userProfile);
        }
        return Response.createFailureResponse("there is no such user");
    }

    /***
     * Edit user profile, saving the new user profile to the user profile database. If we want to update/upload image,
     * the image is saved in a bucket in fire base and we save the url of the image in the user profile repository.
     * @param userProfile
     * @param localImagePath path to the user image in case we want to update the
     * @return response with the user profile
     */

    public Response<UserProfile> editUserProfile(UserProfile userProfile, String localImagePath){
        if (localImagePath != null){
            if (!localImagePath.isEmpty() && !uploadImage(userProfile, localImagePath).isSucceed()){

                return Response.createFailureResponse("user profile edition failed- could not upload image to profile");
            }
        }

        userProfileRepository.save(userProfile);

        return Response.createSuccessfulResponse(userProfile);
    }

    private Response<UserProfile> uploadImage(UserProfile userProfile, String localPath){
        int id = userProfile.getId();
        BlobId blobId = BlobId.of("chatapp-ec932.appspot.com",Integer.toString(id));
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        File fileToRead = new File(localPath);
        try{
            byte[] data = Files.readAllBytes(Paths.get(fileToRead.toURI()));
            storage.create(blobInfo,data);
            String profilePhotoUrl = String.format(URL_TEMPLATE,id);
            userProfile.setImageUrl(profilePhotoUrl);

            return Response.createSuccessfulResponse(userProfile);

        } catch (IOException e){

            return Response.createFailureResponse("could not upload image " + e.getMessage());
        }
    }

}
