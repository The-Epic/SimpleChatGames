package xyz.epicebic.simplechatgames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.epicebic.simplechatgames.managers.DataManager;
import xyz.epicebic.simplechatgames.managers.StorageManager;

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void onPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        DataManager.getInstance().loadPlayerData(event.getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        DataManager.getInstance().unloadQueue(event.getPlayer().getUniqueId());
    }
}
