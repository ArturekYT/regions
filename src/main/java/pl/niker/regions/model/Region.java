package pl.niker.regions.model;

import org.bukkit.Location;
import pl.niker.regions.types.RegionFlagType;

import java.util.ArrayList;
import java.util.List;

public class Region {
    private final String name;
    private final double minX, maxX, minY, maxY, minZ, maxZ;
    private final Location loc1;
    private final Location loc2;
    private final int priority;
    private final List<RegionFlagType> flags;

    public Region(String name, Location loc1, Location loc2, int priority, List<RegionFlagType> flags) {
        this.name = name;
        this.minX = Math.min(loc1.getX(), loc2.getX());
        this.maxX = Math.max(loc1.getX(), loc2.getX());
        this.minY = Math.min(loc1.getY(), loc2.getY());
        this.maxY = Math.max(loc1.getY(), loc2.getY());
        this.minZ = Math.min(loc1.getZ(), loc2.getZ());
        this.maxZ = Math.max(loc1.getZ(), loc2.getZ());
        this.priority = priority;
        this.flags = new ArrayList<>(flags);
        this.loc1 = loc1;
        this.loc2 = loc2;
    }


    public boolean contains(Location loc) {
        return loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    public String getName() {
        return (name == null ? "null" : name);
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
}