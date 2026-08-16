package pl.niker.regions;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import dev.rollczi.litecommands.*;
import dev.rollczi.litecommands.bukkit.*;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.java.*;
import pl.niker.regions.commands.arguments.*;
import pl.niker.regions.commands.*;
import pl.niker.regions.managers.*;
import pl.niker.regions.commands.handler.*;
import pl.niker.regions.listeners.impl.*;
import pl.niker.regions.model.Region;

public class Main extends JavaPlugin {
    private LiteCommands<CommandSender> liteCommands;
    private static Main instance;

    private RegionManager regionManager;
    private SelectorManager selectorManager;
    private CooldownManager cooldownManager;
    private ElytraManager elytraManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        PacketEvents.getAPI().init();

        regionManager = new RegionManager(this);
        Bukkit.getScheduler().runTaskLater(this, () -> regionManager.loadRegions(), 10);

        selectorManager = new SelectorManager(this);
        cooldownManager = new CooldownManager(this);
        elytraManager = new ElytraManager(this, cooldownManager);

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(regionManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(regionManager, selectorManager), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(selectorManager), this);
        getServer().getPluginManager().registerEvents(new RegionJoinListener(regionManager, elytraManager), this);

        PlayerListener playerListener = new PlayerListener(this, regionManager, elytraManager, cooldownManager);

        getServer().getPluginManager().registerEvents(playerListener, this);
        PacketEvents.getAPI().getEventManager().registerListener(playerListener, PacketListenerPriority.HIGHEST);

        liteCommands = LiteBukkitFactory.builder(this)
                .commands(new RegionCommand(this, regionManager, selectorManager))
                .argument(Region.class, new RegionArgument(regionManager))
                .missingPermission(new MissingPermissionHandler())
                .invalidUsage(new InvalidUsageHandler())
                .build();
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public SelectorManager getSelectorManager() {
        return selectorManager;
    }

    public static Main getInstance() {
        return instance;
    }
}