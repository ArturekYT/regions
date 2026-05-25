package pl.niker.regions.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String regionName;
    private final Location from;
    private final Location to;

    public RegionJoinEvent(Player player, String regionName, Location from, Location to) {
        this.player = player;
        this.regionName = regionName;
        this.from = from;
        this.to = to;
    }

    public Player getPlayer() { return player; }
    public Location getFrom() { return from; }
    public Location getTo() { return to; }
    public String getRegionName() { return regionName; }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}