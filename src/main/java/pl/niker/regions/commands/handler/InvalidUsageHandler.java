package pl.niker.regions.commands.handler;

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.niker.regions.util.TextUtil;

public class InvalidUsageHandler
        implements dev.rollczi.litecommands.invalidusage.InvalidUsageHandler<CommandSender> {
    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        CommandSender sender = invocation.sender();
        Schematic schematic = result.getSchematic();
        if (schematic.isOnlyFirst()) {
            sender.sendMessage(TextUtil.format("&x&F&F&0&0&0&0☹ &cPoprawne u\u017cycie: &x&F&F&0&0&0&0" + schematic.first()));
            return;
        }
        sender.sendMessage(TextUtil.format("&x&F&F&0&0&0&0☹ &cPoprawne u\u017cycie:"));
        for (String scheme : schematic.all()) {
            sender.sendMessage(TextUtil.format("&8 - &x&F&F&0&0&0&0" + scheme));
        }
        if (sender instanceof Player p) {
            p.playSound(p, Sound.AMBIENT_BASALT_DELTAS_ADDITIONS, 1.0f, 1.0f);
        }
    }
}