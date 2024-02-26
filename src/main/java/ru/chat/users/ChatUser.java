package ru.chat.users;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public interface ChatUser {

    Long getId();
    PrintStream getStreamOut();
    InputStream getStreamIn();
    Socket getClient();
    String getNickname();
    void setId(Long id);
    void setStreamOut(PrintStream streamOut);
    void setStreamIn(InputStream streamIn);
    void setClient(Socket client);
    void setNickname(String nickname);
}
