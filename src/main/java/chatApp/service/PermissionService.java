package chatApp.service;

import chatApp.entities.PermissionsManager;
import chatApp.entities.Response;
import chatApp.entities.User;
import chatApp.entities.UserActions;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class PermissionService {
    UserRepository userRepository;

    @Autowired
    public PermissionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public Response<Boolean> checkPermission(int userId, UserActions action) {
        Optional<User> user = userRepository.findById(userId);
           if (!user.isPresent()) {
                return Response.createFailureResponse(String.format("User with id: %d does not exist", userId));
            }
           if( PermissionsManager.hasPermission(user.get().getUserType(),action))
               return Response.createSuccessfulResponse(true);
           return Response.createSuccessfulResponse(false);
    }
}
