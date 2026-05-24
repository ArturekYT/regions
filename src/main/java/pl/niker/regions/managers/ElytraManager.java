package pl.niker.regions.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pl.niker.regions.util.TextUtil;

public class ElytraManager {
    private final JavaPlugin plugin;
    private final CooldownManager cooldownManager;

    public ElytraManager(JavaPlugin plugin, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
    }

    public void changeElytra(Player player) {
        ItemStack chestplate = null;
        player.setGliding(false);

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() == Material.NETHERITE_CHESTPLATE || item.getType() == Material.DIAMOND_CHESTPLATE) {
                chestplate = item;
                break;
            }
        }

        if (chestplate != null && player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.ELYTRA) {
            ItemStack currentElytra = player.getInventory().getChestplate().clone();

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getLocation(), currentElytra);
                } else {
                    player.getInventory().addItem(currentElytra);
                }
            }, 1);

            player.getInventory().setChestplate(chestplate.clone());
            player.getInventory().remove(chestplate);
            player.setGliding(false);
            player.teleport(player.getLocation());

            if (!cooldownManager.hasCooldown(player.getUniqueId(), "firework")) {
                cooldownManager.setCooldown(player, "firework", 3, new ItemStack(Material.FIREWORK_ROCKET));
            }
            if (!cooldownManager.hasCooldown(player.getUniqueId(), "elytra")) {
                cooldownManager.setCooldown(player, "elytra", 3, new ItemStack(Material.ELYTRA));
            }
            if (!cooldownManager.hasCooldown(player.getUniqueId(), "diax_chest")) {
                cooldownManager.setCooldown(player, "diax_chest", 3, new ItemStack(Material.DIAMOND_CHESTPLATE));
            }
            if (!cooldownManager.hasCooldown(player.getUniqueId(), "nethe_chest")) {
                cooldownManager.setCooldown(player, "nethe_chest", 3, new ItemStack(Material.NETHERITE_CHESTPLATE));
            }
            return;
        }

        if (chestplate == null && player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.ELYTRA) {
            ItemStack currentElytra = player.getInventory().getChestplate().clone();

            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), currentElytra);
            } else {
                player.getInventory().addItem(currentElytra);
            }

            player.getInventory().setChestplate(null);
            player.setGliding(false);

            if (!cooldownManager.hasCooldown(player.getUniqueId(), "elytra")) {
                cooldownManager.setCooldown(player, "elytra", 3, new ItemStack(Material.ELYTRA));
            }
        }
        player.sendMessage(TextUtil.format(plugin.getConfig().getString("messages.gliding")));
    }
}
