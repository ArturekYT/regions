package pl.niker.regions.listeners.impl;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import io.papermc.paper.event.player.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.*;
import pl.niker.regions.managers.CooldownManager;
import pl.niker.regions.managers.ElytraManager;
import pl.niker.regions.managers.RegionManager;
import pl.niker.regions.model.Region;
import pl.niker.regions.types.RegionFlagType;
import pl.niker.regions.util.TextUtil;

public class PlayerListener implements Listener, PacketListener {
    private final JavaPlugin plugin;
    private final RegionManager regionManager;
    private final ElytraManager elytraManager;
    private final CooldownManager cooldownManager;

    public PlayerListener(JavaPlugin plugin, RegionManager regionManager, ElytraManager elytraManager, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.regionManager = regionManager;
        this.elytraManager = elytraManager;
        this.cooldownManager = cooldownManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Region region = regionManager.getRegion(e.getBlock().getLocation());
        if (region == null) return;

        if (!region.hasFlag(RegionFlagType.BLOCK_BREAK) && !p.hasPermission("regions.bypass.block-break")) {
            p.sendMessage(TextUtil.format(plugin.getConfig().getString("messages.block-break")));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBuild(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Region region = regionManager.getRegion(e.getBlockPlaced().getLocation());
        if (region == null) return;

        if (!region.hasFlag(RegionFlagType.BLOCK_PLACE) && !p.hasPermission("regions.bypass.block-place")) {
            p.sendMessage(TextUtil.format(plugin.getConfig().getString("messages.block-place")));
            cooldownManager.setCooldownLong(p, "block-" + e.getBlockPlaced().getType().name().toLowerCase(), 3000L, new ItemStack(e.getBlockPlaced().getType()));
            e.setCancelled(true);
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        Player p = e.getPlayer();
        if (p == null) return;

        if (e.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity interact = new WrapperPlayClientInteractEntity(e);

            Player target = Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(found -> found.getUniqueId().equals(interact.getEntityId()))
                    .findFirst()
                    .orElse(null);
            if (target == null) return;

            {
                Region region = regionManager.getRegion(target.getLocation());
                if (region == null) return;

                if (region.hasFlag(RegionFlagType.INVINCIBLE) || !region.hasFlag(RegionFlagType.PVP)) {
                    e.setCancelled(true);
                    return;
                }

                Region killRegion = regionManager.getRegion(target.getLocation());
                if (killRegion == null) return;

                if (killRegion.hasFlag(RegionFlagType.INVINCIBLE) || !killRegion.hasFlag(RegionFlagType.PVP)) {
                    e.setCancelled(true);
                }
            }

            {
                Region region = regionManager.getRegion(p.getLocation());
                if (region == null) return;

                if (region.hasFlag(RegionFlagType.INVINCIBLE) || !region.hasFlag(RegionFlagType.PVP)) {
                    e.setCancelled(true);
                    return;
                }

                Region killRegion = regionManager.getRegion(target.getLocation());
                if (killRegion == null) return;

                if (killRegion.hasFlag(RegionFlagType.INVINCIBLE) || !killRegion.hasFlag(RegionFlagType.PVP)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player vic)) return;
        Region region = regionManager.getRegion(vic.getLocation());
        if (region == null) return;

        if (!(e.getDamager() instanceof Player) && e.getDamager().getType() != EntityType.PLAYER) {
            if (!region.hasFlag(RegionFlagType.MOB_DAMAGE)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        Region region = regionManager.getRegion(p.getLocation());
        if (region == null) return;

        if (region.hasFlag(RegionFlagType.INVINCIBLE)) {
            e.setCancelled(true);
            return;
        }

        if (!region.hasFlag(RegionFlagType.DAMAGE) && !p.hasPermission("regions.bypass.damage")) {
            p.sendMessage(TextUtil.format(plugin.getConfig().getString("messages.damage")));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void elytra(EntityToggleGlideEvent e) {
        if (!(e.getEntity() instanceof Player p) || !e.isGliding()) return;

        Region region = regionManager.getRegion(p.getLocation());
        if (region == null) return;

        if (!region.hasFlag(RegionFlagType.GLIDING) && !p.hasPermission("regions.bypass.gliding")) {
            p.setGliding(false);
            p.setFlying(false);
            e.setCancelled(true);
            elytraManager.changeElytra(p);
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block block = e.getClickedBlock();
        Action action = e.getAction();

        if (action == Action.PHYSICAL) {
            Region region = regionManager.getRegion(e.getPlayer().getLocation());
            if (region != null && !region.hasFlag(RegionFlagType.PHISIC) && !p.hasPermission("regions.bypass.physic")) {
                e.setCancelled(true);
                p.sendMessage(TextUtil.format(plugin.getConfig().getString("messages.physic")));
            }
            return;
        }

        if (block == null) {
            if (action == Action.RIGHT_CLICK_AIR && p.getInventory().getItemInMainHand().getType() == Material.ENDER_PEARL) {
                Region region = regionManager.getRegion(p.getLocation());
                if (region != null && !region.hasFlag(RegionFlagType.ENDERPEARL) && !p.hasPermission("regions.bypass.ender_pearl")) {
                    e.setCancelled(true);
                    p.sendMessage(TextUtil.format(plugin.getConfig().getString("messages.ender-pearl")));
                }
            }
            return;
        }

        Region region = regionManager.getRegion(block.getLocation());
        if (region == null) return;

        if (action == Action.RIGHT_CLICK_BLOCK) {
            String typeName = block.getType().name();
            if (typeName.contains("BED") && !region.hasFlag(RegionFlagType.SLEEP) && !p.hasPermission("regions.bypass.sleeping")) {
                e.setCancelled(true);
                p.sendMessage(TextUtil.format(plugin.getConfig().getString("messages.sleep")));
            } else if ((block.getType() == Material.CHEST || block.getType() == Material.ANVIL || typeName.contains("SHULKER")) && !region.hasFlag(RegionFlagType.USE) && !p.hasPermission("regions.bypass.use")) {
                p.sendMessage(TextUtil.format(plugin.getConfig().getString("messages.use")));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void zydy1940(EntityCombustEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        Region region = regionManager.getRegion(p.getLocation());
        if (region == null) return;

        if (region.hasFlag(RegionFlagType.INVINCIBLE)) {
            e.setCancelled(true);
            return;
        }

        if (!region.hasFlag(RegionFlagType.FIRE) && !p.hasPermission("regions.bypass.fire")) {
            p.sendMessage(TextUtil.format(plugin.getConfig().getString("messages.fire")));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        Region region = regionManager.getRegion(e.getTo());
        if (region == null) return;

        if (e.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT && !region.hasFlag(RegionFlagType.CHORUS_TELEPORT) && !p.hasPermission("regions.bypass.chorus_teleport")) {
            p.sendMessage(TextUtil.format(plugin.getConfig().getString("messages.chorus-teleport")));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        Region region = regionManager.getRegion(p.getLocation());
        if (region == null) return;

        if (!region.hasFlag(RegionFlagType.ITEM_PICKUP) && !p.hasPermission("regions.bypass.item_pickup")) {
            p.sendMessage(TextUtil.format(plugin.getConfig().getString("messages.item-pickup")));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        Region region = regionManager.getRegion(p.getLocation());
        if (region == null) return;

        if (!region.hasFlag(RegionFlagType.ITEM_DROP) && !p.hasPermission("regions.bypass.item_drop")) {
            p.sendMessage(TextUtil.format(plugin.getConfig().getString("messages.item-drop")));
            e.setCancelled(true);
        }
    }
}