package ru.nukkit.welcome.password;

import cn.nukkit.Server;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.nukkit.dblib.DbLib;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.db.LastloginTable;
import ru.nukkit.welcome.db.PasswordsTable;
import ru.nukkit.welcome.util.Message;

import java.util.List;

public class PasswordDbLib implements Password {

    private boolean enabled;

    ConnectionSource connectionSource;
    Dao<PasswordsTable, String> passDao;
    Dao<LastloginTable, String> lastloginDao;

    public PasswordDbLib(){
        enabled = false;
        if (Server.getInstance().getPluginManager().getPlugin("DbLib") == null){
            Message.DB_DBLIB_NOTFOUND.log();
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
        PasswordsTable pt;
        try {
            pt = passDao.queryForId(playerName);
        } catch (Exception e) {
            PasswordProvider.setLock(playerName);
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
        try {
            passDao.create(pt);
        } catch (Exception e) {
            pt = null;
        }
        if (pt == null) try {
            pt = passDao.queryForId(playerName);
            pt.setPassword(password);
            passDao.update(pt);
        } catch (Exception e) {
            PasswordProvider.setLock(playerName);
            return false;
        }
        return true;
    }

    public boolean hasPassword(String playerName) {
        if (!enabled) return false;
        if (playerName==null||playerName.isEmpty()) return false;
        try {
            return passDao.idExists(playerName);
        } catch (Exception e) {
            PasswordProvider.setLock(playerName);
        }
        return  false;

    }

    public boolean removePassword(String playerName) {
        if (!enabled) return false;
        if (playerName==null||playerName.isEmpty()) return false;
        try {
            PasswordsTable pt = passDao.queryForId(playerName);
            passDao.delete(pt);
        } catch (Exception e){
            PasswordProvider.setLock(playerName);
            return false;
        }
        return  true;
    }


    public Long lastLoginFromIp (String playerName, String ip){
        if (!enabled) return null; // Ошибка - регистрация запрещена
        List<LastloginTable> result;
        try {
            result = lastloginDao.queryBuilder().where().eq("ip",ip).query();
        } catch (Exception e){
            PasswordProvider.setLock(null);
            return null; // Ошибка - регистрация запрещена
        }
        long time = 0;
        for (LastloginTable row : result){
            if (row.getTime()>time) time=row.getTime();
        }
        return time;
    }

    public boolean checkAutoLogin(String playerName, String uuid, String ip) {
        if (!enabled) return false;
        long loginTime = System.currentTimeMillis();
        if (playerName==null||playerName.isEmpty()) return false;
        String prevIp = "";
        String prevUUID = "";
        long prevTime=0;
        try {
            LastloginTable llt = lastloginDao.queryForId(playerName);
            prevIp = llt.getIp();
            prevUUID = llt.getUuid();
            prevTime = llt.getTime();
        } catch (Exception e) {
            PasswordProvider.setLock(playerName);
        }
        if (loginTime-prevTime>Welcome.getCfg().getMaxAutoTime()) return false;
        if (prevIp.isEmpty()||prevUUID.isEmpty()) return false;
        if (!prevUUID.equalsIgnoreCase(uuid)) return false;
        return prevIp.equalsIgnoreCase(ip);
    }

    public void updateAutoLogin(String playerName, String uuid, String ip, long currentTime) {
        if (!enabled) return;
        if (playerName==null||playerName.isEmpty()) return;
        LastloginTable llt = null;
        try {
            llt = lastloginDao.queryForId(playerName);
            llt.setUuid(uuid);
            llt.setIp(ip);
            llt.setTime(currentTime);
            lastloginDao.update(llt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (llt==null) try {
            lastloginDao.create(new LastloginTable(playerName,uuid,ip,currentTime));
        } catch (Exception ignore){
        }
    }

    public boolean removeAutoLogin(String playerName) {
        if (!enabled) return false;
        if (playerName==null||playerName.isEmpty()) return false;
        try {
            LastloginTable pt = lastloginDao.queryForId(playerName);
            lastloginDao.delete(pt);
        } catch (Exception e){
            PasswordProvider.setLock(playerName);
            return false;
        }
        return true;
    }

    public void onDisable() {
        if (connectionSource!=null) connectionSource.closeQuietly();
    }
}