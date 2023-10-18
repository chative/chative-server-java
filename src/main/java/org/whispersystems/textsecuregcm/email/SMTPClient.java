package org.whispersystems.textsecuregcm.email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class SMTPClient {

    public static void send(String server, int port,String from, String username, String password, List<String> to, String subject, String body)
            throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", server);
        prop.put("mail.smtp.port", String.valueOf(port));
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.enable", true);


        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setReplyTo(new Address[]{new InternetAddress(from)});
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(String.join(", ", to))
        );
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }

    public static void sendHtml(String server, int port,String from,  String username,String password, List<String> to, String subject, String body)
            throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", server);
        prop.put("mail.smtp.port", String.valueOf(port));
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.enable", true);


        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setReplyTo(new Address[]{new InternetAddress(from)});
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(String.join(", ", to))
        );
        message.setSubject(subject);
        Multipart mainPart = new MimeMultipart();
        BodyPart html = new MimeBodyPart();
        // 设置HTML内容
        html.setContent(body, "text/html; charset=utf-8");
        mainPart.addBodyPart(html);
        // 将MiniMultipart对象设置为邮件内容
        message.setContent(mainPart);
        Transport.send(message);
    }

    public static void main(String[] args) {
    }
}
