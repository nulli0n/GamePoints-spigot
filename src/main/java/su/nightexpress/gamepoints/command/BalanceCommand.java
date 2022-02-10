package su.nightexpress.gamepoints.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.Perms;
import su.nightexpress.gamepoints.config.Config;
import su.nightexpress.gamepoints.data.PointUser;

import java.util.List;

public class BalanceCommand extends AbstractCommand<GamePoints> {

    public BalanceCommand(@NotNull GamePoints plugin) {
        super(plugin, new String[]{"balance", "bal"}, Perms.COMMAND_BALANCE);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Balance_Desc.getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Balance_Usage.getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<@NotNull String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1 && player.hasPermission(Perms.COMMAND_BALANCE_OTHERS)) {
            return PlayerUtil.getPlayerNames();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if ((args.length < 2 && !(sender instanceof Player)) || args.length > 2) {
            this.printUsage(sender);
            return;
        }

        if (args.length >= 2 && !sender.hasPermission(Perms.COMMAND_BALANCE_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        String userName = args.length == 2 ? args[1] : sender.getName();
        PointUser user = plugin.getUserManager().getOrLoadUser(userName, false);
        if (user == null) {
            this.errorPlayer(sender);
            return;
        }

        plugin.lang().Command_Balance_Done
            .replace(Config.replacePlaceholders())
            .replace(user.replacePlaceholders())
            .send(sender);
    }
}