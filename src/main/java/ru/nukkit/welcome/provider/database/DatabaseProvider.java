package ru.nukkit.welcome.provider.database;

import cn.nukkit.Server;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.nukkit.dblib.DbLib;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.provider.PasswordProvider;
import ru.nukkit.welcome.util.Message;

import java.util.List;

public class DatabaseProvider implements PasswordProvider {

    // Players table queries

    public final static String createPlayersTable = "CREATE TABLE IF NOT EXISTS players (name VARCHAR(200) NOT NULL, password VARCHAR(200), PRIMARY KEY(name))";

    public final static String createLastloginTable = "CREATE TABLE IF NOT EXISTS lastlogin (name VARCHAR(100) NOT NULL, uuid VARCHAR(100) NOT NULL, ip VARCHAR(100) NOT NULL, time BIGINT NOT NULL, PRIMARY KEY (name))";

    public final static String getPlayer = "SELECT * FROM players WHERE name = :name";

    public final static String setPassword = "REPLACE INTO players VALUES (:name, :password)";

    public final String deletePlayer = "DELETE FROM players WHERE name = :name";

    // Lastlogin table queries

    public final static String getLastloginByIp = "SELECT * FROM lastlogin WHERE ip = :ip";

    public final static String getLastloginByName = "SELECT * FROM lastlogin WHERE name = :name";

    public final static String setLastlogin = "REPLACE INTO lastlogin VALUES (:name, :password, :uuid, :ip)";

    public final String deleteLastlogin = "DELETE FROM lastlogin WHERE name = :name";


    private Sql2o sql2o;
    private boolean enabled;

    public DatabaseProvider() {
        enabled = false;

        if (Server.getInstance().getPluginManager().getPlugin("DbLib") == null) {
            Message.DB_DBLIB_NOTFOUND.log();
            return;
        }
        sql2o = DbLib.getSql2o();
        if (sql2o == null) return;

        try (Connection con = sql2o.beginTransaction(java.sql.Connection.TRANSACTION_SERIALIZABLE)) {
            con.createQuery(createPlayersTable).executeUpdate();
            con.createQuery(createLastloginTable).executeUpdate();
            con.commit();
        }

/*
        try (Connection con = sql2o.open()) {
            con.createQuery(createPlayersTable).executeUpdate();
        }
        try (Connection con = sql2o.open()) {
            con.createQuery(createLastloginTable).executeUpdate();
        } */
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
        PasswordsTable pt = null;
        try (Connection con = sql2o.open()) {
            pt = con.createQuery(getPlayer).addParameter("name", playerName).executeAndFetchFirst(PasswordsTable.class);
            con.close();
        }
        if (pt == null || pt.name == null || pt.password.isEmpty()) return false;
        return password.equals(pt.password);
    }

    @Override
    public boolean setPassword(String playerName, String password) {
        if (!enabled) return false;
        if (playerName == null || playerName.isEmpty()) return false;
        if (password == null || password.isEmpty()) return false;

        try (Connection con = sql2o.open()) {
            con.createQuery(setPassword)
                    .addParameter("name", playerName)
                    .addParameter("password", password)
                    .executeUpdate();
            con.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean hasPassword(String playerName) {
        if (!enabled) return false;
        if (playerName == null || playerName.isEmpty()) return false;
        PasswordsTable pt = null;
        try (Connection con = sql2o.open()) {
            pt = con.createQuery(getPlayer).addParameter("name", playerName).executeAndFetchFirst(PasswordsTable.class);
            con.close();
        }
        if (pt == null || pt.name == null || pt.password.isEmpty()) return false;
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
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Long lastLoginFromIp(String playerNane, String ip) {
        if (!enabled) return null; // Ошибка - регистрация запрещена
        List<LastloginTable> result;
        try (Connection con = sql2o.open()) {
            result = con.createQuery(getLastloginByIp)
                    .addParameter("ip", ip)
                    .executeAndFetch(LastloginTable.class);
        }
        if (result == null || result.isEmpty()) return null;
        long time = 0;
        for (LastloginTable row : result) {
            if (row.time > time) time = row.time;
        }
        return time;
    }

    @Override
    public boolean checkAutoLogin(String playerName, String uuid, String ip) {
        if (!enabled) return false;
        long loginTime = System.currentTimeMillis();
        if (playerName == null || playerName.isEmpty()) return false;
        String prevIp = "";
        String prevUUID = "";
        long prevTime = 0;

        try (Connection con = sql2o.open()) {
            LastloginTable ll = con.createQuery(getLastloginByName)
                    .addParameter("name", playerName)
                    .executeAndFetchFirst(LastloginTable.class);
            if (ll != null) {
                prevIp = ll.ip;
                prevUUID = ll.uuid;
                prevTime = ll.time;
            }
        } catch (Exception e) {
            return false;
        }
        if (loginTime - prevTime > Welcome.getCfg().getMaxAutoTime()) return false;
        if (prevIp.isEmpty() || prevUUID.isEmpty()) return false;
        if (!prevUUID.equalsIgnoreCase(uuid)) return false;
        return prevIp.equalsIgnoreCase(ip);
    }

    @Override
    public void updateAutoLogin(String playerName, String uuid, String ip, long currentTime) {
        if (!enabled) return;
        if (playerName == null || playerName.isEmpty()) return;
        try (Connection con = sql2o.open()) {
            con.createQuery(setLastlogin)
                    .addParameter("name", playerName)
                    .addParameter("uuid", uuid)
                    .addParameter("ip", ip)
                    .addParameter("time", currentTime)
                    .executeUpdate();
        } catch (Exception ignore) {
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
        //
    }
}
