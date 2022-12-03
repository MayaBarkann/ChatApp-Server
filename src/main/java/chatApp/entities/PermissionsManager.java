package chatApp.entities;

import java.util.Map;
import java.util.Set;

public class PermissionsManager {
    private static final Map<UserType, Set<UserActions>> permissions=Map.of(
            UserType.ADMIN,Set.of(UserActions.values()),
            UserType.GUEST,Set.of(UserActions.SendMainRoomMessage,UserActions.ReceiveMainRoomMessage, UserActions.ChangeStatus),
            UserType.REGISTERED,Set.of(UserActions.SendMainRoomMessage,UserActions.ReceiveMainRoomMessage,UserActions.ReceivePersonalMessage,UserActions.SendPersonalMessage,UserActions.HasProfile, UserActions.ViewProfile, UserActions.ChangeStatus, UserActions.GetAllUsers),
            UserType.NOT_ACTIVATED,Set.of()
    );

    /**
     * Checks if given user type has permissions to perform given action.
     *
     * @param userType - UserType Enum, represents the type of user in the system.
     * @param userAction - UserActions Enum, represents an action a user can perform in the system.
     * @return true - if the given user type can perform the action, false - otherwise.
     */
    public static boolean hasPermission(UserType userType,UserActions userAction)
    {
        return permissions.get(userType).contains(userAction);
    }

    /**
     * Gives the User Type a new permission to perform the specified action in the system.
     *
     * @param userType - to user type to whom to assign a permission to perform the given action.
     * @param userAction - the action the given user type needs to be allowed to perform.
     */
    public static void AddPermission(UserType userType,UserActions userAction)
    {
        permissions.get(userType).add(userAction);
    }

    /**
     * Removes the User Type's existing permission to perform the specified action in the system.
     *
     * @param userType - to user type from whom to remove the permission to perform the given action.
     * @param userAction - the action the given user type will not be allowed to perform.
     */
    public static void RemovePermission(UserType userType,UserActions userAction)
    {
        permissions.get(userType).remove(userAction);
    }
}
