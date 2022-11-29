import chatApp.entities.PermissionsManager;
import chatApp.entities.UserActions;
import chatApp.entities.UserType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PermissionTests {

    @Test
    public void testHasPermission_userTypeAdmin_hasSendMainRoomMessagePermission() {
        boolean responseAdmin = PermissionsManager.hasPermission(UserType.ADMIN, UserActions.SendMainRoomMessage);
        boolean responseRegister = PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.MuteOrUnmuteOthers);
        boolean responseGuest = PermissionsManager.hasPermission(UserType.GUEST, UserActions.ReceivePersonalMessage);
        boolean responseNotActivated = PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.SendMainRoomMessage);
        assertTrue(responseAdmin, "admin should have permission");
        assertFalse(responseRegister, "register shouldn't have permission to mute others.");
        assertFalse(responseGuest, "guest shouldn't have permission to receive personal message.");
        assertFalse(responseNotActivated, "not activated user shouldn't have permission to send message to main room.");

    }

    @Test
    public void testHasPermission_userTypeAdmin_hasSendPersonalMessagePermission() {
        boolean responseAdmin = PermissionsManager.hasPermission(UserType.ADMIN, UserActions.SendMainRoomMessage);
        boolean responseRegister = PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.MuteOrUnmuteOthers);
        boolean responseGuest = PermissionsManager.hasPermission(UserType.GUEST, UserActions.ReceivePersonalMessage);
        boolean responseNotActivated = PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.SendMainRoomMessage);
        assertTrue(responseAdmin, "admin should have permission");
        assertFalse(responseRegister, "register shouldn't have permission to mute others.");
        assertFalse(responseGuest, "guest shouldn't have permission to receive personal message.");
        assertFalse(responseNotActivated, "not activated user shouldn't have permission to send message to main room.");
    }

    @Test
    public void testHasPermission_userTypeAdmin_hasMuteOrUnmuteOthersPermission() {
        boolean responseAdmin = PermissionsManager.hasPermission(UserType.ADMIN, UserActions.SendMainRoomMessage);
        boolean responseRegister = PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.MuteOrUnmuteOthers);
        boolean responseGuest = PermissionsManager.hasPermission(UserType.GUEST, UserActions.ReceivePersonalMessage);
        boolean responseNotActivated = PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.SendMainRoomMessage);
        assertTrue(responseAdmin, "admin should have permission");
        assertFalse(responseRegister, "register shouldn't have permission to mute others.");
        assertFalse(responseGuest, "guest shouldn't have permission to receive personal message.");
        assertFalse(responseNotActivated, "not activated user shouldn't have permission to send message to main room.");

    }

    @Test
    public void testHasPermission_userTypeAdmin_hasReceiveMainRoomMessagePermission() {
        boolean responseAdmin = PermissionsManager.hasPermission(UserType.ADMIN, UserActions.SendMainRoomMessage);
        boolean responseRegister = PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.MuteOrUnmuteOthers);
        boolean responseGuest = PermissionsManager.hasPermission(UserType.GUEST, UserActions.ReceivePersonalMessage);
        boolean responseNotActivated = PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.SendMainRoomMessage);
        assertTrue(responseAdmin, "admin should have permission");
        assertFalse(responseRegister, "register shouldn't have permission to mute others.");
        assertFalse(responseGuest, "guest shouldn't have permission to receive personal message.");
        assertFalse(responseNotActivated, "not activated user shouldn't have permission to send message to main room.");
    }

    @Test
    public void testHasPermission_userTypeAdmin_hasReceiveReceivePersonalMessagePermission() {
        boolean responseAdmin = PermissionsManager.hasPermission(UserType.ADMIN, UserActions.SendMainRoomMessage);
        boolean responseRegister = PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.MuteOrUnmuteOthers);
        boolean responseGuest = PermissionsManager.hasPermission(UserType.GUEST, UserActions.ReceivePersonalMessage);
        boolean responseNotActivated = PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.SendMainRoomMessage);
        assertTrue(responseAdmin, "admin should have permission");
        assertFalse(responseRegister, "register shouldn't have permission to mute others.");
        assertFalse(responseGuest, "guest shouldn't have permission to receive personal message.");
        assertFalse(responseNotActivated, "not activated user shouldn't have permission to send message to main room.");
    }
}
