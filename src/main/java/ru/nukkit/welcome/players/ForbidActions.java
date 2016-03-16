package ru.nukkit.welcome.players;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.*;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.commands.Commander;
import ru.nukkit.welcome.util.Message;

public class ForbidActions implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        Player player = null;
        if (event.getEntity() instanceof Player) player = (Player) event.getEntity();
        else if (event instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if (damager instanceof Player) player = (Player) damager;
        } else if (event instanceof EntityDamageByChildEntityEvent) {
            Entity damager = ((EntityDamageByChildEntityEvent) event).getDamager();
            if (damager instanceof Player) player = (Player) damager;
        }
        if (player != null) cancel(player, event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryItemPickup(InventoryPickupItemEvent event) {
        for (Player player : event.getViewers()) {
            cancel(player, event);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryArrowPickup(InventoryPickupArrowEvent event) {
        for (Player player : event.getViewers()) {
            cancel(player, event);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCraftItemEvent(CraftItemEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerChatEvent(PlayerChatEvent event) {
        if (Welcome.getCfg().blockChat) cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (PlayerManager.isPlayerLoggedIn(event.getPlayer())) return;
        String cmd = Commander.getCommandByAlias(event.getMessage().substring(1).split(" ")[0]);
        Message.debugMessage("cmd: " + (cmd == null ? "null" : cmd));
        if (cmd != null && (cmd.equalsIgnoreCase("register") || cmd.equalsIgnoreCase("login")))
            return; // Разрешаем только команды для регистрации
        event.setCancelled();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerFoodLevelChangeEvent(PlayerFoodLevelChangeEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        cancel(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        cancel(event.getPlayer(), event);
    }

    private void cancel(Player player, Cancellable event) {
        if (!PlayerManager.isPlayerLoggedIn(player)) event.setCancelled();
    }
}
