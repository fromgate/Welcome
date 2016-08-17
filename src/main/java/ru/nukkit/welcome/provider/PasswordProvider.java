package ru.nukkit.welcome.provider;


public interface PasswordProvider {
    boolean isEnabled();

    boolean checkPassword(String playerName, String password);

    boolean setPassword(String playerName, String password);

    boolean hasPassword(String playerName);

    boolean removePassword(String playerName);

    Long lastLoginFromIp(String playerNane, String ip);

    boolean checkAutoLogin(String playerName, String uuid, String ip);

    void updateAutoLogin(String playerName, String uuid, String ip, long currentTime);

    boolean removeAutoLogin(String playerName);

    void onDisable();
}
