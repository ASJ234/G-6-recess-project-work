package org.example.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ClientInstance {
<<<<<<< HEAD
    // Define attributes for the ClientInstance object
=======
    // define attributes for the ClientInstance object
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
    String hostname;
    int port;
    String clientId;
    User user;
    byte cache;
    boolean isStudent;
    boolean isAuthenticated;

<<<<<<< HEAD
    // Constructor to initialize ClientInstance with hostname, port, and user
    public ClientInstance(String hostname, int port, User user) {
=======
    public ClientInstance(String hostname, int port, User user) {
        // constructor class for the client instance
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
        this.hostname = hostname;
        this.port = port;
        this.user = user;
    }

<<<<<<< HEAD
    // Method to validate if the input string is a valid JSON object
=======
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
    public static boolean isValid(String input) {
        String regex = "^\\{.*\\}$";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);

        return pattern.matcher(input).matches();
    }

<<<<<<< HEAD
    // Method to display a set of questions from a challenge object and collect answers from the user
    public JSONArray displayQuestionSet(JSONObject challengeObj) {
        // Print challenge details
        System.out.println("CHALLENGE " + challengeObj.getInt("challenge_id") + " (" + challengeObj.get("challenge_name") + ")");
        Scanner scanner = new Scanner(System.in);

        // Get questions from challenge object and initialize solutions array
        JSONArray questions = challengeObj.getJSONArray("questions");
        JSONArray solutions = new JSONArray();
        this.cache = 0; // Reset cache
        int count = 1;

        // Iterate through each question
        for (int i = 0; i < questions.length(); i++) {
            JSONObject question = questions.getJSONObject(i);
            JSONObject answer = new JSONObject();
            this.cache += (byte) question.getInt("score"); // Add question score to cache

            // Display question and score
            System.out.println(count + ". " + question.get("question") + " (" + question.get("score") + " Marks)");

            // Collect answer from user
=======
    public JSONArray displayQuestionSet(JSONObject challengeObj) {
        System.out.println("CHALLENGE " + challengeObj.getInt("challenge_id") + " (" + challengeObj.get("challenge_name") + ")");
        Scanner scanner = new Scanner(System.in);

        JSONArray questions = challengeObj.getJSONArray("questions");
        JSONArray solutions = new JSONArray();
        this.cache = 0;
        int count = 1;
        for (int i = 0; i < questions.length(); i++) {
            JSONObject question = questions.getJSONObject(i);
            JSONObject answer = new JSONObject();
            this.cache += (byte) question.getInt("score");

            System.out.println(count + ". " + question.get("question") + " (" + question.get("score") + " Marks)");

>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
            answer.put("question_id", question.getInt("id"));
            System.out.print(" - ");
            answer.put("answer", scanner.nextLine());

<<<<<<< HEAD
            // Add answer to solutions array
=======
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
            solutions.put(answer);
            count++;
            System.out.print("\n");
        }
        return solutions;
    }

<<<<<<< HEAD
    // Method to start the client instance and interact with the server
    public void start() throws IOException {
        // Execute code for interacting with the server
=======
    public void start() throws IOException {
        // Todo: create a parent menu

        // execute code for interacting with the server
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
        try (
                Socket socket = new Socket(hostname, port);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        ) {
<<<<<<< HEAD
            // Initialize client ID and serializer
            this.clientId = (String) socket.getInetAddress().getHostAddress();
            Serializer serializer = new Serializer(this.user);

            // Prompt user for a command
            System.out.print("[Enter the command] (" + this.user.username + "): ");
=======
            this.clientId = (String) socket.getInetAddress().getHostAddress();
            Serializer serializer = new Serializer(this.user);

            System.out.print("[Enter the command] (" + this.user.username + "): ");
            // read command line input
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408

            // Continuously read from the console and send to the server
            ClientController clientController = new ClientController(user);
            String regex = "^\\{.*\\}$";
            Pattern pattern = Pattern.compile(regex);

<<<<<<< HEAD
            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                // Handle logout command
=======

            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                // send command to the server

>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
                if (userInput.equals("logout") && (this.user.isAuthenticated)) {
                    System.out.println("Session successfully logged out");
                    this.user.logout();
                    System.out.print("[Enter the command] (" + (!this.user.username.isBlank() ? this.user.username : null) + "): ");
                    continue;
                }

<<<<<<< HEAD
                // Serialize the user input command
                String serializedCommand = serializer.serialize(userInput);

                // Check if the serialized command is valid
                if (isValid(serializedCommand)) {
                    // Send the command to the server
                    output.println(serializedCommand);

                    // Read response from the server
                    String response = input.readLine();

                    // Execute the response using the client controller
                    this.user = clientController.exec(response);

                    // Check if the user's output is a valid JSON object
                    if (!pattern.matcher(this.user.output).matches()) {
                        System.out.println("\n" + user.output + "\n");
                    } else {
                        // Parse questions from the user's output and collect answers
                        JSONObject questions = new JSONObject(this.user.output);
                        JSONArray answerSet = displayQuestionSet(questions);

                        // Create an object to send the answers back to the server
=======
                String serializedCommand = serializer.serialize(userInput);

                if (isValid(serializedCommand)) {
                    output.println(serializedCommand);

                    // read response here from the server
                    String response = input.readLine();

                    this.user = clientController.exec(response);

                    if (!pattern.matcher(this.user.output).matches()) {
                        System.out.println("\n" + user.output + "\n");
                    } else {
                        JSONObject questions = new JSONObject(this.user.output);
                        JSONArray answerSet = displayQuestionSet(questions);

>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
                        JSONObject obj = new JSONObject();
                        obj.put("attempt", answerSet);
                        obj.put("participant_id", this.user.id);
                        obj.put("command", "attempt");
                        obj.put("challenge_id", questions.getInt("challenge_id"));
                        obj.put("total_score", this.cache);

<<<<<<< HEAD
                        // Send the answers to the server
=======
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
                        String inp = obj.toString();
                        output.println(inp);
                    }
                } else {
<<<<<<< HEAD
                    // Print invalid serialized command
                    System.out.println(serializedCommand);
                }
                // Prompt for the next instruction
=======
                    System.out.println(serializedCommand);
                }
                // prompt for the next instruction
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
                System.out.print("[Enter the command] (" + this.user.username + "): ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
<<<<<<< HEAD
            // Print connection timeout message
            System.out.println("Connection with the server timeout");
        }
    }
}
=======
            System.out.println("Connection with the server timeout");
        }
    }
}
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
