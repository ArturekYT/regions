package pl.niker.regions.managers;

import org.bukkit.*;
import org.bukkit.configuration.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.*;
import org.bukkit.util.BoundingBox;
import pl.niker.regions.model.Region;
import pl.niker.regions.types.RegionFlagType;

import java.util.*;

public class RegionManager {
    private final JavaPlugin plugin;
    private final Map<String, Region> regions = new HashMap<>();
    private final Map<UUID, Set<String>> playerCurrentRegions = new HashMap<>();

    public RegionManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void createRegion(String name, Location loc1, Location loc2, int priority) {
        List<RegionFlagType> flags = new ArrayList<>(List.of(RegionFlagType.BLOCK_PLACE, RegionFlagType.FIRE, RegionFlagType.BLOCK_BREAK, RegionFlagType.PVP, RegionFlagType.PHISIC, RegionFlagType.GLIDING, RegionFlagType.USE, RegionFlagType.SLEEP, RegionFlagType.MOB_DAMAGE, RegionFlagType.ENDERPEARL, RegionFlagType.CHORUS_TELEPORT, RegionFlagType.ITEM_DROP, RegionFlagType.ITEM_PICKUP, RegionFlagType.DAMAGE));
        Region region = new Region(name, loc1, loc2, priority, flags);

        regions.put(name, region);

        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("regions");
        if (sec == null) sec = plugin.getConfig().createSection("regions");

        ConfigurationSection regSec = sec.createSection(name);
        regSec.set("priority", priority);
        List<String> flagNames = new ArrayList<>();
        for (RegionFlagType flag : flags) flagNames.add(flag.name());
        regSec.set("flags", flagNames);

        regSec.set("loc1.world", loc1.getWorld().getName());
        regSec.set("loc1.x", loc1.getX());
        regSec.set("loc1.y", loc1.getY());
        regSec.set("loc1.z", loc1.getZ());

        regSec.set("loc2.world", loc2.getWorld().getName());
        regSec.set("loc2.x", loc2.getX());
        regSec.set("loc2.y", loc2.getY());
        regSec.set("loc2.z", loc2.getZ());

        plugin.saveConfig();
    }

    public void addFlag(String name, RegionFlagType flag) {
        Region region = regions.get(name);
        if (region == null) return;
        region.addFlag(flag);
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("regions." + name);
        if (sec == null) return;
        List<String> flagNames = new ArrayList<>();
        for (RegionFlagType f : region.getFlags()) flagNames.add(f.name());
        sec.set("flags", flagNames);
        plugin.saveConfig();
    }

    public void removeFlag(String name, RegionFlagType flag) {
        Region region = regions.get(name);
        if (region == null) return;
        region.removeFlag(flag);
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("regions." + name);
        if (sec == null) return;
        List<String> flagNames = new ArrayList<>();
        for (RegionFlagType f : region.getFlags()) flagNames.add(f.name());
        sec.set("flags", flagNames);
        plugin.saveConfig();
    }

    public void removeRegion(String name) {
        regions.remove(name);

        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("regions");
        if (sec != null) {
            sec.set(name, null);
            plugin.saveConfig();
        }
    }

    public void removeRegion(Region region) {
        regions.remove(region.getName());

        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("regions");
        if (sec != null) {
            sec.set(region.getName(), null);
            plugin.saveConfig();
        }
    }

