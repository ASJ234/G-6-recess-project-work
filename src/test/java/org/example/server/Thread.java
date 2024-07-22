package org.example.server;

import com.itextpdf.text.DocumentException;
import org.json.JSONObject;

import javax.mail.MessagingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Thread {
    private Socket socket;

    // Constructor to initialize ServerThread with a socket
    public Thread(Socket socket) {
        this.socket = socket;
    }

    // Method to read JSON input from client
    public JSONObject readUserInput(BufferedReader input) throws IOException {
        String clientInput;
        StringBuilder clientIn = new StringBuilder();

        // Regular expression to match JSON format
        String regex = "^\\{.*\\}$";
        Pattern pattern = Pattern.compile(regex);

        // Read input line by line until JSON format is detected
        while ((clientInput = input.readLine()) != null) {
            if (pattern.matcher(clientInput).matches()) {
                clientIn.append(clientInput);
                break;
            }
            clientIn.append(clientInput);

            // Break loop when end of JSON object is detected
            if (clientInput.equals("}")) {
                break;
            }
        }

        // Parse JSON data into a JSONObject
        JSONObject jsonObject = new JSONObject(clientIn.toString().strip());
        return jsonObject;
    }

    // Method to start processing client requests
    public void start() throws IOException, MessagingException {
        System.out.println("Thread started");

        try (
                // Initialize input and output streams for communication with client
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true)
        ){

            JSONObject clientRequest;
            // Continuously read and process client requests
            while ((clientRequest = this.readUserInput(input)) != null) {

                Live live = new Live();
                live.check();

                System.out.println("Command received: " + clientRequest.toString());

                // Create a ServerController instance to handle client request
                ServerController controller = new ServerController(clientRequest);

                // Execute client request and get response
                String response = controller.run().toString();

                // Send response back to client
                output.println(response);
            }

        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } finally {
            // Close socket when done v
            socket.close();
        }
    }
}
