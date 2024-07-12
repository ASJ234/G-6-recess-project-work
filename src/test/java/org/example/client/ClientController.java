package org.example.client;

import org.json.JSONArray;
import org.json.JSONObject;

public class ClientController {
    User user;


    public ClientController(User user) {
        this.user = user;
    }

    private User login(JSONObject response) {
        // logic to interpret server response in attempt to login
        if (response.getBoolean("status")) {
            this.user.id = response.getInt("participant_id");
            this.user.username = response.getString("username");
            this.user.email = response.getString("email");
            this.user.regNo = response.getString("regNo");
            this.user.schoolName = response.getString("schoolName");
            this.user.isStudent = response.getBoolean("isStudent");
            this.user.isAuthenticated = response.getBoolean("isAuthenticated");
<<<<<<< HEAD
            this.user.output = "[+] Successfully logged in as a " + this.user.username + (this.user.isStudent ? "(Student)" : "(School Representative)");
=======
            this.user.output = "[+] Successfully logged in as a " + this.user.username + (this.user.isStudent ? "(student)" : "(representative)");
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
        } else {
            this.user.output = "[-] " + response.get("reason").toString();
        }
        return this.user;
    }

    private User register(JSONObject response) {
        // logic to interpret server response in attempt to register
        if (response.getBoolean("status")) {
            this.user.output = "[+] " + response.get("reason").toString();
        } else {
            this.user.output = "[-] " + response.get("reason").toString();
        }
        return this.user;
    }

    private User attemptChallenge(JSONObject response) {
        // logic to interpret server response in attempt to attempt challenge
        JSONArray questions = response.getJSONArray("questions");

        if (questions.isEmpty()) {
            this.user.output = "[-] No available questions in this challenge right now";
            return this.user;
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\nQUESTIONS \n\n");
        for (int i = 0; i < questions.length(); i++) {
            JSONObject question = new JSONObject(((JSONObject) questions.get(i)).toString(4));
            stringBuilder.append(question.get("id") + ". " + question.getString("question") + "\n\n");
        }

        this.user.output = response.toString();

        return this.user;
    }

    private User viewChallenges(JSONObject response) {
        // logic to interpret server response in attempt to view challenges
        JSONArray challenges = new JSONArray(response.getString("challenges"));

        if (challenges.isEmpty()) {
            this.user.output = "[-] No open challenges are available right now";
            return this.user;
        }

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

    private User confirm(JSONObject response) {
        // logic to interpret server response in attempt to confirm
        if (response.getBoolean("status")) {
            this.user.output = response.getString("reason");
        } else {
            this.user.output = response.getString("reason");
        }
        return this.user;
    }

    private User viewApplicants(JSONObject response) {
        // logic to interpret server response in attempt to view applicants
        JSONArray participants = new JSONArray(response.getString("applicants"));

        if (participants.isEmpty()) {
            this.user.output = "[-] No pending participant registration requests";
            return this.user;
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(this.user.schoolName.strip().toUpperCase() + " (registration number: " + this.user.regNo + ")\n");
        stringBuilder.append("\n");
        stringBuilder.append("Pending applicants:\n");

        int count = 1;
        for (int i = 0; i < participants.length(); i++) {
            JSONObject participant = new JSONObject(((JSONObject) participants.get(i)).toString(4));
            stringBuilder.append(count + ". " + participant.getString("username") + " " + participant.getString("emailAddress") + "\n");
            count++;
        }

        stringBuilder.append("\n");
        stringBuilder.append("Confirm a student using the commands\n");
        stringBuilder.append(" - confirm yes <username>\n");
        stringBuilder.append(" - confirm no <username>\n");

        this.user.output = stringBuilder.toString();

        return this.user;
    }

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