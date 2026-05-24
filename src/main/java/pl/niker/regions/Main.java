package pl.niker.regions;

import dev.rollczi.litecommands.*;
import dev.rollczi.litecommands.bukkit.*;
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
    public void onEnable() {
        saveDefaultConfig();

        regionManager = new RegionManager(this);
        regionManager.loadRegions();
        selectorManager = new SelectorManager(this);
        cooldownManager = new CooldownManager(this);
        elytraManager = new ElytraManager(this, cooldownManager);

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(regionManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(regionManager), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(selectorManager), this);
        getServer().getPluginManager().registerEvents(new RegionJoinListener(regionManager, elytraManager), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this, regionManager, elytraManager, cooldownManager), this);

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