    public void loadRegions() {
        regions.clear();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("regions");
        if (sec == null) return;

        for (String key : sec.getKeys(false)) {
            ConfigurationSection regSec = sec.getConfigurationSection(key);
            if (regSec == null) continue;

            int priority = regSec.getInt("priority");
            List<String> stringFlags = regSec.getStringList("flags");
            List<RegionFlagType> loadedFlags = new ArrayList<>();
            for (String f : stringFlags) {
                try {
                    loadedFlags.add(RegionFlagType.valueOf(f));
                } catch (IllegalArgumentException ignored) {}
            }
            Location loc1 = new Location(
                    plugin.getServer().getWorld(regSec.getString("loc1.world")),
                    regSec.getDouble("loc1.x"),
                    regSec.getDouble("loc1.y"),
                    regSec.getDouble("loc1.z")
            );
            Location loc2 = new Location(
                    plugin.getServer().getWorld(regSec.getString("loc2.world")),
                    regSec.getDouble("loc2.x"),
                    regSec.getDouble("loc2.y"),
                    regSec.getDouble("loc2.z")
            );

            regions.put(key, new Region(key, loc1, loc2, priority, loadedFlags));
        }
    }

    public void updatePlayerRegions(UUID uuid, Location loc) {
        Set<String> found = new HashSet<>();
        for (Region r : regions.values()) {
            if (r.contains(loc)) {
                found.add(r.getName());
            }
        }
        if (found.isEmpty()) {
            playerCurrentRegions.remove(uuid);
        } else {
            playerCurrentRegions.put(uuid, found);
        }
    }

    public Set<String> getPlayerRegions(UUID uuid) {
        return playerCurrentRegions.getOrDefault(uuid, Collections.emptySet());
    }

    public Set<String> getRegionNamesAtLocation(Location loc) {
        Set<String> found = new HashSet<>();
        for (Region r : regions.values()) {
            if (r.contains(loc)) {
                found.add(r.getName());
            }
        }
        return found;
    }

    public Region getRegion(UUID uuid) {
        Set<String> current = playerCurrentRegions.get(uuid);
        if (current == null || current.isEmpty()) return null;
        Region highestPriorityRegion = null;
        int maxPriority = Integer.MIN_VALUE;
        for (String name : current) {
            Region region = regions.get(name);
            if (region != null && region.getPriority() > maxPriority) {
                maxPriority = region.getPriority();
                highestPriorityRegion = region;
            }
        }
        return highestPriorityRegion;
    }

    public Region getRegion(String regionName) {
        for (Region r : regions.values()) {
            if (r.getName().equals(regionName)) return r;
        }
        return null;
    }

    public Region getRegion(Location loc) {
        Set<String> current = getRegionNamesAtLocation(loc);
        if (current == null || current.isEmpty()) return null;
        Region highestPriorityRegion = null;
        int maxPriority = Integer.MIN_VALUE;
        for (String name : current) {
            Region region = regions.get(name);
            if (region != null && region.getPriority() > maxPriority) {
                maxPriority = region.getPriority();
                highestPriorityRegion = region;
            }
        }
        return highestPriorityRegion;
    }

    public List<String> getNearbyRegions(Location loc, double distance) {
        if (loc == null || loc.getWorld() == null) return List.of();

        List<String> nearby = new ArrayList<>();
        BoundingBox searchBox = BoundingBox.of(loc.toVector(), distance, distance, distance);

        for (Region region : regions.values()) {
            if (!loc.getWorld().equals(region.getWorld())) continue;

            if (region.getBoundingBox().overlaps(searchBox)) {
                nearby.add(region.getName());
            }
        }
        return nearby;
    }

    public boolean isInRegion(Player player, String name) {
        Set<String> current = playerCurrentRegions.get(player.getUniqueId());
        return current != null && current.contains(name);
    }

    public boolean isInRegions(Player player, String... names) {
        Set<String> current = playerCurrentRegions.get(player.getUniqueId());
        if (current == null) return false;
        for (String name : names) {
            if (current.contains(name)) return true;
        }
        return false;
    }

    public Region getRegionByName(String name) {
        return regions.get(name);
    }

    public List<RegionFlagType> getAllFlagTypes() {
        return Arrays.asList(RegionFlagType.values());
    }

    public Map<String, Region> getRegions() {
        return regions;
    }

    public void clearCache(Player player) {
        UUID uuid = player.getUniqueId();
        playerCurrentRegions.remove(uuid);
    }
}