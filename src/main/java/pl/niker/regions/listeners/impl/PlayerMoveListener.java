package pl.niker.regions.listeners.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.niker.regions.listeners.RegionJoinEvent;
import pl.niker.regions.listeners.RegionLeaveEvent;
import pl.niker.regions.managers.RegionManager;

import java.util.*;

public class PlayerMoveListener implements Listener {
    private final RegionManager regionManager;

    public PlayerMoveListener(RegionManager regionManager) {
        this.regionManager = regionManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location to = e.getTo();
        if (to == null) return;

        Location from = e.getFrom();
        if (from.getWorld().equals(to.getWorld()) && e.getFrom().equals(e.getTo())) return;

        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        Set<String> oldRegions = new HashSet<>(regionManager.getPlayerRegions(uuid));
        regionManager.updatePlayerRegions(uuid, to);
        Set<String> newRegions = regionManager.getPlayerRegions(uuid);

        for (String name : newRegions) {
            if (!oldRegions.contains(name)) {
                Bukkit.getPluginManager().callEvent(new RegionJoinEvent(p, name, from, to));
            }
        }

        for (String name : oldRegions) {
            if (!newRegions.contains(name)) {
                Bukkit.getPluginManager().callEvent(new RegionLeaveEvent(p, name));
            }
        }
    }
}