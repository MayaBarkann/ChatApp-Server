package chatApp.service;

import chatApp.entities.Response;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileWriter {

    /**
     *
      * @param filePath - the path of the file in which the messages will be saved.
     * @param list , List of the data objects to write to the file.
     * @return Response<File> object, contains false and error message if action wasn't successful, otherwise - true, and the File object to which the data was written.
     */
public static Response<File> writeListToFile(String filePath, List<?> list){
    Path path= Paths.get(filePath);
    if(!Files.exists(path)) {
       try {
           Files.createFile(path);
       } catch (IOException e) {
           return Response.createFailureResponse(String.format("Cannot create the file: %s \nerror message: %s",filePath,e.getMessage()));
       }
   }
    String result = list.stream().map(element -> element.toString()).collect(Collectors.joining("\n"));
    try {
        Files.write(path,result.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
       return Response.createFailureResponse(String.format("can't write to file: %s\nerror message: %s",filePath,e.getMessage()));
    }
   return Response.createSuccessfulResponse(new File(filePath));
}
}
