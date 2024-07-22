package org.example.server;

import java.io.IOException;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        // Define the server's default port
        int port = 8080;


        try ( // Create a server socket bound to the specified port
                ServerSocket socket = new ServerSocket(port)) {

            System.out.println("Listening for connections");

            // Server loop to continuously accept incoming client connections
            while (true) {
                Socket sock = socket.accept();

                System.out.println("New client connection");

                // Start a new thread to handle client communication
                Thread thread = new Thread(sock);
                thread.start();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}