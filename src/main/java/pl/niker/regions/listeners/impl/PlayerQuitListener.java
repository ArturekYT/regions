package pl.niker.regions.listeners.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.niker.regions.managers.RegionManager;
import pl.niker.regions.managers.SelectorManager;

import java.util.UUID;

public class PlayerQuitListener implements Listener {
    private final RegionManager regionManager;
    private final SelectorManager selectorManager;

    public PlayerQuitListener(RegionManager regionManager, SelectorManager selectorManager) {
        this.regionManager = regionManager;
        this.selectorManager = selectorManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        regionManager.clearCache(p);
        selectorManager.left.remove(uuid);
        selectorManager.right.remove(uuid);
    }
}
