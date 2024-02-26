package ru.chat.server;

import ru.chat.users.ChatUser;
import ru.chat.users.UserRegular;

import java.util.Scanner;

public class UserHandler implements Runnable {

    Server server;
    ChatUser user;

    public UserHandler(Server server, ChatUser user) {
        this.server = server;
        this.user = user;
    }

    @Override
    public void run() {
        String message;
        try (Scanner scn = new Scanner(this.user.getStreamIn())) {
            while (scn.hasNextLine()) {
                message = scn.nextLine();
                if (message.length() != 0) {
                    if (message.charAt(0) == '@') {

                        privateMessage(message);

                    } else if (message.startsWith("kick ") && !(user instanceof UserRegular)) {

                        kickCommand(message);

                    } else if (message.equals("quit")) {

                        quitCommand();

                    } else {
                        server.broadcastMessages(message, user);
                    }
                }
            }
        }
    }

    private void privateMessage(String message) {
        if (message.contains(" ")) {
            System.out.println("Личное сообщение: " + message);
            int firstSpace = message.indexOf(" ");
            String userPrivate = message.substring(1, firstSpace);
            server.sendMessageToUser(message.substring(firstSpace + 1), user, userPrivate);
        }
    }

    private void kickCommand(String message) {
        try {
            long userId = server.getUserIdFromChatCommand(message);
            server.kickUser(userId);
            System.out.println("Администратор " + this.user.getNickname() + " кикнул пользователя с ID " + userId);
            server.broadcastMessageAboutLeaveUser(this.user);
        } catch (NumberFormatException e) {
            user.getStreamOut().println("Неверно введен ID пользователя");
        } catch (Exception e) {
            user.getStreamOut().println(e.getMessage());
        }
    }

    private void quitCommand() {
        this.server.getListUser().remove(this.user);
        String quitMessage = "Пользователь " + this.user.getNickname() + " вычел из чата.";
        System.out.println(quitMessage);
        server.broadcastMessageAboutLeaveUser(this.user);
    }
}
