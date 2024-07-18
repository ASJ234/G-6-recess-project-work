package org.example.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.time.Duration;
import java.time.Instant;

public class ClientInstance {
    // Define attributes for the ClientInstance object
    String hostname;
    int port;
    String clientId;
    User user;
    byte cache;
    boolean isStudent;
    boolean isAuthenticated;

    // Constructor to initialize ClientInstance with hostname, port, and user
    public ClientInstance(String hostname, int port, User user) {
        this.hostname = hostname;
        this.port = port;
        this.user = user;
    }

    // Method to validate if the input string is a valid JSON object
    public static boolean isValid(String input) {
        String regex = "^\\{.*\\}$";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);

        return pattern.matcher(input).matches();
    }

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
        int totalQuestions = questions.length();
        Instant startTime = Instant.now();

        // Iterate through each question
        for (int i = 0; i < totalQuestions; i++) {
            JSONObject question = questions.getJSONObject(i);
            JSONObject answer = new JSONObject();
            this.cache += (byte) question.getInt("score"); // Add question score to cache

            // Calculate remaining questions and elapsed time
            int remainingQuestions = totalQuestions - i - 1;
            Instant currentTime = Instant.now();
            Duration elapsedTime = Duration.between(startTime, currentTime);

            // Display remaining questions and elapsed time
            System.out.println("Remaining Questions: " + remainingQuestions);
            System.out.println("Elapsed Time: " + elapsedTime.toMinutes() + " minutes");

            // Display question and score
            System.out.println(count + ". " + question.get("question") + " (" + question.get("score") + " Marks)");

            // Collect answer from user
            answer.put("question_id", question.getInt("id"));
            System.out.print(" = ");
            answer.put("answer", scanner.nextLine());

            // Add answer to solutions array
            solutions.put(answer);
            count++;
            System.out.print("\n");
        }
        return solutions;
    }

    // Method to start the client instance and interact with the server
    public void start() throws IOException {
        // Execute code for interacting with the server
        try (
                Socket socket = new Socket(hostname, port);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        ) {
            // Initialize client ID and serializer
            this.clientId = (String) socket.getInetAddress().getHostAddress();
            ObjectHandler objectHandler = new ObjectHandler(this.user);

            System.out.println();
            System.out.print("-------------------COMMANDS TO BE ENTERED---------------------------");
            System.out.println("\nregister username firstname lastname email_address dob(yyyy-mm-dd) registration_number image_path" +
                    "\nlogin\nviewApplicants(confirm yes username / confirm no username)\nviewChallenges\nattemptChallenges(attemptChallenge <challengeNo>)\nlogout");
            System.out.println("--------------------------------------------------------------------------");

            // Prompt user for a command
            System.out.print("Enter the command [" + this.user.username + "]: ");

            // Continuously read from the console and send to the server
            ClientHandler clientHandler = new ClientHandler(user);
            String regex = "^\\{.*\\}$";
            Pattern pattern = Pattern.compile(regex);

            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                // Handle logout command
                if (userInput.equals("logout") && (this.user.isAuthenticated)) {
                    System.out.println("✓✓ Session successfully logged out");
                    this.user.logout();
                    System.out.print("Enter the command [" + (!this.user.username.isBlank() ? this.user.username : null) + "]: ");
                    continue;
                }

                // Serialize the user input command
                String serializedCommand = objectHandler.serialize(userInput);

                // Check if the serialized command is valid
                if (isValid(serializedCommand)) {
                    // Send the command to the server
                    output.println(serializedCommand);

                    // Read response from the server
                    String response = input.readLine();

                    // Execute the response using the client controller
                    this.user = clientHandler.exec(response);

                    // Check if the user's output is a valid JSON object
                    if (!pattern.matcher(this.user.output).matches()) {
                        System.out.println("\n" + user.output + "\n");
                    } else {
                        // Parse questions from the user's output and collect answers
                        JSONObject questions = new JSONObject(this.user.output);
                        JSONArray answerSet = displayQuestionSet(questions);

                        // Create an object to send the answers back to the server
                        JSONObject obj = new JSONObject();
                        obj.put("attempt", answerSet);
                        obj.put("participant_id", this.user.id);
                        obj.put("command", "attempt");
                        obj.put("challenge_id", questions.getInt("challenge_id"));
                        obj.put("total_score", this.cache);

                        // Send the answers to the server
                        String inp = obj.toString();
                        output.println(inp);
                    }
                } else {
                    // Print invalid serialized command
                    System.out.println(serializedCommand);
                }
                // Prompt for the next instruction
                System.out.print("Enter the command [" + this.user.username + "]: ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Print connection timeout message
            System.out.println("Connection with the server timeout");
        }
    }
}
