package ru.nukkit.welcome.provider.serverauth;

import cn.nukkit.Server;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;
import ru.nukkit.dblib.DbLib;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.password.PasswordManager;
import ru.nukkit.welcome.provider.PasswordProvider;
import ru.nukkit.welcome.util.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerauthProvider implements PasswordProvider {
    private boolean enabled;
    private ServerauthCfg cfg;

    private ConnectionSource connectionSource;
    Dao<ServerauthTable, String> dao;
    DatabaseTableConfig<ServerauthTable> tableCfg;

    public ServerauthProvider(){
        enabled = false;
        if (Server.getInstance().getPluginManager().getPlugin("DbLib") == null){
            Message.DB_DBLIB_NOTFOUND.log();
            return;
        }
        cfg = new ServerauthCfg(new File(Welcome.getPlugin().getDataFolder()+File.separator+"serverauth.yml"));
        cfg.load();
        cfg.save();


        List<DatabaseFieldConfig> fieldConfigs = new ArrayList<DatabaseFieldConfig>();
        DatabaseFieldConfig field = new DatabaseFieldConfig("user");
        field.setCanBeNull(false);
        field.setId(true);
        field.setDataType(DataType.STRING);
        fieldConfigs.add(field);
        field = new DatabaseFieldConfig("password");
        field.setDataType(DataType.STRING);
        field.setCanBeNull(false);
        fieldConfigs.add(field);
        field = new DatabaseFieldConfig("ip");
        field.setDataType(DataType.STRING);
        fieldConfigs.add(field);
        field = new DatabaseFieldConfig("firstlogin");
        field.setDataType(DataType.STRING);
        fieldConfigs.add(field);
        field = new DatabaseFieldConfig("lastlogin");
        field.setDataType(DataType.STRING);
        fieldConfigs.add(field);
        tableCfg = new DatabaseTableConfig(ServerauthTable.class,cfg.tableName,fieldConfigs);

        connectionSource = cfg.useDefault ? DbLib.getConnectionSource() :
                DbLib.getConnectionSource (new StringBuilder("jdbc:mysql://").append(cfg.host).append(":").
                        append(cfg.port).append("/").
                        append(cfg.db).toString(),cfg.username,cfg.password);

        if (connectionSource==null) return;

        try {
            dao = DaoManager.createDao(connectionSource,tableCfg);
            TableUtils.createTableIfNotExists(connectionSource,ServerauthTable.class);
        } catch (Exception e) {
            return;
        }
        enabled = true;
    }


    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean checkPassword(String playerName, String password) {
        if (!enabled) return false;
        if (playerName==null||playerName.isEmpty()) return false;
        ServerauthTable st;
        try {
            st = dao.queryForId(playerName);
        } catch (Exception e){
            PasswordManager.setLock(playerName);
            return false;
        }
        if (st.getPassword()==null) return false;
        return  password.equals(st.getPassword());
    }

    public boolean setPassword(String playerName, String password) {
        if (!enabled) return false;
        if (playerName==null||playerName.isEmpty()) return false;
        if (password==null||password.isEmpty()) return false;
        ServerauthTable st = new ServerauthTable(playerName,password);
        try {
            dao.create(st);
        } catch (Exception e) {
            st = null;
        }
        if (st == null) try {
            st = dao.queryForId(playerName);
            st.setPassword(password);
            dao.update(st);
        } catch (Exception e) {
            PasswordManager.setLock(playerName);
            return false;
        }
        return true;
    }

    public boolean hasPassword(String playerName) {
        if (!enabled) return false;
        if (playerName==null||playerName.isEmpty()) return false;
        try {
            return dao.idExists(playerName);
        } catch (Exception e) {
            PasswordManager.setLock(playerName);
        }
        return  false;
    }

    public boolean removePassword(String playerName) {
        if (!enabled) return false;
        if (playerName==null||playerName.isEmpty()) return false;
        try {
            ServerauthTable pt = dao.queryForId(playerName);
            dao.delete(pt);
        } catch (Exception e){
            PasswordManager.setLock(playerName);
            return false;
        }
        return  true;
    }

    public Long lastLoginFromIp(String playerName, String ip) {
        if (!enabled) return null; // Ошибка - регистрация запрещена
        List<ServerauthTable> result;
        try {
            result = dao.queryBuilder().where().eq("ip",ip).query();
        } catch (Exception e){
            PasswordManager.setLock(null);
            return null; // Ошибка - регистрация запрещена
        }
        long time = 0;
        for (ServerauthTable row : result){
            long lastTime = Long.parseLong(row.getLastlogin());
            if (lastTime>time) time=lastTime;
        }
        return time;
    }

    public boolean checkAutoLogin(String playerName, String uuid, String ip) {
        if (!enabled) return false;
        long loginTime = System.currentTimeMillis();
        if (playerName==null||playerName.isEmpty()) return false;
        ServerauthTable st = null;
        try {
            st = dao.queryForId(playerName);
        } catch (Exception e) {
            PasswordManager.setLock(playerName);
        }
        if (st == null) return false;
        String prevIp = st.getIp();
        if (prevIp==null||prevIp.isEmpty()) return false;
        String prevTimeStr = st.getLastlogin();
        if (prevTimeStr==null||prevTimeStr.isEmpty()) return false;
        long prevTime = Long.parseLong(prevTimeStr);
        if (loginTime-prevTime>Welcome.getCfg().getMaxAutoTime()) return false;
        return prevIp.equalsIgnoreCase(ip);
    }

    public void updateAutoLogin(String playerName, String uuid, String ip, long currentTime) {
        if (!enabled) return;
        if (playerName==null||playerName.isEmpty()) return;
        try {
            if (!dao.idExists(playerName)) return;
            ServerauthTable st = dao.queryForId(playerName);
            st.setLastlogin(String.valueOf(currentTime));
            st.setIp(ip);
            dao.update(st);
        } catch (Exception e) {
            PasswordManager.setLock(playerName);
        }
    }

    public boolean removeAutoLogin(String playerName) {
        if (!enabled) return false;
        if (playerName==null||playerName.isEmpty()) return false;
        try {
            ServerauthTable st = dao.queryForId(playerName);
            st.setLastlogin("0");
            dao.update(st);
        } catch (Exception e){
            PasswordManager.setLock(playerName);
            return false;
        }
        return true;
    }

    public void onDisable() {
        if (connectionSource!=null) connectionSource.closeQuietly();
    }
}