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

    /**
     * Checks if a given user (represented by user id) has permission to perform the given action in the system.
     *
     * @param userId - int, id of user that we want to check if he has a permission to perform the given action.
     * @param action - UserActions Enum, the action we need to check if the user can perform.
     * @return Response<Boolean> object, contains failure response - if user wasn't found. returns response with false if user doesn't have permission to perform action, and response with true if user has the permission.
     */
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
