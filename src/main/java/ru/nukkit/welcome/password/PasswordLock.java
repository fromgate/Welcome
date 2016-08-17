package ru.nukkit.welcome.password;

import cn.nukkit.Player;
import cn.nukkit.Server;
import ru.nukkit.welcome.provider.PasswordProvider;
import ru.nukkit.welcome.util.Message;

public class PasswordLock implements PasswordProvider {
    public boolean isEnabled() {
        return true;
    }

    public boolean checkPassword(String playerName, String password) {
        informPlayer(playerName);
        return false;
    }

    public boolean setPassword(String playerName, String password) {
        informPlayer(playerName);
        return false;
    }

    public boolean hasPassword(String playerName) {
        informPlayer(playerName);
        return false;
    }

    public boolean removePassword(String playerName) {
        informPlayer(playerName);
        return false;
    }

    public Long lastLoginFromIp(String playerNane, String ip) {
        return null;
    }

    public boolean checkAutoLogin(String playerName, String uuid, String ip) {
        informPlayer(playerName);
        return false;
    }

    public void updateAutoLogin(String playerName, String uuid, String ip) {
    }

    public void updateAutoLogin(String playerName, String uuid, String ip, long currentTime) {
    }

    public boolean removeAutoLogin(String playerName) {
        return false;
    }

    public void onDisable() {
    }

    private void informPlayer(final String playerName) {
        Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
            public void run() {
                Player player = Server.getInstance().getPlayerExact(playerName);
                if (player != null) player.close("", Message.LOCK_INFORM.getText());
            }
        }, 1);
    }
}
