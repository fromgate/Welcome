package ru.nukkit.welcome.provider.redis;

import cn.nukkit.utils.SimpleConfig;

import java.io.File;

public class RedisCfg extends SimpleConfig {
    public RedisCfg(File file) {
        super(file);
    }

    @Path (value = "redis.prefix")
    public String prefix = "welcome_";

    @Path (value = "redis.use-default-server")
    public boolean useDefault = true;

    @Path(value = "redis.custom.host")
    public String host = "localhost";

    @Path(value = "redis.custom.port")
    public int port=6379;

    @Path(value = "redis.custom.extra-config.timeout")
    public int timeout = 0;

    @Path(value = "redis.custom.extra-config.password")
    public String password = "";

    @Path(value = "redis.custom.extra-config.database")
    public int database = 0; // null

    @Path(value = "redis.custom.extra-config.clientname")
    public String clientName = ""; //null
}
