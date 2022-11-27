import chatApp.entities.PermissionsManager;
import chatApp.entities.Response;
import chatApp.entities.UserActions;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import chatApp.service.PermissionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;



public class PermissionTest {
    /*
     insert into CHATAPP.user(id,username,email,password,userType) values(4,"Nota-User","usie@gmail3.com","password","NOT_ACTIVATED");
     insert into CHATAPP.user(id,username,email,password,userType) values(3,"guest-User","usie@gmail4.com","password","GUEST");
     insert into CHATAPP.user(id,username,email,password,userType) values(2,"register-User","usie@gmail1.com","password","REGISTERED");
     insert into CHATAPP.user(id,username,email,password,userType) values(1,"Super-User","usie@gmail.com","password","ADMIN");
     */

    @Test
    public void testPermission() {
        boolean responseAdmin = PermissionsManager.hasPermission(UserType.ADMIN, UserActions.SendMainRoomMessage);
        boolean responseRegister = PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.MuteOrUnmuteOthers);
        boolean responseGuest = PermissionsManager.hasPermission(UserType.GUEST, UserActions.ReceivePersonalMessage);
        boolean responseNotActivated = PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.SendMainRoomMessage);
        Assertions.assertTrue(responseAdmin, "admin should have permission");
        Assertions.assertFalse(responseRegister, "register shouldn't have permission to mute others.");
        Assertions.assertFalse(responseGuest, "guest shouldn't have permission to receive personal message.");
        Assertions.assertFalse(responseNotActivated, "not activated user shouldn't have permission to send message to main room.");

    }
}
