package chatApp.service;

import chatApp.entities.Response;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.*;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

import java.util.Objects;
import java.util.Properties;
import java.util.Set;

@Service
public class EmailSender {
    private final Gmail service;

    /**
     * Empty Constructor for EmailSender. Initializes the Gmail service.
     */
    public EmailSender() {
        NetHttpTransport httpTransport = null;
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            service = new Gmail.Builder(httpTransport, jsonFactory, this.getCredentials(httpTransport, jsonFactory))
                    .setApplicationName("ChatApp")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport.
     * @param jsonFactory   Parses Jason data.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport httpTransport, GsonFactory jsonFactory)
            throws IOException {
        // Load client secrets.
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(Objects.requireNonNull(EmailSender.class.getResourceAsStream("/client_secret.json"))));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Set.of(GmailScopes.GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Creates an email with the given subject and body message, and sends it to the given destination email.
     *
     * @param subject     Subject of email.
     * @param msg         Body of email.
     * @param destination Email address that needs to receive the email.
     * @return Response<String> object, contains: if action successful - data=destination email, isSucceed=true, message=null; if action failed - data=null, isSucceed=false, message=reason for failure.
     */
    public Response<String> sendMail(String subject, String msg, String destination) {
        // Encode as MIME message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        try {
            email.setFrom(new InternetAddress("chat.app3000@gmail.com"));
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(destination));
            email.setSubject(subject);
            email.setText(msg);
        } catch (MessagingException e) {
            return Response.createFailureResponse("Failed to create email message." + e);
        }

        // Encode and wrap the MIME message into a gmail message
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            email.writeTo(buffer);
        } catch (IOException | MessagingException e) {
            return Response.createFailureResponse("Failed to write message to ByteArrayOutputStream." + e);
        }
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        try {
            // Create send message
            message = this.service.users().messages().send("me", message).execute();
            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                return Response.createFailureResponse("Unable to send message: " + e.getDetails());
            } else {
                try {
                    throw e;
                } catch (GoogleJsonResponseException ex) {
                    return Response.createFailureResponse(e.getDetails().toString());
                }
            }
        } catch (IOException e) {
            return Response.createFailureResponse("Unable to send email to " + destination + e);
        }
        return Response.createSuccessfulResponse(destination);
    }

}

