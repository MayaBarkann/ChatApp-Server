package entityTests;

import chatApp.entities.PermissionsManager;
import chatApp.entities.UserActions;
import chatApp.entities.UserType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PermissionsManagerTests {

    //Admin permissions:
    @Test
    public void testHasPermission_userTypeAdmin_hasSendPersonalMessagePermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.ADMIN, UserActions.SendPersonalMessage), "User type is ADMIN, but doesn't have SendPersonalMessage permission.");
    }

    @Test
    public void testHasPermission_userTypeAdmin_hasSendMainRoomMessagePermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.ADMIN, UserActions.SendMainRoomMessage), "User type is ADMIN, but doesn't have SendMainRoomMessage permission.");
    }

    @Test
    public void testHasPermission_userTypeAdmin_hasMuteOrUnmuteOthersPermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.ADMIN, UserActions.MuteOrUnmuteOthers), "User type is ADMIN, but doesn't have MuteOrUnmuteOthers permission.");
    }

    @Test
    public void testHasPermission_userTypeAdmin_hasReceiveMainRoomMessagePermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.ADMIN, UserActions.ReceiveMainRoomMessage), "User type is ADMIN, but doesn't have ReceiveMainRoomMessage permission.");
    }

    @Test
    public void testHasPermission_userTypeAdmin_hasReceiveReceivePersonalMessagePermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.ADMIN, UserActions.ReceivePersonalMessage), "User type is ADMIN, but doesn't have ReceivePersonalMessage permission.");
    }

    @Test
    public void testHasPermission_userTypeAdmin_hasProfile() {
        assertTrue(PermissionsManager.hasPermission(UserType.ADMIN, UserActions.HasProfile), "User type is ADMIN, but doesn't have a profile.");
    }

    @Test
    public void testHasPermission_userTypeAdmin_hasViewProfilePermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.ADMIN, UserActions.ViewProfile), "User type is ADMIN, but doesn't have ViewProfile permission.");
    }

    @Test
    public void testHasPermission_userTypeAdmin_hasChangeStatusPermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.ADMIN, UserActions.ChangeStatus), "User type is ADMIN, but doesn't have ChangeStatus permission.");
    }

    @Test
    public void testHasPermission_userTypeAdmin_hasGetAllUsersPermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.ADMIN, UserActions.GetAllUsers), "User type is ADMIN, but doesn't have GetAllUsers permission.");
    }

    //Registered user permissions:
    @Test
    public void testHasPermission_userTypeRegistered_hasSendPersonalMessagePermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.SendPersonalMessage), "User type is REGISTERED, but doesn't have SendPersonalMessage permission.");
    }

    @Test
    public void testHasPermission_userTypeRegistered_hasSendMainRoomMessagePermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.SendMainRoomMessage), "User type is REGISTERED, but doesn't have SendMainRoomMessage permission.");
    }

    @Test
    public void testHasPermission_userTypeRegistered_hasReceiveMainRoomMessagePermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.ReceiveMainRoomMessage), "User type is REGISTERED, but doesn't have ReceiveMainRoomMessage permission.");
    }

    @Test
    public void testHasPermission_userTypeRegistered_hasReceiveReceivePersonalMessagePermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.ReceivePersonalMessage), "User type is REGISTERED, but doesn't have ReceivePersonalMessage permission.");
    }

    @Test
    public void testHasPermission_userTypeRegistered_hasProfile() {
        assertTrue(PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.HasProfile), "User type is REGISTERED, but doesn't have a profile.");
    }

    @Test
    public void testHasPermission_userTypeRegistered_hasViewProfilePermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.ViewProfile), "User type is REGISTERED, but doesn't have ViewProfile permission.");
    }

    @Test
    public void testHasPermission_userTypeRegistered_hasChangeStatusPermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.ChangeStatus), "User type is REGISTERED, but doesn't have ChangeStatus permission.");
    }

    @Test
    public void testHasPermission_userTypeRegistered_hasGetAllUsersPermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.GetAllUsers), "User type is REGISTERED, but doesn't have GetAllUsers permission.");
    }

    @Test
    public void testHasPermission_userTypeRegistered_hasNoMuteOrUnmuteOthersPermission() {
        assertFalse(PermissionsManager.hasPermission(UserType.REGISTERED, UserActions.MuteOrUnmuteOthers), "User type is REGISTERED, but has MuteOrUnmuteOthers permission.");
    }

    //Guest user permissions:
    @Test
    public void testHasPermission_userTypeGuest_hasSendPersonalMessagePermission() {
        assertFalse(PermissionsManager.hasPermission(UserType.GUEST, UserActions.SendPersonalMessage), "User type GUEST shouldn't have SendPersonalMessage permission.");
    }

    @Test
    public void testHasPermission_userTypeGuest_hasSendMainRoomMessagePermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.GUEST, UserActions.SendMainRoomMessage), "User type is GUEST, but doesn't have SendMainRoomMessage permission.");
    }

    @Test
    public void testHasPermission_userTypeGuest_hasReceiveMainRoomMessagePermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.GUEST, UserActions.ReceiveMainRoomMessage), "User type is GUEST, but doesn't have ReceiveMainRoomMessage permission.");
    }

    @Test
    public void testHasPermission_userTypeGuest_hasReceiveReceivePersonalMessagePermission() {
        assertFalse(PermissionsManager.hasPermission(UserType.GUEST, UserActions.ReceivePersonalMessage), "User type GUEST shouldn't have ReceivePersonalMessage permission.");
    }

    @Test
    public void testHasPermission_userTypeGuest_hasProfile() {
        assertFalse(PermissionsManager.hasPermission(UserType.GUEST, UserActions.HasProfile), "User type GUEST shouldn't have a profile.");
    }

    @Test
    public void testHasPermission_userTypeGuest_hasViewProfilePermission() {
        assertFalse(PermissionsManager.hasPermission(UserType.GUEST, UserActions.ViewProfile), "User type GUEST shouldn't have ViewProfile permission.");
    }

    @Test
    public void testHasPermission_userTypeGuest_hasChangeStatusPermission() {
        assertTrue(PermissionsManager.hasPermission(UserType.GUEST, UserActions.ChangeStatus), "User type is GUEST, but doesn't have ChangeStatus permission.");
    }

    @Test
    public void testHasPermission_userTypeGuest_hasGetAllUsersPermission() {
        assertFalse(PermissionsManager.hasPermission(UserType.GUEST, UserActions.GetAllUsers), "User type GUEST shouldn't have GetAllUsers permission.");
    }

    @Test
    public void testHasPermission_userTypeGuest_hasNoMuteOrUnmuteOthersPermission() {
        assertFalse(PermissionsManager.hasPermission(UserType.GUEST, UserActions.MuteOrUnmuteOthers), "User type GUEST shouldn't have MuteOrUnmuteOthers permission.");
    }

    //Not activated user permissions:
    @Test
    public void testHasPermission_userTypeNotActivated_hasNoPermissions() {
        assertFalse(PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.MuteOrUnmuteOthers), "User type NOT_ACTIVATED shouldn't have MuteOrUnmuteOthers permission.");
        assertFalse(PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.SendPersonalMessage), "User type NOT_ACTIVATED shouldn't have SendPersonalMessage permission.");
        assertFalse(PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.ReceivePersonalMessage), "User type NOT_ACTIVATED shouldn't have ReceivePersonalMessage permission.");
        assertFalse(PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.SendMainRoomMessage), "User type NOT_ACTIVATED shouldn't have SendMainRoomMessage permission.");
        assertFalse(PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.ReceiveMainRoomMessage), "User type NOT_ACTIVATED shouldn't have ReceiveMainRoomMessage permission.");
        assertFalse(PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.HasProfile), "User type NOT_ACTIVATED shouldn't have HasProfile permission.");
        assertFalse(PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.ViewProfile), "User type NOT_ACTIVATED shouldn't have ViewProfile permission.");
        assertFalse(PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.ChangeStatus), "User type NOT_ACTIVATED shouldn't have ChangeStatus permission.");
        assertFalse(PermissionsManager.hasPermission(UserType.NOT_ACTIVATED, UserActions.GetAllUsers), "User type NOT_ACTIVATED shouldn't have GetAllUsers permission.");
    }
}
