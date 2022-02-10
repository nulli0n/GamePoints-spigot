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

public class SetCommand extends AbstractCommand<GamePoints> {

    public SetCommand(@NotNull GamePoints plugin) {
        super(plugin, new String[]{"set"}, Perms.COMMAND_SET);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Set_Desc.getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Set_Usage.getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
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
        PointUser user = plugin.getUserManager().getOrLoadUser(userName, false);
        if (user == null) {
            this.errorPlayer(sender);
            return;
        }

        int amount = StringUtil.getInteger(args[2], user.getBalance());

        user.setBalance(amount);

        plugin.lang().Command_Set_Done_Sender.replace(Config.replacePlaceholders())
            .replace("%amount%", amount)
            .replace(user.replacePlaceholders())
            .send(sender);

        Player player = user.getPlayer();
        if (player != null) {
            plugin.lang().Command_Set_Done_User
                .replace(Config.replacePlaceholders())
                .replace("%amount%", amount).
                send(player);
        }
    }
}