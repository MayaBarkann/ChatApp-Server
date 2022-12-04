package entities;

import chatApp.entities.MessageAbility;
import chatApp.entities.User;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertSame;


public class UserTests {
    private User user;

    @Test
    public void testToggleMessageAbility_userIsMuted_userBecomesUnmuted(){
        user = new User("fsfds@gmail.com","fdsf%$D1","testUser");
        user.setMessageAbility(MessageAbility.MUTED);
        user.toggleMessageAbility();
        assertSame(user.getMessageAbility(), MessageAbility.UNMUTED, "User was muted, but after toggleMessageAbility didn't become unmuted.");
    }

    @Test
    public void testToggleMessageAbility_userIsUnmuted_userBecomesMuted(){
        user = new User("fsfds@gmail.com","fdsf%$D1","testUser");
        user.setMessageAbility(MessageAbility.UNMUTED);
        user.toggleMessageAbility();
        assertSame(user.getMessageAbility(), MessageAbility.MUTED, "User was unmuted, but after toggleMessageAbility didn't become muted.");
    }

}
