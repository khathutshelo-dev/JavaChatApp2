package chatapp;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        try {

            Socket socket = new Socket("localhost", 5000);

            BufferedReader serverInput = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            PrintWriter output = new PrintWriter(
                    socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            output.println(username);

            // Thread for receiving messages
            Thread receiveThread = new Thread(() -> {

                String message;

                try {

                    while ((message = serverInput.readLine()) != null) {

                        System.out.println(message);
                    }

                } catch (IOException e) {

                    System.out.println("Disconnected from server.");
                }
            });

            receiveThread.start();

            // Sending messages
            while (true) {

                String message = scanner.nextLine();

                output.println(message);
            }

        } catch (IOException e) {

            System.out.println("Client Error: " + e.getMessage());
        }
    }
}
