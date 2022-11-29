

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Scanner;


public class MainRoomTest {
    private static int Port = 8080;
    private static String URL = "ws://localhost:" + Port+"/ws";
    private static String MainRoomURL = URL + "/topic/mainChat";
    private static StandardWebSocketClient client;
    @BeforeAll
    public static void beforeAll() {
        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        client  = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());


        stompClient.connect(URL, sessionHandler);

        new Scanner(System.in).nextLine();
    }
    @Test
    public void testMainRoomSendMessage() {

        }




}
