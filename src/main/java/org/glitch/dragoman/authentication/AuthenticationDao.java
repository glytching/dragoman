package org.glitch.dragoman.authentication;

public interface AuthenticationDao {

    boolean exists(String login);

    boolean isValid(String login, String password);

    User getUser(String login, String password);

    void createUser(String login, String password);
}
