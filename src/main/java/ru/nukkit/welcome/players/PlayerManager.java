package ru.nukkit.welcome.players;

import cn.nukkit.Player;
import cn.nukkit.Server;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.password.PasswordProvider;
import ru.nukkit.welcome.password.PasswordValidator;
import ru.nukkit.welcome.util.LoginMeta;
import ru.nukkit.welcome.util.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private static Map<String,Long> waitLogin = new HashMap<String, Long>();
    public UUID getID (Player player){
        return null;
    }

    public static void enterServer (Player player){
        String playerName = player.getName();
        if (waitLogin.containsKey(playerName)) waitLogin.remove(playerName);
        if (player.hasMetadata("welcome-in-game")) player.removeMetadata("welcome-in-game", Welcome.getPlugin());
        if (PasswordProvider.checkAutologin(player)) {
            setPlayerLoggedIn(player);
            Message.LGN_AUTO.tip(5,player,'e','6',player.getName());
            return;
        }
        if (!PasswordProvider.hasPassword(player)) startWaitRegister (player);
        else startWaitLogin(player);
    }

    public static boolean isPlayerLoggedIn(Player player){
        return player.hasMetadata("welcome-in-game");
    }
    public static void setPlayerLoggedIn (Player player){
        player.setMetadata("welcome-in-game", new LoginMeta());
    }

    private static void startWaitRegister(final Player player) {
        if (!player.isOnline()) return;
        if (isPlayerRegistered(player)) return;
        String name = player.getName();
        if (!waitLogin.containsKey(name)) waitLogin.put(name,System.currentTimeMillis()+Welcome.getPlugin().getWaitTime());
        if (System.currentTimeMillis()<waitLogin.get(name)) {
            Message.TYPE_REG.tip(5,player);
            Welcome.getPlugin().getServer().getScheduler().scheduleDelayedTask(new Runnable() {
                public void run() {
                   startWaitRegister(player);
                }
            },200);
        } else player.kick(Message.KICK_TIMEOUT.getText(),false);
    }

    private static boolean isPlayerRegistered(Player player) {
        return PasswordProvider.hasPassword(player);
    }

    private static void startWaitLogin(final Player player) {
        if (!player.isOnline()) return;
        if (isPlayerLoggedIn(player)) return;
        String name = player.getName();
        if (!waitLogin.containsKey(name)) waitLogin.put(name,System.currentTimeMillis()+Welcome.getPlugin().getWaitTime()); //3 минуты - это всё потом в конфиг!
        if (System.currentTimeMillis()<waitLogin.get(name)) {
            Message.TYPE_LGN.tip(5,player);
            Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
                public void run() {
                    startWaitLogin (player);
                }
            },200);
        } else player.kick(Message.KICK_TIMEOUT.getText(),false);
    }


    public static boolean regCommand (Player player, String password1, String password2){
        if (isPlayerRegistered(player)) return Message.REG_ALREADY.print(player);
        if (isPlayerLoggedIn(player)) return Message.LGN_ALREADY.print(player);
        if (password1==null||password1.isEmpty()||password2==null||password2.isEmpty()) return Message.TYPE_REG.print(player,'c');
        if (!password1.equals(password2)) return Message.ERR_PWD_NOTMATCH.print(player,'c');
        if (!PasswordValidator.validatePassword(password1)) {
            Message.ERR_PWD_VALIDATE.print(player,'c');
            player.sendMessage(PasswordValidator.getInfo());
            return true;
        }
        PasswordProvider.setPassword(player,password1);
        setPlayerLoggedIn (player);
        Message.REG_LOG.log(player.getName(),"NOCOLOR");
        Message.REG_OK.print(player,'6');
        PasswordProvider.updateAutologin (player);
        return Message.REG_OK.tip(5,player,'6');
    }

    public static boolean loginCommand (Player player, String password){
        if (password==null||password.isEmpty()) return Message.LGN_MISS_PWD.print(player);
        if (isPlayerLoggedIn(player)) return Message.LGN_ALREADY.print(player);
        if (!PasswordProvider.checkPassword(player,password)) return Message.ERR_PWD_WRONG.print(player);
        setPlayerLoggedIn (player);
        Message.LGN_LOG.log(player.getName(),"NOCOLOR");
        Message.LGN_OK.print(player,'6');
        PasswordProvider.updateAutologin (player);
        return Message.LGN_OK.tip(5,player,'6');
    }


    public static boolean unregCommand(Player player, String password) {
        if (password==null||password.isEmpty()) return Message.UNREG_MISS_PWD.print(player);
        if (!PasswordProvider.checkPassword(player,password)) return Message.ERR_PWD_WRONG.print(player);
        PasswordProvider.removePassword(player);
        return player.kick(Message.UNREG_OK.getText(),false);
    }
}
