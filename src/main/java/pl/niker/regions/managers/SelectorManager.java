package pl.niker.regions.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.niker.regions.types.ActionType;

import java.util.*;

public class SelectorManager {
    private final HashMap<UUID, Location> left = new HashMap<>();
    private final HashMap<UUID, Location> right = new HashMap<>();
    private final JavaPlugin plugin;

    public SelectorManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void selectPosition(Player player, ActionType type, Location loc) {
        UUID uuid = player.getUniqueId();
        if (type == ActionType.LEFT) left.put(uuid, loc);
        else if (type == ActionType.RIGHT) right.put(uuid, loc);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            left.remove(uuid);
            right.remove(uuid);
        }, 5 * 60 * 20);
    }

    public Location getSelectedLocation(Player player, ActionType type) {
        UUID uuid = player.getUniqueId();

        if (left != null && type == ActionType.LEFT) return left.get(uuid);
        else if (right != null && type == ActionType.RIGHT) return right.get(uuid);

        return null;
    }
}
