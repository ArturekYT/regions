package pl.niker.regions.commands.arguments;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import pl.niker.regions.managers.RegionManager;
import pl.niker.regions.model.Region;
import pl.niker.regions.util.TextUtil;
import java.util.stream.Collectors;

public class RegionArgument extends ArgumentResolver<CommandSender, Region> {
    private final RegionManager regionManager;

    public RegionArgument(RegionManager regionManager) {
        this.regionManager = regionManager;
    }

    @Override
    protected ParseResult<Region> parse(Invocation<CommandSender> invocation, Argument<Region> argument, String input) {
        Region region = regionManager.getRegions().get(input);
        if (region == null) {
            return ParseResult.failure(TextUtil.format("&cRegion &x&F&F&0&0&0&0(" + input + ") &cnie został odnaleziony!"));
        }
        return ParseResult.success(region);
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Region> argument, SuggestionContext context) {
        return SuggestionResult.from(regionManager.getRegions().keySet().stream()
                .map(Suggestion::of)
                .collect(Collectors.toList()));
    }
}