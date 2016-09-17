package ru.nukkit.welcome.players;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.potion.Effect;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.password.PasswordManager;
import ru.nukkit.welcome.password.PasswordValidator;
import ru.nukkit.welcome.util.LoginMeta;
import ru.nukkit.welcome.util.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public class PlayerManager {
    private static Map<String, Long> waitLogin = new HashMap<String, Long>();
    private static Map<String, Integer> authAttempts = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);

    public static void enterServer(Player player) {
        if (authAttempts.containsKey(player.getName())) authAttempts.remove(player.getName());
        String playerName = player.getName();
        if (waitLogin.containsKey(playerName)) waitLogin.remove(playerName);
        if (player.hasMetadata("welcome-in-game")) player.removeMetadata("welcome-in-game", Welcome.getPlugin());
        PasswordManager.checkAutologin(player).whenComplete((autoLogin, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                if (autoLogin) {
                    setPlayerLoggedIn(player);
                    tipOrPrint(player, Message.LGN_AUTO, 'e', '6', player.getName());
                    Welcome.getCfg().broadcastLoginMessage(player);
                } else {
                    setBlindEffect(player);
                    PasswordManager.hasPassword(player).whenComplete((hasPassword, e2) -> {
                        if (e2 != null) {
                            e.printStackTrace();
                        } else {
                            if (hasPassword) {
                                startWaitLogin(player);
                            } else {
                                startWaitRegister(player);
                            }
                        }
                    });
                }
            }
        });
    }

    public static boolean isPlayerLoggedIn(Player player) {
        return player.hasMetadata("welcome-in-game");
    }

    public static void setPlayerLoggedIn(Player player) {
        player.setMetadata("welcome-in-game", new LoginMeta());
    }

    public static void setPlayerLoggedOff(Player player) {
        if (player.hasMetadata("welcome-in-game"))
            player.removeMetadata("welcome-in-game", Welcome.getPlugin());
    }

    private static void startWaitRegister(final Player player) {
        if (!player.isOnline()) return;
        isPlayerRegistered(player).whenComplete((registered, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                if (!registered) {
                    String name = player.getName();
                    if (!waitLogin.containsKey(name)) {
                        waitLogin.put(name, System.currentTimeMillis() + Welcome.getCfg().getWaitTime());
                    }
                    if (System.currentTimeMillis() < waitLogin.get(name)) {
                        tipOrPrint(player, Welcome.getPlugin().getCfg().getTypeReg());
                        Welcome.getPlugin().getServer().getScheduler().scheduleDelayedTask(new Runnable() {
                            public void run() {
                                startWaitRegister(player);
                            }
                        }, Welcome.getCfg().getMessageRepeatTicks());
                    } else player.kick(Message.KICK_TIMEOUT.getText(), false);
                }
            }
        });
    }

    public static CompletableFuture<Boolean> isPlayerRegistered(Player player) {
        return PasswordManager.hasPassword(player);
    }

    private static void startWaitLogin(final Player player) {
        if (!player.isOnline()) return;
        if (isPlayerLoggedIn(player)) return;
        String name = player.getName();
        if (!waitLogin.containsKey(name)) {
            waitLogin.put(name, System.currentTimeMillis() + Welcome.getCfg().getWaitTime());
        }
        if (System.currentTimeMillis() < waitLogin.get(name)) {

            tipOrPrint(player, Welcome.getCfg().typeInChat ? Message.TYPE_LGN_CHAT : Message.TYPE_LGN);

            Server.getInstance().getScheduler().scheduleDelayedTask(() -> {
                startWaitLogin(player);
            }, Welcome.getCfg().getMessageRepeatTicks());

        } else player.kick(Message.KICK_TIMEOUT.getText(), false);
    }

    public static void regCommand(final Player player, final String password1, final String password2) {
        if (isPlayerLoggedIn(player)) {
            Message.LGN_ALREADY.print(player);
            return;
        }

        isPlayerRegistered(player).whenComplete((registered, ex) -> {

            if (ex != null) {
                ex.printStackTrace();
            } else {
                if (registered) {
                    Message.REG_ALREADY.print(player);
                } else {
                    PasswordManager.restrictedByIp(player).whenComplete((restrict, e) -> {
                        if (e != null) {
                            e.printStackTrace();
                        } else {
                            if (restrict) {
                                Server.getInstance().getScheduler().scheduleTask(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (player.isOnline()) {
                                            player.close("", Message.REG_RESTRICED_IP.getText());
                                        }
                                    }
                                });
                            } else {
                                String pwd1 = password1;
                                String pwd2 = password2;
                                if (!Welcome.getCfg().passwordConfirmation) pwd2 = pwd1;

                                if (pwd1 == null || pwd1.isEmpty() || pwd2 == null || pwd2.isEmpty()) {
                                    Welcome.getCfg().getTypeReg().print(player, 'c');
                                    return;
                                }

                                if (!pwd1.equals(pwd2)) {
                                    Message.ERR_PWD_NOTMATCH.print(player, 'c');
                                    return;
                                }
                                if (!PasswordValidator.validatePassword(pwd1)) {
                                    Message.ERR_PWD_VALIDATE.print(player, 'c');
                                    player.sendMessage(PasswordValidator.getInfo());
                                    return;
                                }
                                PasswordManager.setPassword(player, pwd1).whenComplete((reg, e2) -> {
                                    if (e2 != null) {
                                        e2.printStackTrace();
                                    } else {
                                        if (reg) {
                                            clearBlindEffect(player);
                                            setPlayerLoggedIn(player);
                                            Message.REG_LOG.log(player.getName(), "NOCOLOR");
                                            if (Welcome.getCfg().useTips) Message.REG_OK.print(player, '6');
                                            PasswordManager.updateAutologin(player);
                                            tipOrPrint(player, Message.REG_OK, '6');
                                            Welcome.getCfg().broadcastLoginMessage(player);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    public static void loginCommand(final Player player, final String password) {
        if (password == null || password.isEmpty()) {
            Message.LGN_MISS_PWD.print(player);
            return;
        }

        if (isPlayerLoggedIn(player)) {
            Message.LGN_ALREADY.print(player);
            return;
        }

        isPlayerRegistered(player).whenComplete((registered, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                if (registered) {
                    PasswordManager.checkPassword(player, password).whenComplete((pwdOk, e2) -> {
                        if (e2 != null) {
                            e2.printStackTrace();
                        } else {
                            if (pwdOk) {
                                setPlayerLoggedIn(player);
                                Message.LGN_LOG.log(player.getName(), "NOCOLOR");
                                Message.LGN_OK.print(player, '6');
                                PasswordManager.updateAutologin(player);
                                clearBlindEffect(player);
                                tipOrPrint(player, Message.LGN_OK, '6');
                                Welcome.getCfg().broadcastLoginMessage(player);
                            } else {
                                if (Welcome.getCfg().loginAtempts) {
                                    String name = player.getName();
                                    int attempt = authAttempts.containsKey(name) ? authAttempts.get(name) : 0;
                                    attempt++;
                                    if (attempt >= Welcome.getCfg().loginAtemptsMax) {
                                        player.close("", Message.LGN_ATTEMPT_EXCEED.getText('c'));
                                        authAttempts.remove(name);
                                        Message.LGN_ATTEMPT_EXCEED_LOG.log(name);
                                        return;
                                    } else authAttempts.put(name, attempt);
                                }
                                Message.ERR_PWD_WRONG.print(player);
                            }
                        }
                    });
                } else {
                    Message.LGN_NEED_REG.print(player);
                }
            }
        });
    }

    public static void logOff(final Player player) {
        if (!isPlayerLoggedIn(player)) {
            Message.ERR_NOT_LOGGED.print(player);
            return;
        }
        PasswordManager.updateAutologin(player, 0);
        setPlayerLoggedOff(player);
        player.kick(Message.LOGOFF_OK.getText(), false);
    }

    public static void unregCommand(Player player, String password) {
        if (password == null || password.isEmpty()) {
            Message.UNREG_MISS_PWD.print(player);
            return;
        }


        PasswordManager.checkPassword(player, password).whenComplete((pwdOk, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                if (pwdOk) {
                    PasswordManager.removeAutologin(player.getName());
                    PasswordManager.removePassword(player).whenComplete((remove, e2) -> {
                        player.kick(Message.UNREG_OK.getText(), false);
                    });
                } else {
                    Message.ERR_PWD_WRONG.print(player);
                }
            }
        });
    }

    public static void clearBlindEffect(Player player) {
        if (!Welcome.getCfg().setBlindEffect) return;
        if (player.hasEffect(Effect.BLINDNESS))
            player.removeEffect(Effect.BLINDNESS);
    }

    public static void setBlindEffect(Player player) {
        if (!Welcome.getCfg().setBlindEffect) return;
        Effect effect = Effect.getEffect(Effect.BLINDNESS);
        effect.setAmbient(false);
        effect.setDuration(Integer.MAX_VALUE);
        effect.setAmplifier(10);
        player.addEffect(effect);
    }

    private static boolean tipOrPrint(Player player, Message message, Object... params) {
        if (Welcome.getCfg().useTips) message.tip(5, player, params);
        else message.print(player, params);
        return true;
    }
}
