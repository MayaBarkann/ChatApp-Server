package chatApp.service;

import chatApp.entities.*;
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
        Optional<User> optionalUser = userRepository.findById(userId);
           if (!optionalUser.isPresent()) {
                return Response.createFailureResponse(String.format("User with id: %d does not exist", userId));
            }
           User user = optionalUser.get();
           if( PermissionsManager.hasPermission(user.getUserType(),action)) {
               if(action==UserActions.SendMainRoomMessage)
               {
                   if(user.getMessageAbility()== MessageAbility.MUTED)
                       return Response.createSuccessfulResponse(false);
               }
               return Response.createSuccessfulResponse(true);

           }
           return Response.createSuccessfulResponse(false);
    }
}
