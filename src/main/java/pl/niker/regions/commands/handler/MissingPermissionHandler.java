package pl.niker.regions.commands.handler;

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.niker.regions.util.TextUtil;

public class MissingPermissionHandler implements MissingPermissionsHandler<CommandSender> {
    public void handle(Invocation<CommandSender> invocation, MissingPermissions missingPermissions, ResultHandlerChain<CommandSender> chain) {
        String permissions = missingPermissions.asJoinedText();
        CommandSender sender = invocation.sender();
        sender.sendMessage(TextUtil.format("&x&F&F&0&0&0&0☹ &cNie posiadasz wymaganej permisji do wykonania tej komendy &x&F&F&0&0&0&0(" + permissions + ")"));

        if (sender instanceof Player player) {
            player.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        }
    }
}
