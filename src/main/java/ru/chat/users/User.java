package ru.chat.users;

import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

@Data
public abstract class User implements ChatUser {

    private static Long counterId = 1L;

    private Long id;
    private PrintStream streamOut;
    private InputStream streamIn;
    private Socket client;
    private String nickname;


    public User(Socket client, String nickname) throws IOException {
        this.id = counterId++;
        this.streamOut = new PrintStream(client.getOutputStream());
        this.streamIn = client.getInputStream();
        this.client = client;
        this.nickname = nickname;
    }

}
