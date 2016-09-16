package ru.nukkit.welcome.provider.simpleauth;

public class PlayersTable {

    public String name;

    public String hash;

    public String lastip;

    public String registerdate;

    public String logindate;

    public PlayersTable(String playerName, String password) {
        this.name = playerName;
        this.hash = password;
        this.lastip = null;
        this.registerdate = null;
        this.logindate = null;
    }
}
