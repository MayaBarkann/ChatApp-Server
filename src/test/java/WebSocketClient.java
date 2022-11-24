//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.concurrent.CountDownLatch;
//import javax.websocket.ClientEndpoint;
//import javax.websocket.CloseReason;
//
//import javax.websocket.OnClose;
//import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
//import javax.websocket.Session;
//import org.glassfish.tyrus.client.ClientManager;
//
//@ClientEndpoint
//public class WebSocketClient {
//    private  WebSocketClient() {
//    }
//    private static CountDownLatch latch;
//
//    @OnOpen
//    public void onOpen(Session session) {
//        System.out.println ("--- Connected " + session.getId());
//        try {
//            session.getBasicRemote().sendText("start");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @OnMessage
//    public String onMessage(String message, Session session) {
//        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            System.out.println ("--- Received " + message);
//            String userInput = bufferRead.readLine();
//            return userInput;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @OnClose
//    public void onClose(Session session, CloseReason closeReason) {
//        System.out.println("Session " + session.getId() +
//                " closed because " + closeReason);
//        latch.countDown();
//    }
//
//    public static WebSocketClient start(String url) {
//        latch = new CountDownLatch(1);
//        ClientManager client = ClientManager.createClient();
//        try {
//            URI uri = new URI(url);
//            client.connectToServer(WebSocketClient.class, uri);
//            latch.await();
//        } catch (URISyntaxException | InterruptedException | jakarta.websocket.DeploymentException | IOException e) {
//            e.printStackTrace();
//        }
//        return new WebSocketClient();
//    }
//}