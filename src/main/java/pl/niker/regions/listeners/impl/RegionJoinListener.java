package pl.niker.regions.listeners.impl;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import pl.niker.regions.listeners.RegionJoinEvent;
import pl.niker.regions.managers.ElytraManager;
import pl.niker.regions.managers.RegionManager;
import pl.niker.regions.model.Region;
import pl.niker.regions.types.RegionFlagType;

import java.util.UUID;

public class RegionJoinListener implements Listener {
    private final RegionManager regionManager;
    private final ElytraManager elytraManager;

    public RegionJoinListener(RegionManager regionManager, ElytraManager elytraManager) {
        this.regionManager = regionManager;
        this.elytraManager = elytraManager;
    }

    @EventHandler
    public void onRegionJoin(RegionJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        Region region = regionManager.getRegion(uuid);
        if (region == null) return;

        if (!region.hasFlag(RegionFlagType.GLIDING) && p.isGliding() && !p.hasPermission("regions.bypass.gliding")) {
            elytraManager.changeElytra(p);
        }
    }
}
