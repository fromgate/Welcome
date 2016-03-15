package ru.nukkit.welcome.password;


public interface Password {
    boolean isEnabled();
    boolean checkPassword (String playerName, String password);
    boolean setPassword (String playerName, String password);
    boolean hasPassword (String playerName);
    boolean removePassword (String playerName);
    boolean checkAutoLogin (String playerName, String uuid, String ip);
    void updateAutoLogin (String playerName, String uuid, String ip);
    void updateAutoLogin (String playerName, String uuid, String ip, long currentTime);
    void onDisable();
}
