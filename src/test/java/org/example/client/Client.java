package org.example.client;

import java.io.IOException;

public class Client {
    // Define hostname and port number
    String hostname;
    int port;

    // Constructor to initialize the Client with hostname and port number
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    // Method to start a new client instance
    public ClientInstance startClientInstance() throws IOException {
        // Create a new User object
        User user = new User();
        // Create a new ClientInstance with hostname, port, and user
        ClientInstance clientInstance = new ClientInstance(hostname, port, user);
        // Start the client instance
        clientInstance.start();
        return clientInstance;
    }

    public static void main(String[] args) throws IOException {
        // Create a new Client object with hostname "localhost" and port 8080
        Client client = new Client("localhost", 8080);

        // Start a new client instance
        client.startClientInstance();
    }
}
