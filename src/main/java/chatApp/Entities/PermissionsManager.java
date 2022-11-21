package chatApp.Entities;

import java.util.Map;
import java.util.Set;

public class PermissionsManager {
    private static final Map<UserType, Set<UserActions>> permissions=Map.of(
            UserType.ADMIN,Set.of(UserActions.values()),
            UserType.GUEST,Set.of(UserActions.SendMainRoomMessage),
            UserType.REGISTERED,Set.of(UserActions.SendMainRoomMessage,UserActions.SendPersonalMessage,UserActions.HasProfile),
            UserType.NOT_ACTIVATED,Set.of()
    );
    public static boolean hasPermission(UserType userType,UserActions userAction)
    {
        return permissions.get(userType).contains(userAction);
    }
    public static void AddPermission(UserType userType,UserActions userAction)
    {
        permissions.get(userType).add(userAction);
    }
    public static void RemovePermission(UserType userType,UserActions userAction)
    {
        permissions.get(userType).remove(userAction);
    }
}
