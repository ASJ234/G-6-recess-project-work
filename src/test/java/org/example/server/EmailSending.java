package org.example.server;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSending {
    String host = "smtp.gmail.com";
<<<<<<< HEAD
    String username = "G-6-recess-project.portal@gmail.com";
    String from = "G-6-recess-project.portal@gmail.com";
=======
    String username = "eelection3.portal@gmail.com";
    String from = "eelection3.portal@gmail.com";
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
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
<<<<<<< HEAD
        message.setSubject("Notification of Participant registration under your school");
=======
        message.setSubject("Notification of registration under your school");
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408

        StringBuilder emailMessage = new StringBuilder();
        emailMessage.append("New participant notification\n\n");
        emailMessage.append("This message is to notify you of a new participant's request to register under your school\n\n");
        emailMessage.append("The participant details are as below\n");
        emailMessage.append("\tUsername: ").append(username).append("\n");
        emailMessage.append("\temail: ").append(participantEmail).append("\n");
<<<<<<< HEAD
        emailMessage.append("\nTo verify this participant please login into the command line interface and confirm the participants with the commands below\n");
=======
        emailMessage.append("\nTo verify this participant please login into the command line and confirm with the commands\n");
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
        emailMessage.append("\tconfirm with:-> confirm yes/no " + username + "\n");

        message.setText(emailMessage.toString());
        Transport.send(message);
    }

<<<<<<< HEAD
=======
//    public static void main(String[] args) throws MessagingException {
//        EmailSending email = new EmailSending();
//        email.sendParticipantRegistrationRequestEmail("ogenrwothjimfrank@gmail.com", "frank@gmail.com", "frank");
//    }
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
}
