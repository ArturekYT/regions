package pl.niker.regions.listeners.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.niker.regions.managers.SelectorManager;
import pl.niker.regions.types.ActionType;

public class PlayerInteractListener implements Listener {
    private final SelectorManager selectorManager;

    public PlayerInteractListener(SelectorManager selectorManager) {
        this.selectorManager = selectorManager;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block clicked = e.getClickedBlock();
        if (clicked==null) return;
        Location loc = clicked.getLocation();
        if (loc==null) return;

        if (e.getAction().isRightClick() && p.getInventory().getItemInMainHand().getType() == Material.WOODEN_AXE) {
            selectorManager.selectPosition(p, ActionType.RIGHT, loc);
            e.setCancelled(true);
        }
        else if (e.getAction().isLeftClick() && p.getInventory().getItemInMainHand().getType() == Material.WOODEN_AXE) {
            selectorManager.selectPosition(p, ActionType.LEFT, loc);
            e.setCancelled(true);
        }
    }
}
