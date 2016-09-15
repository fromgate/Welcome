package ru.nukkit.welcome.provider;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.TaskHandler;
import org.sql2o.Sql2o;
import ru.nukkit.dblib.DbLib;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.password.PasswordLock;
import ru.nukkit.welcome.provider.database.DatabaseProvider;
import ru.nukkit.welcome.provider.redis.RedisProvider;
import ru.nukkit.welcome.provider.serverauth.ServerauthProvider;
import ru.nukkit.welcome.provider.serverauth.SimpleauthProvider;
import ru.nukkit.welcome.util.Cfg;
import ru.nukkit.welcome.util.Message;

public enum Providers {
    YAML(YamlProvider.class),
    DATABASE(DatabaseProvider.class),
    SERVERAUTH(ServerauthProvider.class),
    SIMPLEAUTH(SimpleauthProvider.class),
    REDIS(RedisProvider.class),
    LOCK(PasswordLock.class);

    private static PasswordProvider passworder;

    Class<? extends PasswordProvider> clazz;

    Providers(Class<? extends PasswordProvider> clazz) {
        this.clazz = clazz;
    }


    public PasswordProvider getProvider() {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            if (Message.isDebug()) e.printStackTrace();
            return null;
        }
    }

    public static void init() {
        Providers pp = getByName(Welcome.getCfg().passwordProvider);
        passworder = pp == null ? null : pp.getProvider();
        if (passworder == null || !passworder.isEnabled()) {
            Message m = passworder == null ? Message.DB_UNKNOWN : Message.DB_INIT;
            m.log(Welcome.getCfg().passwordProvider);
            pp = Providers.LOCK;
            setLock(null);
        }
        Message.DB_INIT.log(pp.name(), Welcome.getCfg().getHashAlgorithm());
        if (pp == Providers.YAML && Server.getInstance().getPluginManager().getPlugin("DbLib") != null) {
            Message.DB_DBLIB_FOUND.log();
        }
    }

    private static TaskHandler task = null;

    public static void reInit() {
        if (task != null) return;
        Message.DB_RENIT_TRY.log();
        Providers pp = getByName(Welcome.getCfg().passwordProvider);
        PasswordProvider pwd = (pp == null) ? null : pp.getProvider();
        if (pwd != null && pwd.isEnabled()) {
            passworder = pwd;
            Message.DB_REINIT.log(pp.name(), Welcome.getCfg().getHashAlgorithm());
        } else task = Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
            public void run() {
                task = null;
                reInit();
            }
        }, Welcome.getCfg().getReinitTimeTicks());
    }

    public static void onDisable() {
        if (passworder != null) passworder.onDisable();
    }

    public static Providers getByName(String pwdProv) {
        for (Providers pp : Providers.values())
            if (pp.name().equalsIgnoreCase(pwdProv)) return pp;
        return null;
    }

    public static void setLock(final String playerName) {
        Message.DB_LOCK.log();
        onDisable();
        passworder = Providers.LOCK.getProvider();
        if (playerName != null && playerName.isEmpty())
            Server.getInstance().getScheduler().scheduleDelayedTask(() -> {
                Player player = Server.getInstance().getPlayerExact(playerName);
                if (player != null) player.close("", Message.LOCK_INFORM.getText());
            }, 1);
        reInit();
    }

    public static Sql2o getSql2o() {
        if (Server.getInstance().getPluginManager().getPlugin("DbLib") == null) {
            Message.DB_DBLIB_NOTFOUND.log();
            return null;
        }
        Cfg cfg = Welcome.getCfg();
        switch (cfg.dbConnect.toLowerCase()) {
            case "dblib":
                return DbLib.getSql2o();
            case "sqlite":
                return DbLib.getSql2o(DbLib.getSqliteUrl(cfg.dbSqliteFile), "", "");
            case "mysql":
                return DbLib.getSql2oMySql(cfg.dbMySqlHost, cfg.dbMySqlPort, cfg.dbMySqlDb, cfg.dbMySqlUser, cfg.dbMySqlPwd);
        }
        return null;
    }

    public static PasswordProvider getCurrentProvider() {
        return passworder;
    }
}
