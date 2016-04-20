package ru.nukkit.welcome.password;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.TaskHandler;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.provider.PasswordProvider;
import ru.nukkit.welcome.provider.YamlProvider;
import ru.nukkit.welcome.provider.database.DatabaseProvider;
import ru.nukkit.welcome.players.PlayerManager;
import ru.nukkit.welcome.provider.redis.RedisProvider;
import ru.nukkit.welcome.provider.serverauth.ServerauthProvider;
import ru.nukkit.welcome.util.Message;

public enum PasswordManager {
    YAML (YamlProvider.class),
    DATABASE (DatabaseProvider.class),
    SERVERAUTH (ServerauthProvider.class),
    REDIS (RedisProvider.class),
    LOCK (PasswordLock.class);

    Class<? extends PasswordProvider> clazz;
    PasswordManager(Class<? extends PasswordProvider> clazz) {
        this.clazz = clazz;
    }

    public PasswordProvider getProvider(){
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private static PasswordProvider passworder;

    public static void init(){
        PasswordManager pp = getByName(Welcome.getCfg().passwordProvider);
        passworder = pp==null ? null :  pp.getProvider();
        if (passworder==null||!passworder.isEnabled()) {
            pp = PasswordManager.LOCK;
            setLock(null);
        }
        Message.DB_INIT.log(pp.name(),Welcome.getCfg().getHashAlgorithm());
        if (pp== PasswordManager.YAML&&Server.getInstance().getPluginManager().getPlugin("DbLib") != null)
            Message.DB_DBLIB_FOUND.log();
    }

    public static PasswordManager getByName(String pwdProv){
        for (PasswordManager pp : PasswordManager.values())
            if (pp.name().equalsIgnoreCase(pwdProv)) return pp;
        return null;
    }

    public static boolean checkPassword (String playerName, String pwdStr){
        return passworder.checkPassword(playerName,hashPassword(pwdStr));
    }

    public static boolean checkPassword (Player player, String pwdStr){
        return passworder.checkPassword(player.getName().toLowerCase(),hashPassword(pwdStr));
    }

    public static boolean setPassword (String playerName, String pwdStr){
        return passworder.setPassword(playerName,hashPassword(pwdStr));
    }

    public static boolean setPassword (Player player, String pwdStr){
        return passworder.setPassword(player.getName().toLowerCase(),hashPassword(pwdStr));
    }

    public static boolean hasPassword(String playerName) {
        return passworder.hasPassword(playerName);
    }

    public static boolean hasPassword(Player player) {
        return passworder.hasPassword(player.getName().toLowerCase());
    }

    public static boolean removePassword(Player player) {
        return  passworder.removePassword(player.getName().toLowerCase());
    }

    public static boolean removePassword(String playerName) {
        return  passworder.removePassword(playerName);
    }

    public static String hashPassword (String password){
        return Welcome.getCfg().getHashAlgorithm().getHash(password);
    }


    public static boolean checkAutologin (Player player){
        if (!hasPassword(player)) return false;
        if (!Welcome.getCfg().autologinEnable) return false;
        return passworder.checkAutoLogin(player.getName().toLowerCase(), player.getUniqueId().toString(), player.getAddress());
    }

    public static void updateAutologin(Player player, long time) {
        if (!hasPassword(player)) return;
        if (!PlayerManager.isPlayerLoggedIn(player)) return;
        passworder.updateAutoLogin(player.getName().toLowerCase(), player.getUniqueId().toString(), player.getAddress(),time);
    }

    public static void updateAutologin(Player player) {
        if (!hasPassword(player)) return;
        if (!PlayerManager.isPlayerLoggedIn(player)) return;
        passworder.updateAutoLogin(player.getName().toLowerCase(), player.getUniqueId().toString(), player.getAddress(),System.currentTimeMillis());
    }

    public static void removeAutologin(String playerName) {
        passworder.removeAutoLogin(playerName);
    }

    public static void setLock(final String playerName){
        Message.DB_LOCK.log();
        onDisable();
        passworder = PasswordManager.LOCK.getProvider();
        if (playerName != null&&playerName.isEmpty())
            Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
                public void run() {
                    Player player = Server.getInstance().getPlayerExact(playerName);
                    if (player!=null) player.close("", Message.LOCK_INFORM.getText());
                }
            }, 1);
        reInit();
    }

    private static TaskHandler task=null;
    public static void reInit(){
        if (task!=null) return;
        Message.DB_RENIT_TRY.log();
        PasswordManager pp = getByName(Welcome.getCfg().passwordProvider);
        PasswordProvider pwd = (pp==null) ? null : pp.getProvider();
        if (pwd != null&&pwd.isEnabled()){
            passworder = pwd;
            Message.DB_REINIT.log(pp.name(),Welcome.getCfg().getHashAlgorithm());
        } else task = Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable(){
            public void run() {
                task = null;
                reInit();
            }
        }, Welcome.getCfg().getReinitTimeTicks());
    }

    public static void onDisable() {
        if (passworder!=null) passworder.onDisable();
    }

    public static boolean restrictedByIp(Player player) {
        if (!Welcome.getCfg().registerRestrictions) return false;
        Long lastLoginTime = passworder.lastLoginFromIp(player.getName(),player.getAddress());
        if (lastLoginTime == null) return true;
        return (System.currentTimeMillis()-lastLoginTime)<Welcome.getCfg().getRestrictIpTime();
    }
}
