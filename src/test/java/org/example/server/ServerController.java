package org.example.server;

import org.json.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerController {
    JSONObject obj;

    // Constructor to initialize ServerController with JSONObject
    public ServerController(JSONObject obj) {
        this.obj = obj;
    }

    // Method to handle login logic
    private JSONObject login(JSONObject obj) throws SQLException, ClassNotFoundException {
        // Initialize database connection
        DatabaseConnection dbConnection = new DatabaseConnection();

        // Extract username and email from tokens
        JSONArray tokens = obj.getJSONArray("tokens");
        String username = tokens.get(1).toString();
        String email = tokens.get(2).toString();

        // Prepare client response JSON object
        JSONObject clientResponse = new JSONObject();
        clientResponse.put("command", "login");
        clientResponse.put("username", username);
        clientResponse.put("email", email);

        // Query participant table for matching username and email
        String readParticipantQuery = "SELECT * FROM participants";
        ResultSet participantResultSet = dbConnection.read(readParticipantQuery);
        while (participantResultSet.next()) {
            if (username.equals(participantResultSet.getString("username")) &&
                    email.equals(participantResultSet.getString("emailAddress"))) {
                // Matching participant found
                String registration_number = participantResultSet.getString("registration_number");

                // Populate client response for participant
                clientResponse.put("participant_id", participantResultSet.getInt("participant_id"));
                clientResponse.put("registration_number", registration_number);
                clientResponse.put("schoolName", "undefined");
                clientResponse.put("isStudent", true);
                clientResponse.put("isAuthenticated", true);
                clientResponse.put("status", true);

                return clientResponse;
            }
        }

        // Query school table for matching representative username and email
        String readRepresentativeQuery = "SELECT * FROM schools";
        ResultSet representativeResultSet = dbConnection.read(readRepresentativeQuery);
        while (representativeResultSet.next()) {
            if (username.equals(representativeResultSet.getString("representative_name")) &&
                    email.equals(representativeResultSet.getString("representative_email"))) {
                // Matching representative found
                String schoolName = representativeResultSet.getString("name");
                String registration_number = representativeResultSet.getString("registration_number");

                // Populate client response for representative
                clientResponse.put("participant_id", 0);
                clientResponse.put("schoolName", schoolName);
                clientResponse.put("registration_number", registration_number);
                clientResponse.put("isStudent", false);
                clientResponse.put("isAuthenticated", true);
                clientResponse.put("status", true);

                return clientResponse;
            }
        }

        // No matching credentials found
        clientResponse.put("isStudent", false);
        clientResponse.put("isAuthenticated", false);
        clientResponse.put("status", false);
        clientResponse.put("reason", "Invalid credentials. Check the details provided");

        return clientResponse;
    }

    // Method to handle student registration logic
    private JSONObject register(JSONObject obj) throws IOException, MessagingException, SQLException, ClassNotFoundException {
        // Initialize email agent and database connection
        EmailSending emailAgent = new EmailSending();
        DatabaseConnection dbConnection = new DatabaseConnection();

        // Extract registration details from tokens
        JSONArray tokens = obj.getJSONArray("tokens");
        JSONObject participantObj = new JSONObject();
        participantObj.put("username", tokens.get(1));
        participantObj.put("firstname", tokens.get(2));
        participantObj.put("lastname", tokens.get(3));
        participantObj.put("emailAddress", tokens.get(4));
        participantObj.put("dob", tokens.get(5));
        participantObj.put("registration_number", tokens.get(6));
        participantObj.put("imagePath", tokens.get(7));

        // Prepare client response JSON object
        JSONObject clientResponse = new JSONObject();
        clientResponse.put("command", "register");

        // Check if the participant has been rejected before
        ResultSet rejectedParticipant = dbConnection.getRejectedParticipant(
                participantObj.getString("username"),
                participantObj.getString("emailAddress"),
                participantObj.getString("registration_number")
        );

        if (rejectedParticipant.next()) {
            // Participant has been rejected before
            clientResponse.put("status", false);
            clientResponse.put("reason", "You have been previously rejected from registering under this school. Registration denied.");
            return clientResponse;
        }

        // Query representative table to get representative email
        ResultSet rs = dbConnection.getRepresentative(participantObj.getString("registration_number"));
        String representativeEmail;
        if (rs.next()) {
            representativeEmail = rs.getString("representative_email");
        } else {
            // If no representative found for given regNo
            clientResponse.put("status", false);
            clientResponse.put("reason", "The school registration number does not match registered school numbers");
            return clientResponse;
        }

        // Initialize file storage for participants
        FileStorage fileStorage = new FileStorage("participantsfile.json");
        if (!fileStorage.read().toString().contains(participantObj.toString())) {
            // Add participant if not already exists
            fileStorage.add(participantObj);
            clientResponse.put("status", true);
            clientResponse.put("reason", "Participant created successfully awaiting School representative approval");
            // Send registration request email to representative
            emailAgent.sendParticipantRegistrationRequestEmail(representativeEmail, participantObj.getString("emailAddress"), participantObj.getString("username"));

            return clientResponse;
        }

        // Participant already exists
        clientResponse.put("status", false);
        clientResponse.put("reason", "Participant creation failed. An existing participant object found");

        return clientResponse;
    }



    // Method to handle attempting a challenge
    private JSONObject attemptChallenge(JSONObject obj) throws SQLException, ClassNotFoundException {
        // Prepare client response JSON object
        JSONObject clientResponse = new JSONObject();
        JSONArray questions = new JSONArray();

        // Initialize database connection
        DatabaseConnection dbConnection = new DatabaseConnection();

        // Extract challengeId from tokens
        int challengeId = Integer.parseInt((String) new JSONArray(obj.get("tokens").toString()).get(1));
        ResultSet challengeQuestions;
        challengeQuestions = dbConnection.getChallengeQuestions(challengeId);

        // Get challenge details including time allocation
        ResultSet challengeDetails = dbConnection.getChallengeDetails(challengeId);
        int timeAllocation = 0;
        if (challengeDetails.next()) {
            timeAllocation = challengeDetails.getInt("duration_minutes");
        }

        // Iterate through challenge questions and add to client response
        while (challengeQuestions.next()) {
            JSONObject question = new JSONObject();
            question.put("id", challengeQuestions.getString("question_id"));
            question.put("question", challengeQuestions.getString("question"));
            question.put("score", challengeQuestions.getString("score"));

            questions.put(question);
        }

        // Populate client response with challenge details
        clientResponse.put("command", "attemptChallenge");
        clientResponse.put("questions", questions);
        clientResponse.put("challenge_id", challengeId);
        clientResponse.put("challenge_name", challengeId);
        clientResponse.put("time_allocation", timeAllocation);  // Add time allocation to the response
        return clientResponse;
    }

    // Method to view available challenges
    private JSONObject viewChallenges(JSONObject obj) throws SQLException, ClassNotFoundException {
        // Prepare client response JSON object
        JSONObject clientResponse = new JSONObject();

        // Initialize database connection
        DatabaseConnection dbConnection = new DatabaseConnection();

        // Retrieve available challenges from database
        ResultSet availableChallenges = dbConnection.getChallenges();
        JSONArray challenges = new JSONArray();

        // Iterate through available challenges and add to client response
        while (availableChallenges.next()) {
            JSONObject challenge = new JSONObject();
            challenge.put("id", availableChallenges.getInt("challenge_id"));
            challenge.put("name", availableChallenges.getString("title"));
            challenge.put("difficulty", availableChallenges.getString("description"));
            challenge.put("time_allocation", availableChallenges.getInt("duration_minutes"));
            challenge.put("starting_date", availableChallenges.getDate("starting_date"));
            challenge.put("closing_date", availableChallenges.getDate("closing_date"));

            challenges.put(challenge);
        }

        // Populate client response with challenges
        clientResponse.put("command", "viewChallenges");
        clientResponse.put("challenges", challenges.toString());

        return clientResponse;
    }

    // Method to confirm or reject registered participants
    private JSONObject confirm(JSONObject obj) throws IOException, SQLException, ClassNotFoundException {
        // Initialize file storage for participants
        FileStorage fileStorage = new FileStorage("participantsfile.json");

        // Extract username from object
        String username = obj.getString("username");
        JSONObject participant = fileStorage.readEntryByUserName(username);

        // Prepare client response JSON object
        JSONObject clientResponse = new JSONObject();
        clientResponse.put("command", "confirm");

        // Check if participant exists
        if (participant.isEmpty()) {
            clientResponse.put("status", false);
            clientResponse.put("reason", "Invalid command. Check the username provided");
            return clientResponse;
        }

        // Initialize database connection and email agent
        DatabaseConnection dbConnection = new DatabaseConnection();
        EmailSending emailAgent = new EmailSending();

        // Confirm or reject participant based on 'confirm' flag
        if (obj.getBoolean("confirm")) {
            dbConnection.createParticipant(participant.getString("username"), participant.getString("firstname"),
                    participant.getString("lastname"), participant.getString("emailAddress"),
                    participant.getString("dob"), participant.getString("registration_number"), participant.getString("imagePath"));
            fileStorage.deleteEntryByUserName(username);
            clientResponse.put("reason", participant.getString("firstname") + " " + participant.getString("lastname") +
                    " (" + participant.getString("emailAddress") + ") confirmed successfully");

            // Send confirmation email
            try {
                emailAgent.sendConfirmedParticipantEmail(participant.getString("emailAddress"), participant.getString("username"));
            } catch (MessagingException e) {
                // Log the error, but don't stop the confirmation process
                System.err.println("Failed to send confirmation email: " + e.getMessage());
            }
        } else {
            dbConnection.createParticipantRejected(participant.getString("username"), participant.getString("firstname"),
                    participant.getString("lastname"), participant.getString("emailAddress"),
                    participant.getString("dob"), participant.getString("registration_number"), participant.getString("imagePath"));
            fileStorage.deleteEntryByUserName(username);
            clientResponse.put("reason", participant.getString("firstname") + " " + participant.getString("lastname") +
                    " (" + participant.getString("emailAddress") + ") rejected successfully");

            // Send rejection email
            try {
                emailAgent.sendRejectedParticipantEmail(participant.getString("emailAddress"), participant.getString("username"));
            } catch (MessagingException e) {
                // Log the error, but don't stop the rejection process
                System.err.println("Failed to send rejection email: " + e.getMessage());
            }
        }
        clientResponse.put("status", true);
        return clientResponse;
    }

    // Method to view applicants based on school's registration number
    private JSONObject viewApplicants(JSONObject obj) throws IOException {
        // Extract registration number from object
        String registration_number = obj.getString("registration_number");

        // Initialize file storage for participants
        FileStorage fileStorage = new FileStorage("participantsfile.json");

        // Filter participants by registration number
        String participants = fileStorage.filterParticipantsByRegNo(registration_number);

        // Prepare client response JSON object
        JSONObject clientResponse = new JSONObject();
        clientResponse.put("command", "viewApplicants");
        clientResponse.put("applicants", participants);

        return clientResponse;
    }

    // Method to handle challenge attempt
    public JSONObject attempt(JSONObject obj) throws SQLException, ClassNotFoundException {
        JSONArray attempt = obj.getJSONArray("attempt");
        int challengeId = obj.getInt("challenge_id");
        int participantId = obj.getInt("participant_id");

        DatabaseConnection dbConnection = new DatabaseConnection();

        int totalScore = 0;
        int score = 0;

        int questionId = 0;
        for (int i = 0; i < attempt.length(); i++) {
            JSONObject answerObj = attempt.getJSONObject(i);
            questionId = answerObj.getInt("question_id");
            String answer = answerObj.getString("answer");

            ResultSet correctAnswer = dbConnection.getCorrectAnswer(questionId);

            dbConnection.ChallengeAttempt(participantId, challengeId, questionId);

            if (correctAnswer.next()) {
                int questionScore = correctAnswer.getInt("score");
                String correctContent = correctAnswer.getString("answer");

                if (answer.equals("-")) {
                    // Participant is not sure, award 0 for this question
                    // No change in score
                } else if (answer.equals(correctContent)) {
                    // Correct answer, add full score
                    score += questionScore;
                } else {
                    // Wrong answer, deduct 3 marks
                    score -= 3;
                }

                totalScore += questionScore;
            }
        }

        // Ensure score doesn't go below 0
        score = Math.max(0, score);

        // Create challenge attempt in database
        dbConnection.createChallengeAttempt(participantId, challengeId, score, totalScore);


        JSONObject response = new JSONObject();
        response.put("command", "attemptResult");
        response.put("score", score);
        response.put("totalScore", totalScore);
        response.put("reason", "✓✓ Your marks have been recorded in our database a detailed report will be provided for you though your email once the challenge is done");

        return response;
    }

    // Main method to run appropriate logic based on command received
    public JSONObject run() throws IOException, SQLException, ClassNotFoundException, MessagingException {
        switch (this.obj.get("command").toString()) {
            case "login":
                // Call login logic
                return this.login(this.obj);

            case "register":
                // Call registration logic
                return this.register(this.obj);

            case "viewChallenges":
                // Call view challenges logic
                return this.viewChallenges(this.obj);

            case "attemptChallenge":
                // Call attempt challenge logic
                return this.attemptChallenge(this.obj);

            case "confirm":
                // Call confirmation logic
                return this.confirm(this.obj);

            case "viewApplicants":
                // Call view applicants logic
                return this.viewApplicants(this.obj);

            case "attempt":
                // Call attempt logic
                return this.attempt(this.obj);

            default:
                // Unrecognized command
                JSONObject outputObj = new JSONObject();
                outputObj.put("command", "exception");
                outputObj.put("reason", "Invalid command");

                return outputObj;
        }
    }
}