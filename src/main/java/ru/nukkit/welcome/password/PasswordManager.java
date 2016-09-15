package ru.nukkit.welcome.password;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.TaskHandler;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.players.PlayerManager;
import ru.nukkit.welcome.provider.PasswordProvider;
import ru.nukkit.welcome.provider.YamlProvider;
import ru.nukkit.welcome.provider.database.DatabaseProvider;
import ru.nukkit.welcome.provider.database_old.DatabaseOrmliteProvider;
import ru.nukkit.welcome.provider.redis.RedisProvider;
import ru.nukkit.welcome.provider.serverauth.ServerauthProvider;
import ru.nukkit.welcome.provider.serverauth.SimpleauthProvider;
import ru.nukkit.welcome.util.Message;
import ru.nukkit.welcome.util.Task;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public enum PasswordManager {
    YAML(YamlProvider.class),
    DATABASE(DatabaseProvider.class),
    SERVERAUTH(ServerauthProvider.class),
    SIMPLEAUTH(SimpleauthProvider.class),
    REDIS(RedisProvider.class),
    LOCK(PasswordLock.class),
    DATABASE_OLD(DatabaseOrmliteProvider.class);

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

    public static CompletableFuture<Boolean> checkPassword(String playerName, String pwdStr) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new Task() {
            @Override
            public void onRun() {
                result.complete(passworder.checkPassword(playerName.toLowerCase(), hashPassword(playerName.toLowerCase(), pwdStr)));
            }
        }.start();
        return result;
    }

    public static CompletableFuture<Boolean> checkPassword(Player player, String pwdStr) {
        return checkPassword(player.getName(), pwdStr);
    }

    public static CompletableFuture<Boolean> setPassword(String playerName, String pwdStr) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new Task() {
            @Override
            public void onRun() {
                result.complete(passworder.setPassword(playerName.toLowerCase(), hashPassword(playerName, pwdStr)));
            }
        }.start();
        return result;
    }

    public static CompletableFuture<Boolean> setPassword(Player player, String pwdStr) {
        return setPassword(player.getName(), pwdStr);
    }

    public static CompletableFuture<Boolean> hasPassword(String playerName) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new Task() {
            @Override
            public void onRun() {
                result.complete(passworder.hasPassword(playerName.toLowerCase()));
            }
        }.start();
        return result;
    }

    public static CompletableFuture<Boolean> hasPassword(Player player) {
        return hasPassword(player.getName());
    }

    public static CompletableFuture<Boolean> removePassword(String playerName) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new Task() {
            @Override
            public void onRun() {
                result.complete(passworder.removePassword(playerName.toLowerCase()));
            }
        }.start();
        return result;
    }

    public static CompletableFuture<Boolean> removePassword(Player player) {
        return removePassword(player.getName());
    }

    public static String hashPassword(String userName, String password) {
        return Welcome.getCfg().getHashAlgorithm().getHash(userName, password);
    }

    public static CompletableFuture<Boolean> checkAutologin(Player player) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        if (!Welcome.getCfg().autologinEnable) {
            result.complete(false);
        } else {
            hasPassword(player).whenComplete((hasPassword, e) -> {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    if (!hasPassword) {
                        result.complete(false);
                    } else {
                        result.complete(passworder.checkAutoLogin(player.getName().toLowerCase(), player.getUniqueId().toString(), player.getAddress()));
                    }
                }
            });
        }
        return result;
    }

    public static void updateAutologin(Player player, long time) {
        if (!PlayerManager.isPlayerLoggedIn(player)) return;
        updateAutologin(player.getName(), player.getUniqueId(), player.getAddress(), time);

    }

    public static void updateAutologin(Player player) {
        updateAutologin(player, System.currentTimeMillis());
    }

    public static void updateAutologin(String playerName, UUID uuid, String ip, long time) {
        hasPassword(playerName).whenComplete((hasPassword, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                if (hasPassword) {
                    new Task() {
                        @Override
                        public void onRun() {
                            passworder.updateAutoLogin(playerName.toLowerCase(), uuid.toString(), ip, time);
                        }
                    }.start();
                }
            }
        });
    }

    public static void updateAutologin(String playerName, UUID uuid, String ip) {
        updateAutologin (playerName, uuid, ip, System.currentTimeMillis());
    }

    public static void removeAutologin(String playerName) {
        new Task() {
            @Override
            public void onRun() {
                passworder.removeAutoLogin(playerName.toLowerCase());
            }
        }.start();
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

    public static CompletableFuture<Boolean> restrictedByIp(Player player) {
        CompletableFuture<Boolean> result = new CompletableFuture();
        if (!Welcome.getCfg().registerRestrictions) {
            result.complete(false);
        } else {
            new Task() {
                @Override
                public void onRun() {
                    Long lastLoginTime = passworder.lastLoginFromIp(player.getName(), player.getAddress());
                    if (lastLoginTime == null) {
                        result.complete(false);
                    } else {
                        result.complete((System.currentTimeMillis() - lastLoginTime) < Welcome.getCfg().getRestrictIpTime());
                    }
                }
            }.start();
        }
        return result;
    }
}
