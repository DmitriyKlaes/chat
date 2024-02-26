package ru.chat.users;

import java.io.IOException;
import java.net.Socket;

public class UserAdministrator extends User {

    public UserAdministrator(Socket client, String nickname) throws IOException {
        super(client, nickname);
    }

}
