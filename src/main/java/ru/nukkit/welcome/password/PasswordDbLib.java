package ru.nukkit.welcome.password;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.nukkit.dblib.DbLib;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.db.LastloginTable;
import ru.nukkit.welcome.db.PasswordsTable;

import java.sql.SQLException;

public class PasswordDbLib implements Password {

    private boolean enabled;

    ConnectionSource connectionSource;
    Dao<PasswordsTable, String> passDao;
    Dao<LastloginTable, String> lastloginDao;

    public PasswordDbLib(){
        enabled = false;
        if (Server.getInstance().getPluginManager().getPlugin("DbLib") == null){
            Welcome.getPlugin().getLogger().info(TextFormat.RED+"DbLib plugin not found");
            return;
        }
        connectionSource = DbLib.getConnectionSource();
        if (connectionSource == null) return;
        try {
            passDao =  DaoManager.createDao(connectionSource, PasswordsTable.class);
            TableUtils.createTableIfNotExists(connectionSource, PasswordsTable.class);
            lastloginDao = DaoManager.createDao(connectionSource,LastloginTable.class);
            TableUtils.createTableIfNotExists(connectionSource, LastloginTable.class);
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
        PasswordsTable pt = null;
        try {
            pt = passDao.queryForId(playerName);
        } catch (SQLException e) {
            return false;
        }
        if (pt.getPassword()==null) return false;
        return  password.equals(pt.getPassword());
    }

    public boolean setPassword(String playerName, String password) {
        if (!enabled) return false;
        if (playerName==null||playerName.isEmpty()) return false;
        if (password==null||password.isEmpty()) return false;
        PasswordsTable pt = new PasswordsTable (playerName,password);
        if (pt!=null)
            try {
                passDao.create(pt);
            } catch (SQLException e) {
                pt = null;
            }
        if (pt == null) try {
            pt = passDao.queryForId(playerName);
            pt.setPassword(password);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean hasPassword(String playerName) {
        if (!enabled) return false;
        if (playerName==null||playerName.isEmpty()) return false;
        try {
            return passDao.idExists(playerName);
        } catch (SQLException e) {
        }
        return  false;

    }

    public boolean removePassword(String playerName) {
        if (!enabled) return false;
        if (playerName==null||playerName.isEmpty()) return false;
        try {
            PasswordsTable pt = null;
            pt = passDao.queryForId(playerName);
            passDao.delete(pt);
        } catch (Exception e){
            return false;
        }
        return  true;
    }

    public boolean checkAutoLogin(String playerName, String uuid, String ip) {
        long loginTime = System.currentTimeMillis();
        if (!enabled) return false;
        if (playerName==null||playerName.isEmpty()) return false;
        String prevIp = "";
        String prevUUID = "";
        long prevTime=0;
        LastloginTable llt = null;
        try {
            llt = lastloginDao.queryForId(playerName);
        } catch (SQLException e) {
        }

        if (llt!=null){
            prevIp = llt.getIp();
            prevUUID = llt.getUuid();
            prevTime = llt.getTime();

            llt.setUuid(uuid);
            llt.setIp(ip);
            llt.setTime(loginTime);
        } else try {
            lastloginDao.create(new LastloginTable(playerName,uuid,ip,loginTime));
        } catch (SQLException e) {
        }

        if (loginTime-prevTime>Welcome.getPlugin().getMaxAutoTime()) return false;
        if (prevIp.isEmpty()||prevUUID.isEmpty()) return false;
        if (!prevUUID.equalsIgnoreCase(uuid)) return false;
        return prevIp.equalsIgnoreCase(ip);
    }
}