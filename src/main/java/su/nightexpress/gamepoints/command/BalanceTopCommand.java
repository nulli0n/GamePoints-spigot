package su.nightexpress.gamepoints.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.Perms;
import su.nightexpress.gamepoints.config.Config;
import su.nightexpress.gamepoints.data.PointUser;
import su.nightexpress.gamepoints.lang.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BalanceTopCommand extends AbstractCommand<GamePoints> {

    public BalanceTopCommand(@NotNull GamePoints plugin) {
        super(plugin, new String[]{"top", "balancetop", "baltop"}, Perms.COMMAND_BALANCETOP);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_BALANCE_TOP_DESC).getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.COMMAND_BALANCE_TOP_USAGE).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return Arrays.asList("1", "2", "<page>");
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        int page = args.length >= 2 ? StringUtil.getInteger(args[1], 1) - 1 : 0;

        List<List<Map.Entry<String, Integer>>> total = CollectionsUtil.split(plugin.getStoreManager().getBalanceTop(), 10);
        int pages = total.size();

        if (page >= pages) page = pages - 1;
        if (page < 0) page = 0;

        List<Map.Entry<String, Integer>> list = pages > 0 ? total.get(page) : new ArrayList<>();
        int pos = 1 + 10 * page;

        for (String line : plugin.getMessage(Lang.COMMAND_BALANCE_TOP_LIST)
                .replace(Config.replacePlaceholders())
                .replace("%page_min%", (page + 1)).replace("%page_max%", pages)
                .asList()) {
            if (line.contains(PointUser.PLACEHOLDER_NAME)) {
                for (Map.Entry<String, Integer> entry : list) {
                    sender.sendMessage(line
                            .replace("%pos%", String.valueOf(pos++))
                            .replace(PointUser.PLACEHOLDER_BALANCE, String.valueOf(entry.getValue()))
                            .replace(PointUser.PLACEHOLDER_NAME, entry.getKey()));
                }
                continue;
            }
            sender.sendMessage(line);
        }
    }

}
