package demo.tool.emailsender;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class GmailClient {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static  NetHttpTransport HTTP_TRANSPORT = null;
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
    public static void main(String[] args) {
        System.out.println("Hello World!");
        try {
            //GoogleCredential credential = GoogleCredential.fromStream(
            //        Files.newInputStream(Paths.get("/Users/user/Downloads/client_secret_1033422975321-usg29toj4uctbggkpbmiutjvhj9q2bip.apps.googleusercontent.com.json")))
            //        .createScoped(SCOPES);
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(
                            Files.newInputStream(Paths.get("/Users/user/Downloads/client_secret_1033422975321-usg29toj4uctbggkpbmiutjvhj9q2bip.apps.googleusercontent.com.json"))
                    ));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
            //returns an authorized Credential object.
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            service.users().messages().send("me",
                    createMessageWithEmail(
                    createEmail("ian@difft.org","","how are you", "hello"))).execute();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        Multipart mainPart = new MimeMultipart();
        BodyPart html = new MimeBodyPart();
        // 设置HTML内容
        html.setContent("<h1>This is actual message embedded in \n" +
                "   HTML tags</h1>", "text/html; charset=utf-8");
        mainPart.addBodyPart(html);
        // 将MiniMultipart对象设置为邮件内容
        email.setContent(mainPart);

        return email;
    }
         private static Message createMessageWithEmail(MimeMessage emailContent)
             throws javax.mail.MessagingException, IOException {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         emailContent.writeTo(buffer);
         byte[] bytes = buffer.toByteArray();
         String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
         Message message = new Message();
         message.setRaw(encodedEmail);
         return message;
     }

}
