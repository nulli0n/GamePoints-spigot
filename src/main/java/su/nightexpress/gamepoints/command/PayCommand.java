package su.nightexpress.gamepoints.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.lang.EngineLang;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.Perms;
import su.nightexpress.gamepoints.config.Config;
import su.nightexpress.gamepoints.data.PointUser;
import su.nightexpress.gamepoints.lang.Lang;

import java.util.Arrays;
import java.util.List;

public class PayCommand extends AbstractCommand<GamePoints> {

    public PayCommand(@NotNull GamePoints plugin) {
        super(plugin, new String[]{"pay"}, Perms.COMMAND_PAY);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_PAY_DESC).getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.COMMAND_PAY_USAGE).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }


    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return PlayerUtil.getPlayerNames();
        }
        if (i == 2) {
            return Arrays.asList("<amount>", "1", "10", "50", "100");
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 3) {
            this.printUsage(sender);
            return;
        }

        String userName = args[1];
        if (userName.equalsIgnoreCase(sender.getName())) {
            plugin.getMessage(EngineLang.ERROR_COMMAND_SELF).send(sender);
            return;
        }

        PointUser userTarget = plugin.getUserManager().getOrLoadUser(userName, false);
        if (userTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        Player from = (Player) sender;
        PointUser userFrom = plugin.getUserManager().getOrLoadUser(from);
        int amount = StringUtil.getInteger(args[2], 0);
        if (amount <= 0) {
            plugin.getMessage(EngineLang.ERROR_NUMBER_INVALID).replace("%num%", args[2]).send(sender);
            return;
        }
        if (amount > userFrom.getBalance()) {
            plugin.getMessage(Lang.COMMAND_PAY_ERROR_NO_MONEY).send(from);
            return;
        }

        userTarget.addPoints(amount);
        userFrom.takePoints(amount);

        plugin.getMessage(Lang.COMMAND_PAY_DONE_SENDER)
            .replace(Config.replacePlaceholders())
            .replace("%amount%", amount)
            .replace("%player%", userTarget.getName())
            .send(sender);

        Player pTarget = plugin.getServer().getPlayer(userTarget.getName());
        if (pTarget != null) {
            plugin.getMessage(Lang.COMMAND_PAY_DONE_USER)
                .replace(Config.replacePlaceholders())
                .replace("%amount%", amount)
                .replace("%player%", userFrom.getName())
                .send(pTarget);
        }
    }
}
