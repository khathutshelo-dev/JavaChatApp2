package chatapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

    static Vector<ClientHandler> clients = new Vector<>();

    public static void main(String[] args) {

        try {

            ServerSocket serverSocket = new ServerSocket(5000);

            System.out.println("=================================");
            System.out.println(" Chat Server Started");
            System.out.println(" Port: 5000");
            System.out.println(" Waiting for clients...");
            System.out.println("=================================");

            while (true) {

                Socket socket = serverSocket.accept();

                System.out.println("New client connected!");

                ClientHandler clientThread = new ClientHandler(socket);

                clients.add(clientThread);

                clientThread.start();
            }

        } catch (IOException e) {

            System.out.println("Server Error: " + e.getMessage());
        }
    }

    public static void broadcast(String message) {

        for (ClientHandler client : clients) {

            client.sendMessage(message);
        }
    }
}