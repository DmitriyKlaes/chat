package ru.chat.server;

import lombok.Getter;
import ru.chat.users.ChatUser;
import ru.chat.users.UserAdministrator;
import ru.chat.users.UserRegular;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements ServerConfiguration {

  @Getter
  private final List<ChatUser> listUser;

  public Server() {
    this.listUser = new ArrayList<>();
  }

  public void run() throws IOException {
    try (ServerSocket server = new ServerSocket(PORT)) {
      System.out.println("Сервер запущен на порту " + PORT);

      while (true) {
        Socket client = server.accept();

        String nickname = (new Scanner(client.getInputStream())).nextLine();
        System.out.println("Новый пользователь: \"" + nickname + "\"\n\t     Хост:" + client.getInetAddress().getHostAddress());

        ChatUser user = createNewUser(client, nickname);
        broadcastMessageAboutNewUser(user);
        user.getStreamOut().println("Добро пожаловать, " + user.getNickname());

        new Thread(new UserHandler(this, user)).start();
      }
    }
  }

  private ChatUser createNewUser(Socket client, String nickName) throws IOException {
    ChatUser newUser;
      if (nickName.equalsIgnoreCase("admin")) {
        newUser = new UserAdministrator(client, nickName);
      } else {
        newUser = new UserRegular(client, nickName);
      }
    this.listUser.add(newUser);
    return newUser;
  }

  public void broadcastMessageAboutNewUser(ChatUser user) {
    String rights = user instanceof UserAdministrator ? "Администратор " : "Пользователь ";
    this.listUser.forEach(it -> it.getStreamOut().println(rights + user.getNickname() + " вошел в чат!"));
  }

  public void broadcastMessageAboutLeaveUser(ChatUser user) {
    String rights = user instanceof UserAdministrator ? "Администратор " : "Пользователь ";
    this.listUser.forEach(it -> it.getStreamOut().println(rights + user.getNickname() + " ушел из чата!"));
  }

  public void broadcastMessages(String message, ChatUser userSender) {
    for (ChatUser user : this.listUser) {
      if (!(user instanceof UserRegular)) {
        user.getStreamOut().printf("%s(%d): %s%n", userSender.getNickname(), userSender.getId(), message);
      } else {
        user.getStreamOut().printf("%s: %s%n", userSender.getNickname(), message);
      }
    }
  }

  public void sendMessageToUser(String message, ChatUser userSender, String userNick) {
    Optional<ChatUser> targetUser = this.listUser.stream()
                                                 .filter(it -> it.getNickname().equals(userNick))
                                                 .findFirst();
    if (targetUser.isEmpty()) {
      userSender.getStreamOut().println(userSender.getNickname() + " -> (!Никому!): " + message);
    } else {
      ChatUser chatUser = targetUser.get();
      userSender.getStreamOut().println(userSender.getNickname() + " -> " + chatUser.getNickname() +": " + message);
      chatUser.getStreamOut().println("Вам от " + userSender.getNickname() + ": " + message);
    }
  }

  public void kickUser(long userId) throws Exception {
      ChatUser userForKick = findUserById(userId);
      if (userForKick == null) {
        throw new Exception("Пользователь с ID " + userId + " не найден!");
      }
      userForKick.getStreamOut().println("Вас кикнули с сервера!");
      userForKick.getClient().close();
      this.listUser.remove(userForKick);
  }

  public ChatUser findUserById(long id) {
    return this.listUser.stream()
                        .filter(it -> it.getId() == id)
                        .findFirst()
                        .orElse(null);
  }

  public long getUserIdFromChatCommand(String message) throws NumberFormatException {
    String[] splitMessage = message.split(" ");
    return Long.parseLong(splitMessage[1]);
  }

  public static void main(String[] args) throws IOException {
    new Server().run();
  }
}

