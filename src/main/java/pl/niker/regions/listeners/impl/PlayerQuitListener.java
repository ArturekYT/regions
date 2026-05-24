package pl.niker.regions.listeners.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.niker.regions.managers.RegionManager;

public class PlayerQuitListener implements Listener {
    private final RegionManager regionManager;

    public PlayerQuitListener(RegionManager regionManager) {
        this.regionManager = regionManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        regionManager.clearCache(p);
    }
}
