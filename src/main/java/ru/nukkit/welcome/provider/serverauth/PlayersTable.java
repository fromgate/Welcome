package ru.nukkit.welcome.provider.serverauth;

public class PlayersTable {


    public String user;

    public String password;

    public String ip;

    public String firstlogin;

    public String lastlogin;

    public PlayersTable(String playerName, String password) {
        this.user = playerName;
        this.password = password;
        this.ip = null;
        this.firstlogin = null;
        this.lastlogin = null;
    }

}
