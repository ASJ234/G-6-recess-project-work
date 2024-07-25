package org.example.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ObjectHandler {
    User user;

    // Constructor to initialize the Serializer with a User object
    public ObjectHandler(User user) {
        this.user = user;
    }

    // Method to handle the login process
    public String login() {
        if (this.user.isAuthenticated) {
            return "Session already authenticated";
        }

        // Collect user login details
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter user login details.");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.println("\n");
        String[] tokens = new String[3];
        tokens[0] = "login";
        tokens[1] = username;
        tokens[2] = email;

        // Create JSON object for login command
        JSONObject obj = new JSONObject();
        obj.put("command", "login");
        obj.put("isAuthenticated", false);
        obj.put("tokens", tokens);
        obj.put("isStudent", false);

        return obj.toString(4);
    }

    // Method to handle the registration process
    public String register(String[] arr) {
        // Create JSON object for register command
        JSONObject obj = new JSONObject();
        obj.put("command", "register");
        obj.put("isAuthenticated", user.isAuthenticated);
        obj.put("tokens", arr);
        obj.put("tokenized_image", tokenizeImage(arr[7]));
        obj.put("isStudent", user.isStudent);

        return obj.toString(4);
    }



//Method where image is broken down into 4kb arrays
    private static JSONObject tokenizeImage(String path) {
        JSONObject jsonObject = new JSONObject();
        JSONArray arr = new JSONArray();

        File file = new File(path);
        if (!file.exists()) {
            return new JSONObject();
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[4 * 1024];
            int bytesRead;


            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                JSONObject obj = new JSONObject();
                byte[] bufferCopy = new byte[bytesRead];
                System.arraycopy(buffer, 0, bufferCopy, 0, bytesRead);

                obj.put("buffer", bufferCopy);
                obj.put("size", bytesRead);
                arr.put(obj);
            }

            jsonObject.put("data", arr);
            jsonObject.put("size", new File(path).length());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // Method to handle the attempt challenge process
    public String attemptChallenge(String[] tokens) {
        // Create JSON object for attemptChallenge command
        JSONObject obj = new JSONObject();
        obj.put("command", "attemptChallenge");
        obj.put("tokens", tokens);

        return obj.toString(4);
    }

    // Method to handle viewing applicants
    public String viewApplicants() {
        // Create JSON object for viewApplicants command
        JSONObject obj = new JSONObject();
        obj.put("command", "viewApplicants");
        obj.put("isAuthenticated", this.user.isAuthenticated);
        obj.put("isStudent", this.user.isStudent);
        obj.put("registration_number", this.user.registration_number);

        return obj.toString(4);
    }

    // Method to handle confirmation process
    public String confirm(String[] arr) {
        // Create JSON object for confirm command
        JSONObject obj = new JSONObject();
        obj.put("command", "confirm");
        obj.put("username", arr[2]);
        obj.put("registration_number", this.user.registration_number);
        obj.put("confirm", (arr[1].toLowerCase().equals("yes")) ? true : false);
        obj.put("tokens", arr);

        return obj.toString(4);
    }

    // Method to handle viewing challenges
    public String viewChallenges() {
        // Create JSON object for viewChallenges command
        JSONObject obj = new JSONObject();
        obj.put("command", "viewChallenges");
        obj.put("isAuthenticated", this.user.isAuthenticated);
        obj.put("isStudent", this.user.isStudent);

        return obj.toString(4);
    }

    // Method to handle logout process
    public String logout() {
        this.user.logout();
        return "You have Successfully logged out";
    }

    // Method to serialize commands and execute appropriate actions
    public String serialize(String command) {
        String[] tokens = command.split("\\s+");

        if (!user.isAuthenticated && tokens[0].equals("register")) {
            return this.register(tokens);
        }

        if (!user.isAuthenticated && tokens[0].equals("login")) {
            return this.login();
        }

        if (!user.isAuthenticated) {
            return "Session unauthenticated. First login by entering command login";
        }

        if (user.isStudent) {
            switch (tokens[0]) {
                case "logout":
                    return this.logout();

                case "viewChallenges":
                    return this.viewChallenges();

                case "attemptChallenge":
                    return this.attemptChallenge(tokens);

                default:
                    return "Invalid student command";
            }
        } else {
            switch (tokens[0]) {
                case "logout":
                    return this.logout();

                case "confirm":
                    return this.confirm(tokens);

                case "viewApplicants":
                    return this.viewApplicants();

                default:
                    return "Invalid school representative command";
            }
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        ObjectHandler sample = new ObjectHandler(new User());

        // Test serialize method with login command
        sample.serialize("login sossy asj@gmail.com");

        // Test serialize method with another login command
        sample.serialize("login ariko arikojoel1@gmail.com");
    }
}
