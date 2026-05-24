package pl.niker.regions.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import pl.niker.regions.types.ActionType;

import java.util.*;

public class SelectorManager {
    public final HashMap<UUID, Location> left = new HashMap<>();
    public final HashMap<UUID, Location> right = new HashMap<>();
    private final JavaPlugin plugin;

    private final ItemStack selectorItem;

    public SelectorManager(JavaPlugin plugin) {
        this.plugin = plugin;

        {
            selectorItem = new ItemStack(Material.WOODEN_AXE);
            ItemMeta meta = selectorItem.getItemMeta();
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "selector"), PersistentDataType.INTEGER, 0);
            selectorItem.setItemMeta(meta);
        }
    }

    public ItemStack getSelectorItem() {
        return selectorItem;
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
