package pl.niker.regions.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    private final JavaPlugin plugin;

    public CooldownManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean hasCooldown(UUID uuid, String key) {
        Map<UUID, Long> map = cooldowns.computeIfAbsent(key, k -> new HashMap<>());
        return map.getOrDefault(uuid, 0L) > System.currentTimeMillis();
    }

    public long getRemaining(UUID uuid, String key) {
        Map<UUID, Long> map = cooldowns.computeIfAbsent(key, k -> new HashMap<>());
        long remaining = map.getOrDefault(uuid, 0L) - System.currentTimeMillis();
        return Math.max(remaining, 0);
    }

    public void setCooldown(UUID uuid, String key, int seconds) {
        cooldowns.computeIfAbsent(key, k -> new HashMap<>())
                .put(uuid, System.currentTimeMillis() + (seconds * 1000L));
    }

    public void setCooldown(Player player, String key, int seconds, ItemStack item) {
        UUID uuid = player.getUniqueId();
        cooldowns.computeIfAbsent(key, k -> new HashMap<>())
                .put(uuid, System.currentTimeMillis() + (seconds * 1000L));

        if (item != null && item.getType() != Material.AIR) {
            Bukkit.getScheduler().runTask(
                    plugin,
                    () -> player.setCooldown(item.getType(), seconds * 20)
            );
        }
    }

    public void setCooldownLong(Player player, String key, long milliseconds, ItemStack item) {
        UUID uuid = player.getUniqueId();
        cooldowns.computeIfAbsent(key, k -> new HashMap<>())
                .put(uuid, System.currentTimeMillis() + milliseconds);

        if (item != null && item.getType() != Material.AIR) {
            int ticks = (int) (milliseconds / 50);
            Bukkit.getScheduler().runTask(
                    plugin,
                    () -> player.setCooldown(item.getType(), ticks)
            );
        }
    }

    public void clearCooldown(UUID uuid, String key) {
        Map<UUID, Long> map = cooldowns.get(key);
        if (map != null) {
            map.remove(uuid);
        }
    }

    public void clearAllPlayer(UUID uuid) {
        Iterator<Map<UUID, Long>> iterator = cooldowns.values().iterator();
        while (iterator.hasNext()) {
            Map<UUID, Long> map = iterator.next();
            map.remove(uuid);
        }
    }
}
