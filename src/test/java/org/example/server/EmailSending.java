package org.example.server;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;



public class EmailSending {
    // SMTP server host
    String host = "smtp.gmail.com";

    // Gmail account credentials for sending emails
    String username = "arikojoel3@gmail.com";
    String from = "arikojoel3@gmail.com";
    String password = "ywaz twyx iieb ojds";
    Session session;

    // Constructor to set up email session
    public EmailSending() {
        // Set up properties for the SMTP session
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Initialize session with authentication
        String username = this.username;
        String password = this.password;
        this.session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Enable debugging output for the session
        this.session.setDebug(true);
    }

    // Method to send participant registration request email
    public void sendParticipantRegistrationRequestEmail(String to, String participantEmail, String username) throws MessagingException {
        // Create a new MimeMessage for sending email
        MimeMessage message = new MimeMessage(this.session);

        // Set sender and recipient addresses
        message.setFrom(new InternetAddress(this.from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        // Set email subject
        message.setSubject("Notification of Participant registration under your school");

        // Construct email message body
        StringBuilder emailMessage = new StringBuilder();
        emailMessage.append("New participant notification\n\n");
        emailMessage.append("This message is to notify you of a new participant's request to register under your school\n\n");
        emailMessage.append("The participant details are as below:\n");
        emailMessage.append("\tUsername: ").append(username).append("\n");
        emailMessage.append("\tEmail: ").append(participantEmail).append("\n\n");
        emailMessage.append("To verify this participant, please login into the command line interface and confirm using the following commands:\n");
        emailMessage.append("\tconfirm with:- confirm yes/no ").append(username).append("\n");

        // Set email message text
        message.setText(emailMessage.toString());

        // Send the email message
        Transport.send(message);
    }


        // Method to send email to rejected participants
        public void sendRejectedParticipantEmail(String to, String username) throws MessagingException {
            MimeMessage message = new MimeMessage(this.session);

            message.setFrom(new InternetAddress(this.from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject("Registration Status: Not Approved");

            StringBuilder emailMessage = new StringBuilder();
            emailMessage.append("Dear ").append(username).append(",\n\n");
            emailMessage.append("We regret to inform you that your registration for the mathematics challenge competition has not been approved.\n\n");
            emailMessage.append("Your registration was reviewed by the school representative and was not confirmed at this time.\n\n");
            emailMessage.append("If you believe this decision was made in error or if you have any questions, please contact your school representative for more information.\n\n");
            emailMessage.append("Thank you for your interest in the mathematics challenge competition.\n\n");
            emailMessage.append("Best regards,\nThe mathematics challenge competition Team");

            message.setText(emailMessage.toString());

            Transport.send(message);
        }

        // Method to send email to confirmed participants
        public void sendConfirmedParticipantEmail(String to, String username) throws MessagingException {
            MimeMessage message = new MimeMessage(this.session);

            message.setFrom(new InternetAddress(this.from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject("Registration Status: Approved");

            StringBuilder emailMessage = new StringBuilder();
            emailMessage.append("Dear ").append(username).append(",\n\n");
            emailMessage.append("Congratulations! Your registration for the mathematics challenge competition has been approved.\n\n");
            emailMessage.append("Your registration was reviewed and confirmed by the school representative. You are now officially registered as a participant.\n\n");
            emailMessage.append("Next steps:\n");
            emailMessage.append("1. Log in to the mathematics challenge competition platform through the command line interface using your registered credentials(username and email).\n");
            emailMessage.append("2. Familiarize yourself with the available challenges and their requirements.\n");
            emailMessage.append("3. Start participating in the challenges when they become available.\n\n");
            emailMessage.append("If you have any questions or need assistance, please don't hesitate to contact our support team.\n\n");
            emailMessage.append("We wish you the best of luck in the mathematics challenge competition!\n\n");
            emailMessage.append("Best regards,\nThe mathematics challenge competition Team");

            message.setText(emailMessage.toString());

            Transport.send(message);
        }



}
