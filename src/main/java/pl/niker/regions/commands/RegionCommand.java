package pl.niker.regions.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.niker.regions.managers.RegionManager;
import pl.niker.regions.managers.SelectorManager;
import pl.niker.regions.model.Region;
import pl.niker.regions.types.ActionType;
import pl.niker.regions.types.RegionFlagType;
import pl.niker.regions.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Command(name = "region", aliases = "rg")
@Permission(value = "regions.command.*")
public class RegionCommand {
    private final JavaPlugin plugin;
    private final RegionManager regionManager;
    private final SelectorManager selectorManager;

    public RegionCommand(JavaPlugin plugin, RegionManager regionManager, SelectorManager selectorManager) {
        this.plugin = plugin;
        this.regionManager = regionManager;
        this.selectorManager = selectorManager;
    }

    @Execute(name = "create")
    @Permission(value = "regions.command.create")
    void createRegion(@Context Player player, @Arg String name, @Arg int priority) {
        Location left = selectorManager.getSelectedLocation(player, ActionType.LEFT);
        Location right = selectorManager.getSelectedLocation(player, ActionType.RIGHT);

        if (left == null || right == null) {
            player.sendMessage(TextUtil.format("&cMusisz zaznaczyć &4pierwszą &ci &4drugą &cpozycję!"));
            player.playSound(player, Sound.BLOCK_ANVIL_LAND, 1, 1);
            return;
        }

        regionManager.createRegion(name, left, right, priority);
        player.sendMessage(TextUtil.format("&aPomyślnie stworzono region &2" + name + "&a!"));
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }

    @Execute(name = "selector")
    @Permission(value = "regions.command.selector")
    void selector(@Context Player player) {
        player.getInventory().addItem(selectorManager.getSelectorItem());
        player.sendMessage(TextUtil.format("&aNadano selector!"));
    }

    @Execute(name = "remove")
    @Permission(value = "regions.command.remove")
    void removeRegion(@Context CommandSender player, @Arg Region region) {
        regionManager.removeRegion(region);

        player.sendMessage(TextUtil.format("&cPomyślnie usunięto region &4" + region.getName() + "&c!"));
        if (player instanceof Player p) {
            p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
    }

    @Execute(name = "flag")
    @Permission(value = "regions.command.flag")
    void flag(@Context CommandSender sender, @Arg Region region, @Arg @Async RegionFlagType type, @Arg boolean enabled) {
        if (enabled) regionManager.addFlag(region.getName(), type);
        else regionManager.removeFlag(region.getName(), type);
        sender.sendMessage(TextUtil.format("&aPomyślnie ustawiono flagę &2" + type.name().toUpperCase() + " &ana " + (enabled ? "&2Włączoną" : "&4Wyłączoną") + "&a!"));
    }

    @Execute(name = "show-flags")
    @Permission(value = "regions.command.show-flags")
    void showFlags(@Context CommandSender sender, @OptionalArg Region region) {
        if (sender instanceof Player p) {
            UUID uuid = p.getUniqueId();
            if (region == null) {
                region = regionManager.getRegion(uuid);
            }

            if (region == null) {
                p.sendMessage(TextUtil.format("&cNie jesteś w żadnym regionie!"));
                return;
            }

            List<String> formattedFlags = new ArrayList<>();
            for (RegionFlagType flagType : regionManager.getAllFlagTypes()) {
                boolean enabled = region.getFlags().contains(flagType);
                String color = enabled ? "&a" : "&c";
                formattedFlags.add(color + flagType.name());
            }

            p.sendMessage(TextUtil.format("&7Flagi dla &e" + region.getName() + "&8:\n " + String.join("&8, ", formattedFlags)));
        }
    }

    @Execute(name = "reload")
    @Permission(value = "regions.command.reload-configuration")
    void reload(@Context CommandSender sender) {
        long start = System.currentTimeMillis();

        try {
            plugin.reloadConfig();
            sender.sendMessage(TextUtil.format("&aPomyślnie przeładowano konfigurację pluginu! &8(&a" + (System.currentTimeMillis() - start) + "ms&8)"));
        } catch (Exception e) {
            sender.sendMessage(TextUtil.format("&cNie udało się przeładować konfiguracji pluginu! " + e.getMessage()));
        }
    }
}
