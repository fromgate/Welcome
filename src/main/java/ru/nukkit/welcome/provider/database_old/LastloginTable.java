package ru.nukkit.welcome.provider.database_old;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "lastlogin")
public class LastloginTable {

    @DatabaseField(id = true)
    private String name;

    @DatabaseField(canBeNull = false)
    private String uuid;

    @DatabaseField(canBeNull = false)
    private String ip;

    @DatabaseField(canBeNull = false)
    private long time;

    public LastloginTable() {
    }

    public LastloginTable(String name, String uuid, String ip, long time) {
        this.name = name;
        this.uuid = uuid;
        this.ip = ip;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}


