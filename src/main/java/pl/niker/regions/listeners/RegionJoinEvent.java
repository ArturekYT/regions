package pl.niker.regions.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String regionName;

    public RegionJoinEvent(Player player, String regionName) {
        this.player = player;
        this.regionName = regionName;
    }

    public Player getPlayer() { return player; }
    public String getRegionName() { return regionName; }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}