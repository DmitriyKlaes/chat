package ru.chat.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MessageSender implements Runnable {

    private final Socket client;
    private final PrintWriter output;
    private final Scanner consoleInput;

    public MessageSender(Socket client) throws IOException {
        this.client = client;
        this.output = new PrintWriter(client.getOutputStream(), true);
        this.consoleInput = new Scanner(System.in);
    }

    @Override
    public void run() {
        System.out.print("Введите ник: ");
        String nickname = consoleInput.nextLine();
        this.output.println(nickname);
        while (consoleInput.hasNextLine() && !client.isClosed()) {
            String message = consoleInput.nextLine();
            if (message.equals("quit")) {
                try {
                    this.output.println(message);
                    this.client.close();
                    this.consoleInput.close();
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            this.output.println(message);
        }
    }
}