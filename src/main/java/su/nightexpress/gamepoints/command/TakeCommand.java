package su.nightexpress.gamepoints.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.Perms;
import su.nightexpress.gamepoints.config.Config;
import su.nightexpress.gamepoints.data.PointUser;

import java.util.Arrays;
import java.util.List;

public class TakeCommand extends AbstractCommand<GamePoints> {

    public TakeCommand(@NotNull GamePoints plugin) {
        super(plugin, new String[]{"take"}, Perms.COMMAND_TAKE);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Take_Desc.getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Take_Usage.getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return PlayerUtil.getPlayerNames();
        }
        if (arg == 2) {
            return Arrays.asList("<amount>", "1", "10", "50", "100");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 3) {
            this.printUsage(sender);
            return;
        }

        String userName = args[1];
        int amount = StringUtil.getInteger(args[2], 0);
        if (amount <= 0) {
            plugin.lang().Error_Number_Invalid.replace("%num%", args[2]).send(sender);
            return;
        }

        PointUser user = plugin.getUserManager().getOrLoadUser(userName, false);
        if (user == null) {
            this.errorPlayer(sender);
            return;
        }

        user.takePoints(amount);

        plugin.lang().Command_Take_Done_Sender
            .replace(Config.replacePlaceholders())
            .replace("%amount%", amount)
            .replace(user.replacePlaceholders())
            .send(sender);

        Player player = plugin.getServer().getPlayer(user.getName());
        if (player != null) {
            plugin.lang().Command_Take_Done_User
                .replace(Config.replacePlaceholders())
                .replace("%amount%", amount).send(player);
        }
    }
}
