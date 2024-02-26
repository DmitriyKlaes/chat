package ru.chat.client;

import java.io.IOException;
import java.net.Socket;

public class Client implements ClientConfiguration {

    private final Socket client;

    public Client() throws IOException {
        this.client = new Socket(HOST, PORT);
        System.out.println("Успешное подключение к чат-серверу!");
    }

    public void run() throws IOException {
        new Thread(new MessageHandler(this.client)).start();
        new Thread(new MessageSender(this.client)).start();
    }

    public static void main(String[] args) throws IOException {
        new Client().run();
    }
}
