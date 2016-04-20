package ru.nukkit.welcome.provider.serverauth;

import cn.nukkit.utils.SimpleConfig;

import java.io.File;

/**
 * Created by Igor on 20.03.2016.
 */
public class ServerauthCfg extends SimpleConfig {

    @Path (value = "database.use-default-db")
    public boolean useDefault = true;

    @Path (value = "database.table-name")
    public String tableName = "srvauth_serverauthdata";

    @Path(value = "database.MySQL.host")
    public  String host = "localhost";

    @Path(value ="database.MySQL.port")
    public  int port = 3306;

    @Path(value ="database.MySQL.database")
    public  String db ="serverauth";

    @Path(value = "database.MySQL.username")
    public String username = "nukkit";

    @Path(value = "database.MySQL.password")
    public  String password = "tikkun";


    public ServerauthCfg(File file) {
        super(file);
    }



}
