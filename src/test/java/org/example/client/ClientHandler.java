package org.example.client;

import org.json.JSONArray;
import org.json.JSONObject;

public class ClientHandler {
    User user;

    // Constructor to initialize the ClientController with a User object
    public ClientHandler(User user) {
        this.user = user;
    }

    // Method to handle login logic based on server response
    private User login(JSONObject response) {
        // If the login is successful
        if (response.getBoolean("status")) {
            // Update user attributes based on the response
            this.user.id = response.getInt("participant_id");
            this.user.username = response.getString("username");
            this.user.email = response.getString("email");
            this.user.registration_number = response.getString("registration_number");
            this.user.schoolName = response.getString("schoolName");
            this.user.isStudent = response.getBoolean("isStudent");
            this.user.isAuthenticated = response.getBoolean("isAuthenticated");

            // Set a success message for the user
            this.user.output = "✓✓ Successfully logged in as a " + this.user.username + (this.user.isStudent ? "(Student)" : "(School Representative)");
        } else {
            // Set a failure message for the user
            this.user.output = "!! " + response.get("reason").toString();
        }
        return this.user;
    }

    // Method to handle registration logic based on server response
    private User register(JSONObject response) {
        // If the registration is successful
        if (response.getBoolean("status")) {
            // Set a success message for the user
            this.user.output = "✓✓ " + response.get("reason").toString();
        } else {
            // Set a failure message for the user
            this.user.output = "!! " + response.get("reason").toString();
        }
        return this.user;
    }

    private User attemptChallenge(JSONObject response) {
        // Get the list of questions from the response
        JSONArray questions = response.getJSONArray("questions");

        // If there are no questions available
        if (questions.isEmpty()) {
            this.user.output = "!! There are no available questions in this challenge right now";
            return this.user;
        }

        int totalQuestions = questions.length();
        int timeAllocation = response.getInt("time_allocation"); // Assume this is in minutes
        long startTime = System.currentTimeMillis();

        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append("Challenge started. You have ").append(timeAllocation).append(" minutes to complete ")
                .append(totalQuestions).append(" questions.\n\n");

        for (int i = 0; i < totalQuestions; i++) {
            JSONObject question = new JSONObject(((JSONObject) questions.get(i)).toString(4));

            // Calculate remaining time
            long currentTime = System.currentTimeMillis();
            long elapsedTimeMinutes = (currentTime - startTime) / 60000;
            long remainingTimeMinutes = Math.max(0, timeAllocation - elapsedTimeMinutes);

            outputBuilder.append("Question ").append(i + 1).append(" of ").append(totalQuestions)
                    .append(" | Remaining time: ").append(remainingTimeMinutes).append(" minutes\n\n");
            outputBuilder.append(question.getString("id")).append(". ").append(question.getString("question")).append("\n\n");

            // Here you would typically wait for user input before moving to the next question
            // For demonstration, we'll just add a placeholder
            outputBuilder.append("(Answer input would go here)\n\n");

            // Check if time is up
            if (remainingTimeMinutes <= 0) {
                outputBuilder.append("Time's up! The challenge has ended.\n");
                break;
            }
        }

        this.user.output = outputBuilder.toString();
        return this.user;
    }

    // Method to handle viewing of challenges based on server response
    private User viewChallenges(JSONObject response) {
        // Get the list of challenges from the response
        JSONArray challenges = new JSONArray(response.getString("challenges"));

        // If there are no challenges available
        if (challenges.isEmpty()) {
            this.user.output = "!! There are no open challenges available right now";
            return this.user;
        }

        // Build a string to display the challenges
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nCHALLENGES \n\n");
        for (int i = 0; i < challenges.length(); i++) {
            JSONObject challenge = new JSONObject(((JSONObject) challenges.get(i)).toString(4));
            stringBuilder.append("challenge id: " + challenge.get("id") + "\nchallenge name: " + challenge.getString("name") + "\ndifficulty: " + challenge.getString("difficulty") + "\nclosing date: " + challenge.getString("closing_date") + "\t\tduration: " + challenge.getInt("time_allocation") + "\n\n\n");
        }

        stringBuilder.append("Attempt a particular challenge using the command:\n- attemptChallenge challengeNumber\n\n");

        this.user.output = stringBuilder.toString();

        return this.user;
    }

    // Method to handle confirmation logic based on server response
    private User confirm(JSONObject response) {
        // Set the output message based on the response status
        this.user.output = response.getString("reason");
        return this.user;
    }

    // Method to handle viewing of applicants based on server response
    private User viewApplicants(JSONObject response) {
        // Get the list of applicants from the response
        JSONArray participants = new JSONArray(response.getString("applicants"));

        // If there are no applicants available
        if (participants.isEmpty()) {
            this.user.output = "!! There are no Applicants awaiting the School representative's confirmation.";
            return this.user;
        }

        // Build a string to display the applicants
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.user.schoolName.strip().toUpperCase() + " (registration_number: " + this.user.registration_number + ")\n");
        stringBuilder.append("\nA list of Applicants awaiting your confirmation.\n");

        int count = 1;
        for (int i = 0; i < participants.length(); i++) {
            JSONObject participant = new JSONObject(((JSONObject) participants.get(i)).toString(4));
            stringBuilder.append(count + ". " + participant.getString("username") + " " + participant.getString("emailAddress") + "\n");
            count++;
        }

        stringBuilder.append("\nConfirm a participant using the commands below.\n");
        stringBuilder.append(" - confirm yes username\n");
        stringBuilder.append(" - confirm no username\n");

        this.user.output = stringBuilder.toString();

        return this.user;
    }

    // Main method to execute the appropriate action based on the command in the response data
    public User exec(String responseData) {
        JSONObject response = new JSONObject(responseData);
        switch (response.get("command").toString()) {
            case "login":
                return this.login(response);

            case "register":
                return this.register(response);

            case "attemptChallenge":
                return this.attemptChallenge(response);

            case "viewChallenges":
                return this.viewChallenges(response);

            case "confirm":
                return this.confirm(response);

            case "viewApplicants":
                return this.viewApplicants(response);

            default:throw new IllegalStateException("Invalid response");
        }
    }
}