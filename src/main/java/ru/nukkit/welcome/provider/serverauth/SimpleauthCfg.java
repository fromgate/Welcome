package ru.nukkit.welcome.provider.serverauth;

import cn.nukkit.utils.SimpleConfig;

import java.io.File;

public class SimpleauthCfg extends SimpleConfig {

    @Path(value = "database.use-default-db")
    public boolean useDefault = true;

    @Path(value = "database.table-name")
    public String tableName = "simpleauth_players";

    @Path(value = "database.MySQL.host")
    public String host = "localhost";

    @Path(value = "database.MySQL.port")
    public int port = 3306;

    @Path(value = "database.MySQL.database")
    public String db = "serverauth";

    @Path(value = "database.MySQL.username")
    public String username = "nukkit";

    @Path(value = "database.MySQL.password")
    public String password = "tikkun";

    public SimpleauthCfg(File file) {
        super(file);
    }
}
