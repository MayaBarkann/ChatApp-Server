package chatApp.service;

import chatApp.Entities.Response;
import chatApp.repository.UserProfileRepository;
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

    @Autowired
    private Storage storage;
    private UserProfileRepository userProfileRepository;
    private final static String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/chatapp-ec932.appspot.com/o/%s?alt=media";
    //https://console.cloud.google.com/storage/browser/chatapp-ec932.appspot.com

    public UserProfileService(UserProfileRepository userProfileRepository, Storage storage){
        this.userProfileRepository = userProfileRepository;
        this.storage = storage;
    }


    public Response<String> upload(String filePath, String idFileName) throws Exception{
        BlobId blobId = BlobId.of("chatapp-ec932.appspot.com", idFileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        File fileToRead = new File(filePath);
        byte[] data = Files.readAllBytes(Paths.get(fileToRead.toURI()));
        storage.create(blobInfo,data);
        return Response.createSuccessfulResponse("test");
    }











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
