package ru.nukkit.welcome.provider.serverauth;

import cn.nukkit.utils.Config;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.provider.PasswordProvider;
import ru.nukkit.welcome.provider.Providers;
import ru.nukkit.welcome.util.Message;

import java.io.File;
import java.util.List;

public class ServerauthProvider implements PasswordProvider {

    private String createPlayersTable = "CREATE TABLE IF NOT EXISTS :table (user VARCHAR(200) NOT NULL, " +
            "password VARCHAR(200), ip VARCHAR(200), firstlogin VARCHAR(200), lastlogin VARCHAR(200), PRIMARY KEY(user))";

    private String selectByName = "SELECT * FROM :table WHERE user = :name";

    private String selectByIp = "SELECT * FROM :table WHERE ip = :lastip";

    private String updatePassword = "UPDATE :table SET user = :name, password = :hash WHERE user = :name";

    private String updatePassword2 = "INSERT OR IGNORE INTO :table (user, password, firstlogin) VALUES (:name, :hash, :registerdate)";

    private String deletePlayer = "DELETE FROM :table WHERE user = :name";

    private String updateLastlogin = "UPDATE :table SET ip = :lastip, firstlogin = :logindate WHERE user = :name";

    private String deleteLastlogin = "DELETE FROM :table WHERE user = :name";

    private Sql2o sql2o;

    private boolean enabled;

    private String tableName;

    public ServerauthProvider() {
        enabled = false;

        File oldCfgFile = new File(Welcome.getPlugin().getDataFolder() + File.separator + "serverauth.yml");
        if (oldCfgFile.exists()) {
            Config oldCfg = new Config(oldCfgFile, Config.YAML);
            String oldTableName = oldCfg.getString("database.table-name");
            if (oldTableName != null && !oldTableName.isEmpty() && !oldTableName.equals(Welcome.getCfg().sbServerAuthTable)) {
                Welcome.getCfg().sbSimpleAuthTable = oldTableName;
                Welcome.getCfg().save();
            }
            oldCfgFile.delete();
            Message.OLD_FILE_REMOVED.log("NOCOLOR");
        }

        this.tableName = Welcome.getCfg().sbServerAuthTable;

        createPlayersTable = createPlayersTable.replace(":table", tableName);
        selectByName = selectByName.replace(":table", tableName);
        selectByIp = selectByIp.replace(":table", tableName);
        updatePassword = updatePassword.replace(":table", tableName);
        updatePassword2 = updatePassword2.replace(":table", tableName);
        deletePlayer = deletePlayer.replace(":table", tableName);
        updateLastlogin = updateLastlogin.replace(":table", tableName);
        deleteLastlogin = deleteLastlogin.replace(":table", tableName);


        sql2o = Providers.getSql2o();
        if (sql2o == null) return;

        try (Connection con = sql2o.open()) {
            con.createQuery(createPlayersTable)
                    .executeUpdate();
        }

        enabled = true;
    }


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean checkPassword(String playerName, String password) {
        if (!enabled) return false;
        if (playerName == null || playerName.isEmpty()) return false;
        PlayersTable pt;
        try (Connection con = sql2o.open()) {
            pt = con.createQuery(selectByName)
                    .addParameter("name", playerName)
                    .executeAndFetchFirst(PlayersTable.class);
        }
        if (pt == null || pt.user == null || pt.password.isEmpty()) return false;
        return password.equals(pt.password);
    }

    @Override
    public boolean setPassword(String playerName, String password) {
        if (!enabled) return false;
        if (playerName == null || playerName.isEmpty()) return false;
        if (password == null || password.isEmpty()) return false;
        try (Connection con = sql2o.beginTransaction(java.sql.Connection.TRANSACTION_SERIALIZABLE)) {
            con.createQuery(updatePassword)
                    .addParameter("name", playerName)
                    .addParameter("hash", password)
                    .executeUpdate();
            con.createQuery(updatePassword2)
                    .addParameter("name", playerName)
                    .addParameter("hash", password)
                    .addParameter("registerdate", String.valueOf(System.currentTimeMillis()))
                    .executeUpdate();
            con.commit();
        }
        return true;
    }

    @Override
    public boolean hasPassword(String playerName) {
        if (!enabled) return false;
        if (playerName == null || playerName.isEmpty()) return false;
        PlayersTable pt;
        try (Connection con = sql2o.open()) {
            pt = con.createQuery(selectByName)
                    .addParameter("name", playerName)
                    .executeAndFetchFirst(PlayersTable.class);
        }
        if (pt == null || pt.password == null || pt.password.isEmpty()) return false;
        return true;
    }

    @Override
    public boolean removePassword(String playerName) {
        if (!enabled) return false;
        if (playerName == null || playerName.isEmpty()) return false;
        try (Connection con = sql2o.open()) {
            con.createQuery(deletePlayer)
                    .addParameter("name", playerName)
                    .executeUpdate();
        }
        return true;
    }

    @Override
    public Long lastLoginFromIp(String playerNane, String ip) {
        if (!enabled) return null; // Ошибка - регистрация запрещена
        List<PlayersTable> result;
        try (Connection con = sql2o.open()) {
            result = con.createQuery(selectByIp)
                    //.addParameter("table", tableName)
                    .addParameter("lastip", ip)
                    .executeAndFetch(PlayersTable.class);
        }
        if (result == null || result.isEmpty()) return null;
        long time = 0;
        for (PlayersTable row : result) {
            if (row.firstlogin == null || row.firstlogin.isEmpty()) continue;
            long lastTime = Long.parseLong(row.firstlogin);
            if (lastTime > time) time = lastTime;
        }
        return time;
    }

    @Override
    public boolean checkAutoLogin(String playerName, String uuid, String ip) {
        if (!enabled) return false;
        if (playerName == null || playerName.isEmpty()) return false;
        PlayersTable pt;
        try (Connection con = sql2o.open()) {
            pt = con.createQuery(selectByName)
                    .addParameter("name", playerName)
                    .executeAndFetchFirst(PlayersTable.class);
        }
        if (pt == null) return false;
        if (pt.ip == null || pt.ip.isEmpty()) return false;
        if (pt.firstlogin == null || pt.firstlogin.isEmpty()) return false;

        if (System.currentTimeMillis() - Long.parseLong(pt.firstlogin) > Welcome.getCfg().getMaxAutoTime())
            return false;
        return ip.equals(pt.ip);
    }

    @Override
    public void updateAutoLogin(String playerName, String uuid, String ip, long currentTime) {
        if (!enabled) return;
        if (playerName == null || playerName.isEmpty()) return;
        try (Connection con = sql2o.open()) {
            con.createQuery(updateLastlogin)
                    .addParameter("name", playerName)
                    .addParameter("lastip", ip)
                    .addParameter("logindate", String.valueOf(currentTime))
                    .executeUpdate();
        }
    }

    @Override
    public boolean removeAutoLogin(String playerName) {
        if (!enabled) return false;
        if (playerName == null || playerName.isEmpty()) return false;

        try (Connection con = sql2o.open()) {
            con.createQuery(deleteLastlogin)
                    .addParameter("name", playerName)
                    .executeUpdate();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void onDisable() {
        // Zzzzzz....
    }


}
