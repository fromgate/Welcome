package ru.nukkit.welcome.provider.serverauth;

public class ServerauthTable {
    public ServerauthTable() {
    }

    public ServerauthTable(String name, String password) {
        this.user = name;
        this.password = password;
        this.firstlogin = String.valueOf(System.currentTimeMillis());
        this.lastlogin = String.valueOf(System.currentTimeMillis());
    }

    // @DatabaseField(id=true, canBeNull = false, columnName = "user")
    String user;

    //@DatabaseField(canBeNull = false, columnName = "password")
    String password;

    //@DatabaseField
    String ip;

    //@DatabaseField (columnName = "firstlogin")
    String firstlogin;

    //@DatabaseField (columnName = "lastlogin")
    String lastlogin;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstlogin() {
        return firstlogin;
    }

    public void setFirstlogin(String firstlogin) {
        this.firstlogin = firstlogin;
    }

    public String getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(String lastlogin) {
        this.lastlogin = lastlogin;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}