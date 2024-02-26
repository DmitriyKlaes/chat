package ru.chat.users;

import java.io.IOException;
import java.net.Socket;

public class UserRegular extends User {

    public UserRegular(Socket client, String nickname) throws IOException {
        super(client, nickname);
    }

}
