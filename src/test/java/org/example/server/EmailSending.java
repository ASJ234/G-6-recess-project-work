package org.example.server;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSending {
    String host = "smtp.gmail.com";
    String username = "G-6-recess-project.portal@gmail.com";
    String from = "G-6-recess-project.portal@gmail.com";
    String password = "mngv mtyl wmzk exmi";
    Session session;

    public EmailSending() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        String username = this.username;
        String password = this.password;
        this.session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        this.session.setDebug(true);
    }

    public void sendParticipantRegistrationRequestEmail(String to, String participantEmail, String username) throws MessagingException {
        MimeMessage message = new MimeMessage(this.session);

        message.setFrom(new InternetAddress(this.from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("Notification of Participant registration under your school");

        StringBuilder emailMessage = new StringBuilder();
        emailMessage.append("New participant notification\n\n");
        emailMessage.append("This message is to notify you of a new participant's request to register under your school\n\n");
        emailMessage.append("The participant details are as below\n");
        emailMessage.append("\tUsername: ").append(username).append("\n");
        emailMessage.append("\temail: ").append(participantEmail).append("\n");
        emailMessage.append("\nTo verify this participant please login into the command line interface and confirm the participants with the commands below\n");
        emailMessage.append("\tconfirm with:-> confirm yes/no " + username + "\n");

        message.setText(emailMessage.toString());
        Transport.send(message);
    }

}
