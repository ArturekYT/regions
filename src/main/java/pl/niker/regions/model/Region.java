package pl.niker.regions.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import pl.niker.regions.types.RegionFlagType;

import java.util.ArrayList;
import java.util.List;

public class Region {
    private final String name;
    private final BoundingBox boundingBox;
    private final String worldName;
    private final int priority;
    private final List<RegionFlagType> flags;

    public Region(String name, Location loc1, Location loc2, int priority, List<RegionFlagType> flags) {
        this.name = name;
        this.boundingBox = BoundingBox.of(loc1, loc2);
        this.worldName = loc1.getWorld().getName();
        this.priority = priority;
        this.flags = new ArrayList<>(flags);
    }

    public Region(String name, BoundingBox boundingBox, String worldName, int priority, List<RegionFlagType> flags) {
        this.name = name;
        this.boundingBox = boundingBox;
        this.worldName = worldName;
        this.priority = priority;
        this.flags = new ArrayList<>(flags);
    }

    public boolean contains(Location loc) {
        return loc.getWorld().getName().equalsIgnoreCase(this.worldName) && boundingBox.contains(loc.toVector());
    }

    public String getName() {
        return (name == null ? "null" : name);
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public int getPriority() {
        return priority;
    }

    public List<RegionFlagType> getFlags() {
        return flags;
    }

    public boolean hasFlag(RegionFlagType type) {
        return flags.contains(type);
    }

    public void addFlag(RegionFlagType type) {
        if (!flags.contains(type)) {
            flags.add(type);
        }
    }

    public void removeFlag(RegionFlagType type) {
        if (flags.contains(type)) flags.remove(type);
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}