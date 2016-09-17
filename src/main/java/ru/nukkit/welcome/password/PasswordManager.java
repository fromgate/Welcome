package ru.nukkit.welcome.password;


import cn.nukkit.Player;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.players.PlayerManager;
import ru.nukkit.welcome.provider.PasswordProvider;
import ru.nukkit.welcome.provider.Providers;
import ru.nukkit.welcome.util.Task;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class PasswordManager {

    public static CompletableFuture<Boolean> checkPassword(String playerName, String pwdStr) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new Task() {
            @Override
            public void onRun() {
                result.complete(getProvider().checkPassword(playerName.toLowerCase(), hashPassword(playerName.toLowerCase(), pwdStr)));
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
                result.complete(getProvider().setPassword(playerName.toLowerCase(), hashPassword(playerName, pwdStr)));
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
                result.complete(getProvider().hasPassword(playerName.toLowerCase()));
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
                result.complete(getProvider().removePassword(playerName.toLowerCase()));
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
                        result.complete(getProvider().checkAutoLogin(player.getName().toLowerCase(), player.getUniqueId().toString(), player.getAddress()));
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
                            getProvider().updateAutoLogin(playerName.toLowerCase(), uuid.toString(), ip, time);
                        }
                    }.start();
                }
            }
        });
    }

    public static void updateAutologin(String playerName, UUID uuid, String ip) {
        updateAutologin(playerName, uuid, ip, System.currentTimeMillis());
    }

    public static void removeAutologin(String playerName) {
        new Task() {
            @Override
            public void onRun() {
                getProvider().removeAutoLogin(playerName.toLowerCase());
            }
        }.start();
    }

    public static CompletableFuture<Boolean> restrictedByIp(Player player) {
        CompletableFuture<Boolean> result = new CompletableFuture();
        if (!Welcome.getCfg().registerRestrictions) {
            result.complete(false);
        } else {
            new Task() {
                @Override
                public void onRun() {
                    Long lastLoginTime = getProvider().lastLoginFromIp(player.getName(), player.getAddress());
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

    private static PasswordProvider getProvider() {
        return Providers.getCurrentProvider();
    }
}
