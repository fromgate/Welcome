package ru.nukkit.welcome.password;


public interface Password {
    public abstract boolean isEnabled();
    public abstract boolean checkPassword (String playerName, String password);
    public abstract boolean setPassword (String playerName, String password);
    public abstract boolean hasPassword (String playerName);
    public abstract boolean removePassword (String playerName);
    public abstract boolean checkAutoLogin (String playerName, String uuid, String ip);
}
