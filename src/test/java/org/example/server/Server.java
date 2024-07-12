package org.example.server;

import java.io.IOException;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        // define server default port
        int port = 8080;

        try (// start server
             ServerSocket socket = new ServerSocket(port)) {

            System.out.println("Listening for connections");

            while (true) {
                Socket sock = socket.accept();

                System.out.println("New client connection");

                ServerThread serverThread = new ServerThread(sock);
                serverThread.start();

            }
<<<<<<< HEAD

=======
            // accept incoming connection and create a thread

            // start a thread for continous


            // accept client connection
>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
        } catch (Exception e) {
            e.printStackTrace();
        }

<<<<<<< HEAD
=======



>>>>>>> 6e134709888d204a57e0f83e1dcb2fc26d51d408
    }
}