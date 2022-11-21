package chatApp.service;

import chatApp.Entities.Response;
import chatApp.Entities.UserProfile;
import chatApp.repository.UserProfileRepository;
import chatApp.repository.UserRepository;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    public Response<UserProfile> getUserProfileById(int id){
    }

    public Response<UserProfile> editUserProfile(UserProfile userProfile, String localImagePath){
        if (localImagePath != null){ //TODO: CHECK ABOUT NULL OR ""
            if (!localImagePath.isEmpty() && !uploadImage(userProfile, localImagePath)){
                return Response.createFailureResponse("user profile edition failed- could not upload image to profile");
            }
        }
        userProfileRepository.save(userProfile);
        return Response.createSuccessfulResponse(userProfile);
    }

    private boolean uploadImage(UserProfile userProfile, String localPath){
        int id = userProfile.getId();
        BlobId blobId = BlobId.of("chatapp-ec932.appspot.com",Integer.toString(id));
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        File fileToRead = new File(localPath);
        try{
            byte[] data = Files.readAllBytes(Paths.get(fileToRead.toURI()));
            storage.create(blobInfo,data);
            String profilePhotoUrl = String.format(URL_TEMPLATE,id);
            userProfile.setImageUrl(profilePhotoUrl);

            return true;

        } catch (IOException e){

            return false;
        }
    }

    //    public Response<UserProfile> upload(String filePath, int id){
//        BlobId blobId = BlobId.of("chatapp-ec932.appspot.com",Integer.toString(id));
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
//        File fileToRead = new File(filePath);
//
//        try{
//            byte[] data = Files.readAllBytes(Paths.get(fileToRead.toURI()));
//            storage.create(blobInfo,data);
//            UserProfile userProfile = userProfileRepository.findById(id);
//            String profilePhotoUrl = String.format(URL_TEMPLATE,id);
//            userProfile.setImageUrl(profilePhotoUrl);
//            userProfileRepository.save(userProfile);
//
//            return Response.createSuccessfulResponse(userProfile);
//
//        } catch (IOException e){
//
//            return Response.createFailureResponse("could not upload file to cloud, invalid path " + e.getMessage());
//        }
//    }



//    public Response<String> upload(MultipartFile multipartFile, int id) {
//
//        try {
//            String fileName = Integer.toString(id);                        // to get original file name
//            //fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));  // to generated random string values for file name.
//
//            File file = convertToFile(multipartFile, fileName);                      // to convert multipartFile to File
//            String TEMP_URL = uploadFile(file, fileName);                                   // to get uploaded file link
//            file.delete();                                                                // to delete the copy of uploaded file stored in the project folder
//            return Response.createSuccessfulResponse(TEMP_URL);                     // Your customized response
//        } catch (IOException e) {
//            e.printStackTrace();
//            return Response.createFailureResponse("Could not upload image: " + e.getMessage());
//        }
//
//    }

//    public Object download(String fileName) throws IOException {
//        String destFileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));     // to set random strinh for destination file name
//        String destFilePath = "Z:\\New folder\\" + destFileName;                                    // to set destination file path
//
//        ////////////////////////////////   Download  ////////////////////////////////////////////////////////////////////////
//        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("path of JSON with genarated private key"));
//        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
//        Blob blob = storage.get(BlobId.of("your bucket name", fileName));
//        blob.downloadTo(Paths.get(destFilePath));
//        return sendResponse("200", "Successfully Downloaded!");
//    }

//    private String uploadFile(File file, String fileName) throws IOException {
//        BlobId blobId = BlobId.of("chatapp-ec932.appspot.com", fileName);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
//        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/chatapp-ec932-firebase-adminsdk-inpxh-117d4a7941-2.json"));
//        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
//        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
//        return String.format(DOWNLOAD_URL, fileName);
//
//        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
//    }
//
//    private static File convertToFile(MultipartFile multipartFile, String fileName) throws IOException{
//        File tempFile = new File(fileName);
//        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//            fos.write(multipartFile.getBytes());
//        }
//        return tempFile;
//    }
//
//    private static String getExtension(String fileName) {
//        return fileName.substring(fileName.lastIndexOf("."));
//    }



}
