package chatApp.service;

import chatApp.Entities.Response;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
public class UserProfileService {

    private final static String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/chatapp-ec932.appspot.com/o/%s?alt=media";
    //https://console.cloud.google.com/storage/browser/chatapp-ec932.appspot.com


    public Response<String> upload(MultipartFile multipartFile, int id) {

        try {
            String fileName = Integer.toString(id);                        // to get original file name
            //fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));  // to generated random string values for file name.

            File file = this.convertToFile(multipartFile, fileName);                      // to convert multipartFile to File
            String TEMP_URL = this.uploadFile(file, fileName);                                   // to get uploaded file link
            file.delete();                                                                // to delete the copy of uploaded file stored in the project folder
            return Response.createSuccessfulResponse(TEMP_URL);                     // Your customized response
        } catch (IOException e) {
            e.printStackTrace();
            return Response.createFailureResponse("Could not upload image: " + e.getMessage());
        }

    }

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

    private String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("chatapp-ec932.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("downloaded private key JSON file path"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        return String.format(DOWNLOAD_URL, fileName);

//        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException{
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }



}
