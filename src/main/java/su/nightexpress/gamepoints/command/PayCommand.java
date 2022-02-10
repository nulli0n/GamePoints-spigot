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

public class PayCommand extends AbstractCommand<GamePoints> {

    public PayCommand(@NotNull GamePoints plugin) {
        super(plugin, new String[]{"pay"}, Perms.COMMAND_PAY);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Pay_Desc.getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Pay_Usage.getLocalized();
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
            plugin.lang().Error_Command_Self.send(sender);
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
            plugin.lang().Error_Number_Invalid.replace("%num%", args[2]).send(sender);
            return;
        }
        if (amount > userFrom.getBalance()) {
            plugin.lang().Command_Pay_Error_NoMoney.send(from);
            return;
        }

        userTarget.addPoints(amount);
        userFrom.takePoints(amount);

        plugin.lang().Command_Pay_Done_Sender
            .replace(Config.replacePlaceholders())
            .replace("%amount%", amount)
            .replace("%player%", userTarget.getName())
            .send(sender);

        Player pTarget = plugin.getServer().getPlayer(userTarget.getName());
        if (pTarget != null) {
            plugin.lang().Command_Pay_Done_User
                .replace(Config.replacePlaceholders())
                .replace("%amount%", amount)
                .replace("%player%", userFrom.getName())
                .send(pTarget);
        }
    }
}
