package chatApp.service;

import chatApp.Entities.Response;
import chatApp.repository.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final Map<Integer, String> authTokens;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        authTokens = new HashMap<>();
    }
    private String createAuthToken(String email) {
        String authToken = RandomString.make(64);
        //add token to map; //use user repo
        return authToken;
    }

    public Response<String> userLogin(String email, String password) {
        Response<String> validateResponse = this.loginCredentialsValidate(email,password);
        if(!validateResponse.isSucceed()){
            return Response.createFailureResponse(validateResponse.getMessage());
        }
        String authToken = this.createAuthToken(email);
        return Response.createSuccessfulResponse(authToken);
    }

    private Response<String> loginCredentialsValidate(String email, String password) {
        return Response.createSuccessfulResponse(email);
    }
}
