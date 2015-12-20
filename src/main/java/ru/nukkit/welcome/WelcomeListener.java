package ru.nukkit.welcome;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import ru.nukkit.welcome.password.PasswordProvider;
import ru.nukkit.welcome.players.PlayerManager;

public class WelcomeListener implements Listener {
    @EventHandler (ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onJoin (PlayerJoinEvent event){
        PlayerManager.enterServer(event.getPlayer());
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onQuit (PlayerQuitEvent event){
        if (PasswordProvider.hasPassword(event.getPlayer()))
            PasswordProvider.checkAutologin(event.getPlayer());
    }
}
