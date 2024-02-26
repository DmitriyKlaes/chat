package ru.chat.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class MessageHandler implements Runnable {

    private final Socket client;
    private final InputStream input;

    public MessageHandler(Socket client) throws IOException {
        this.client = client;
        this.input = client.getInputStream();
    }

    @Override
    public void run() {
        try (Scanner scn = new Scanner(this.input)) {
            while (scn.hasNextLine() && !client.isClosed()) {
                System.out.println(scn.nextLine());
            }
        }
    }
}
