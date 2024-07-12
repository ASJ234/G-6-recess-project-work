package org.example.client;

import org.json.JSONArray;
import org.json.JSONObject;

public class ClientController {
    User user;

    // Constructor to initialize the ClientController with a User object
    public ClientController(User user) {
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
            this.user.regNo = response.getString("regNo");
            this.user.schoolName = response.getString("schoolName");
            this.user.isStudent = response.getBoolean("isStudent");
            this.user.isAuthenticated = response.getBoolean("isAuthenticated");

            // Set a success message for the user
            this.user.output = "[+] Successfully logged in as a " + this.user.username + (this.user.isStudent ? "(Student)" : "(School Representative)");
        } else {
            // Set a failure message for the user
            this.user.output = "[-] " + response.get("reason").toString();
        }
        return this.user;
    }

    // Method to handle registration logic based on server response
    private User register(JSONObject response) {
        // If the registration is successful
        if (response.getBoolean("status")) {
            // Set a success message for the user
            this.user.output = "[+] " + response.get("reason").toString();
        } else {
            // Set a failure message for the user
            this.user.output = "[-] " + response.get("reason").toString();
        }
        return this.user;
    }

    // Method to handle challenge attempt logic based on server response
    private User attemptChallenge(JSONObject response) {
        // Get the list of questions from the response
        JSONArray questions = response.getJSONArray("questions");

        // If there are no questions available
        if (questions.isEmpty()) {
            this.user.output = "[-] No available questions in this challenge right now";
            return this.user;
        }

        // Build a string to display the questions
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nQUESTIONS \n\n");
        for (int i = 0; i < questions.length(); i++) {
            JSONObject question = new JSONObject(((JSONObject) questions.get(i)).toString(4));
            stringBuilder.append(question.get("id") + ". " + question.getString("question") + "\n\n");
        }

        this.user.output = response.toString();

        return this.user;
    }

    // Method to handle viewing of challenges based on server response
    private User viewChallenges(JSONObject response) {
        // Get the list of challenges from the response
        JSONArray challenges = new JSONArray(response.getString("challenges"));

        // If there are no challenges available
        if (challenges.isEmpty()) {
            this.user.output = "[-] No open challenges are available right now";
            return this.user;
        }

        // Build a string to display the challenges
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nCHALLENGES \n\n");
        for (int i = 0; i < challenges.length(); i++) {
            JSONObject challenge = new JSONObject(((JSONObject) challenges.get(i)).toString(4));
            stringBuilder.append("challenge id: " + challenge.get("id") + "\nchallenge name: " + challenge.getString("name") + "\ndifficulty: " + challenge.getString("difficulty") + "\nclosing date: " + challenge.getString("closing_date") + "\t\tduration: " + challenge.getInt("time_allocation") + "\n\n\n");
        }

        stringBuilder.append("Attempt a particular challenge using the command:\n-> attemptChallenge <challenge_id>\n\n");

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
            this.user.output = "[-] No pending participant registration requests";
            return this.user;
        }

        // Build a string to display the applicants
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.user.schoolName.strip().toUpperCase() + " (registration number: " + this.user.regNo + ")\n");
        stringBuilder.append("\nPending applicants:\n");

        int count = 1;
        for (int i = 0; i < participants.length(); i++) {
            JSONObject participant = new JSONObject(((JSONObject) participants.get(i)).toString(4));
            stringBuilder.append(count + ". " + participant.getString("username") + " " + participant.getString("emailAddress") + "\n");
            count++;
        }

        stringBuilder.append("\nConfirm a student using the commands\n");
        stringBuilder.append(" - confirm yes <username>\n");
        stringBuilder.append(" - confirm no <username>\n");

        this.user.output = stringBuilder.toString();

        return this.user;
    }

    // Main method to execute the appropriate action based on the command in the response data
    public User exec(String responseData) {
        JSONObject response = new JSONObject(responseData);
        switch (response.get("command").toString()) {
            case "login" -> {
                return this.login(response);
            }
            case "register" -> {
                return this.register(response);
            }
            case "attemptChallenge" -> {
                return this.attemptChallenge(response);
            }
            case "viewChallenges" -> {
                return this.viewChallenges(response);
            }
            case "confirm" -> {
                return this.confirm(response);
            }
            case "viewApplicants" -> {
                return this.viewApplicants(response);
            }
            default -> throw new IllegalStateException("Invalid response");
        }
    }
}
