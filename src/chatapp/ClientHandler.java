package chatapp;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String username;

    public ClientHandler(Socket socket) {

        this.socket = socket;

        try {

            input = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            output = new PrintWriter(
                    socket.getOutputStream(), true);

            username = input.readLine();

            System.out.println(username + " joined the chat.");

            Server.broadcast(username + " joined the chat!");

        } catch (IOException e) {

            System.out.println("Handler Error: " + e.getMessage());
        }
    }

    @Override
    public void run() {

        String message;

        try {

            while ((message = input.readLine()) != null) {

                System.out.println(username + ": " + message);

                Server.broadcast(username + ": " + message);
            }

        } catch (IOException e) {

            System.out.println(username + " disconnected.");
        }
    }

    public void sendMessage(String message) {

        output.println(message);
    }

    public String getUsername() {

        return username;
    }
}