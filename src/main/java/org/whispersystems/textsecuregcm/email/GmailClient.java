// package org.whispersystems.textsecuregcm.email;
//
// import com.google.api.client.auth.oauth2.Credential;
// import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
// import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
// import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
// import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
// import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
// import com.google.api.client.http.javanet.NetHttpTransport;
// import com.google.api.client.json.JsonFactory;
// import com.google.api.client.json.jackson2.JacksonFactory;
// import com.google.api.client.util.store.FileDataStoreFactory;
// import com.google.api.services.gmail.Gmail;
// import com.google.api.services.gmail.GmailScopes;
// import com.google.api.services.gmail.model.Message;
// import com.google.api.services.gmail.model.Profile;
// import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
// import org.apache.commons.codec.binary.Base64;
//
// import javax.activation.DataHandler;
// import javax.activation.DataSource;
// import javax.activation.FileDataSource;
// import javax.mail.Multipart;
// import javax.mail.internet.*;
// import javax.mail.Session;
// import java.io.*;
// import java.security.GeneralSecurityException;
// import java.util.Collections;
// import java.util.List;
// import java.util.Properties;
//
//
// public class GmailClient {
//     private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
//     private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//     private static final String TOKENS_DIRECTORY_PATH = "tokens";
//
//     /**
//      * Global instance of the scopes required by this quickstart.
//      * If modifying these scopes, delete your previously saved tokens/ folder.
//      */
//     private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS);
//     private static final String CREDENTIALS_FILE_PATH = "credentials.json";
//
//     private Gmail service = null;
//
//     public GmailClient(String credentials) throws GeneralSecurityException, IOException {
//         this.service = getClient(credentials);
//     }
//
//     private Gmail getClient(String credentials) throws GeneralSecurityException, IOException {
//         final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//         return new Gmail(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, credentials));
//     }
//
//     /**
//      * Creates an authorized Credential object.
//      * @param HTTP_TRANSPORT The network HTTP Transport.
//      * @return An authorized Credential object.
//      * @throws IOException If the credentials.json file cannot be found.
//      */
//     private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String credential) throws IOException {
//         GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(credential));
//
//         // Build flow and trigger user authorization request.
//         GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                 HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                 .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
//                 .setAccessType("offline")
//                 .build();
//         LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//         return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//     }
//
//     /**
//      * Create a MimeMessage using the parameters provided.
//      *
//      * @param to email address of the receiver
//      * @param from email address of the sender, the mailbox account
//      * @param subject subject of the email
//      * @param bodyText body text of the email
//      * @return the MimeMessage to be used to send email
//      * @throws MessagingException
//      */
//     public MimeMessage createEmail(String to,
//                                           String from,
//                                           String subject,
//                                           String bodyText)
//             throws MessagingException, javax.mail.MessagingException {
//         Properties props = new Properties();
//         Session session = Session.getDefaultInstance(props, null);
//
//         MimeMessage email = new MimeMessage(session);
//
//         email.setFrom(new InternetAddress(from));
//         email.addRecipient(javax.mail.Message.RecipientType.TO,
//                 new InternetAddress(to));
//         email.setSubject(subject);
//         email.setText(bodyText);
//         return email;
//     }
//
//     /**
//      * Create a message from an email.
//      *
//      * @param emailContent Email to be set to raw of message
//      * @return a message containing a base64url encoded email
//      * @throws IOException
//      * @throws MessagingException
//      */
//     private Message createMessageWithEmail(MimeMessage emailContent)
//             throws javax.mail.MessagingException, IOException {
//         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//         emailContent.writeTo(buffer);
//         byte[] bytes = buffer.toByteArray();
//         String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
//         Message message = new Message();
//         message.setRaw(encodedEmail);
//         return message;
//     }
//
//     /**
//      * Create a MimeMessage using the parameters provided.
//      *
//      * @param to Email address of the receiver.
//      * @param from Email address of the sender, the mailbox account.
//      * @param subject Subject of the email.
//      * @param bodyText Body text of the email.
//      * @param file Path to the file to be attached.
//      * @return MimeMessage to be used to send email.
//      * @throws MessagingException
//      */
//     private MimeMessage createEmailWithAttachment(String to,
//                                                         String from,
//                                                         String subject,
//                                                         String bodyText,
//                                                         File file)
//             throws MessagingException, IOException, javax.mail.MessagingException {
//         Properties props = new Properties();
//         Session session = Session.getDefaultInstance(props, null);
//
//         MimeMessage email = new MimeMessage(session);
//
//         email.setFrom(new InternetAddress(from));
//         email.addRecipient(javax.mail.Message.RecipientType.TO,
//                 new InternetAddress(to));
//         email.setSubject(subject);
//
//         MimeBodyPart mimeBodyPart = new MimeBodyPart();
//         mimeBodyPart.setContent(bodyText, "text/plain");
//
//         Multipart multipart = new MimeMultipart();
//         multipart.addBodyPart(mimeBodyPart);
//
//         mimeBodyPart = new MimeBodyPart();
//         DataSource source = new FileDataSource(file);
//
//         mimeBodyPart.setDataHandler(new DataHandler(source));
//         mimeBodyPart.setFileName(file.getName());
//
//         multipart.addBodyPart(mimeBodyPart);
//         email.setContent(multipart);
//
//         return email;
//     }
//
//     /**
//      * Send an email from the user's mailbox to its recipient.
//      *
//      * @param userId User's email address. The special value "me"
//      * can be used to indicate the authenticated user.
//      * @param emailContent Email to be sent.
//      * @return The sent message
//      * @throws MessagingException
//      * @throws IOException
//      */
//     private Message sendMessage(String userId, MimeMessage emailContent)
//             throws MessagingException, IOException, javax.mail.MessagingException {
//         Message message = createMessageWithEmail(emailContent);
//         message = service.users().messages().send(userId, message).execute();
//
//         System.out.println("Message id: " + message.getId());
//         System.out.println(message.toPrettyString());
//         return message;
//     }
//
//     public Message sendEmail(String to, String subject, String body)
//             throws MessagingException, javax.mail.MessagingException, IOException {
//         return sendEmail(to, subject, body, null);
//     }
//
//     public Message sendEmail(String to, String subject, String body, File file)
//             throws MessagingException, javax.mail.MessagingException, IOException {
//         // Profile profile = service.users().getProfile("me").execute();
//
//         MimeMessage mimeMessage;
//         if (null == file) {
//             mimeMessage = createEmail(to, "me", subject, body);
//         } else {
//             mimeMessage = createEmailWithAttachment(to, "me", subject, body, file);
//         }
//
//         return sendMessage("me", mimeMessage);
//     }
// }
