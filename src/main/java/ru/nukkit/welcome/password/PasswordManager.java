package ru.nukkit.welcome.password;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.TaskHandler;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.players.PlayerManager;
import ru.nukkit.welcome.provider.PasswordProvider;
import ru.nukkit.welcome.provider.YamlProvider;
import ru.nukkit.welcome.provider.database.DatabaseProvider;
import ru.nukkit.welcome.provider.redis.RedisProvider;
import ru.nukkit.welcome.provider.serverauth.ServerauthProvider;
import ru.nukkit.welcome.provider.serverauth.SimpleauthProvider;
import ru.nukkit.welcome.provider.sql2o.Sql2oProvider;
import ru.nukkit.welcome.util.Message;

public enum PasswordManager {
    YAML(YamlProvider.class),
    DATABASE(Sql2oProvider.class),
    SERVERAUTH(ServerauthProvider.class),
    SIMPLEAUTH(SimpleauthProvider.class),
    REDIS(RedisProvider.class),
    LOCK(PasswordLock.class),
    DATABASE_OLD(DatabaseProvider.class);

    Class<? extends PasswordProvider> clazz;

    PasswordManager(Class<? extends PasswordProvider> clazz) {
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

    private static PasswordProvider passworder;

    public static void init() {
        PasswordManager pp = getByName(Welcome.getCfg().passwordProvider);
        passworder = pp == null ? null : pp.getProvider();
        if (passworder == null || !passworder.isEnabled()) {
            Message m = passworder == null ? Message.DB_UNKNOWN : Message.DB_INIT;
            m.log(Welcome.getCfg().passwordProvider);
            pp = PasswordManager.LOCK;
            setLock(null);
        }
        Message.DB_INIT.log(pp.name(), Welcome.getCfg().getHashAlgorithm());
        if (pp == PasswordManager.YAML && Server.getInstance().getPluginManager().getPlugin("DbLib") != null)
            Message.DB_DBLIB_FOUND.log();
    }

    public static PasswordManager getByName(String pwdProv) {
        for (PasswordManager pp : PasswordManager.values())
            if (pp.name().equalsIgnoreCase(pwdProv)) return pp;
        return null;
    }

    public static boolean checkPassword(String playerName, String pwdStr) {
        return passworder.checkPassword(playerName.toLowerCase(), hashPassword(playerName.toLowerCase(), pwdStr));
    }

    public static boolean checkPassword(Player player, String pwdStr) {
        return checkPassword(player.getName(), pwdStr);
    }

    public static boolean setPassword(String playerName, String pwdStr) {
        return passworder.setPassword(playerName.toLowerCase(), hashPassword(playerName, pwdStr));
    }

    public static boolean setPassword(Player player, String pwdStr) {
        return setPassword(player.getName(), pwdStr);
    }

    public static boolean hasPassword(String playerName) {
        return passworder.hasPassword(playerName.toLowerCase());
    }

    public static boolean hasPassword(Player player) {
        return hasPassword(player.getName());
    }

    public static boolean removePassword(String playerName) {
        return passworder.removePassword(playerName.toLowerCase());
    }

    public static boolean removePassword(Player player) {
        return removePassword(player.getName());
    }

    public static String hashPassword(String userName, String password) {
        return Welcome.getCfg().getHashAlgorithm().getHash(userName, password);
    }


    public static boolean checkAutologin(Player player) {
        if (!hasPassword(player)) return false;
        if (!Welcome.getCfg().autologinEnable) return false;
        return passworder.checkAutoLogin(player.getName().toLowerCase(), player.getUniqueId().toString(), player.getAddress());
    }

    public static void updateAutologin(Player player, long time) {
        if (!hasPassword(player)) return;
        if (!PlayerManager.isPlayerLoggedIn(player)) return;
        passworder.updateAutoLogin(player.getName().toLowerCase(), player.getUniqueId().toString(), player.getAddress(), time);
    }

    public static void updateAutologin(Player player) {
        if (!hasPassword(player)) return;
        if (!PlayerManager.isPlayerLoggedIn(player)) return;
        passworder.updateAutoLogin(player.getName().toLowerCase(), player.getUniqueId().toString(), player.getAddress(), System.currentTimeMillis());
    }

    public static void removeAutologin(String playerName) {
        passworder.removeAutoLogin(playerName.toLowerCase());
    }

    public static void setLock(final String playerName) {
        Message.DB_LOCK.log();
        onDisable();
        passworder = PasswordManager.LOCK.getProvider();
        if (playerName != null && playerName.isEmpty())
            Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
                public void run() {
                    Player player = Server.getInstance().getPlayerExact(playerName);
                    if (player != null) player.close("", Message.LOCK_INFORM.getText());
                }
            }, 1);
        reInit();
    }

    private static TaskHandler task = null;

    public static void reInit() {
        if (task != null) return;
        Message.DB_RENIT_TRY.log();
        PasswordManager pp = getByName(Welcome.getCfg().passwordProvider);
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

    public static boolean restrictedByIp(Player player) {
        if (!Welcome.getCfg().registerRestrictions) return false;
        Long lastLoginTime = passworder.lastLoginFromIp(player.getName(), player.getAddress());
        if (lastLoginTime == null) return false;
        return (System.currentTimeMillis() - lastLoginTime) < Welcome.getCfg().getRestrictIpTime();
    }
}
