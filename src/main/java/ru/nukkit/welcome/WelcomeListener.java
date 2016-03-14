package ru.nukkit.welcome;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerPreLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import ru.nukkit.welcome.password.PasswordProvider;
import ru.nukkit.welcome.players.PlayerManager;
import ru.nukkit.welcome.util.Message;

public class WelcomeListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        PlayerManager.enterServer(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        PasswordProvider.updateAutologin(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPrelogin(PlayerPreLoginEvent event) {
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            if (player.equals(event.getPlayer())) continue;
            if (player.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                event.setKickMessage(Message.ALREADY_LOGGED_IN.getText(event.getPlayer().getName()));
                event.setCancelled();
            }
        }
    }
}
