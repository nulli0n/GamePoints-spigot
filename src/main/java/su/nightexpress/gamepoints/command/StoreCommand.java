package su.nightexpress.gamepoints.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.Perms;
import su.nightexpress.gamepoints.api.store.IPointStore;

import java.util.List;

public class StoreCommand extends AbstractCommand<GamePoints> {

    public StoreCommand(@NotNull GamePoints plugin) {
        super(plugin, new String[]{"store"}, Perms.COMMAND_STORE);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Store_Desc.getLocalized();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Store_Usage.getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return plugin.getStoreManager().getStoreIds();
        }
        if (arg == 2) {
            return PlayerUtil.getPlayerNames();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if ((args.length < 3 && !(sender instanceof Player)) || args.length > 3) {
            this.printUsage(sender);
            return;
        }

        if (args.length < 2) {
            Player player = (Player) sender;
            plugin.getStoreManager().getStoreMainMenu().open(player, 1);
            return;
        }

        String storeName = args[1];
        IPointStore store = plugin.getStoreManager().getStore(storeName);
        if (store == null) {
            plugin.lang().Store_Error_Invalid.send(sender);
            return;
        }

        if (args.length >= 3 && !sender.hasPermission(Perms.ADMIN)) {
            this.errorPermission(sender);
            return;
        }

        String userName = args.length == 3 ? args[2] : sender.getName();
        Player player = plugin.getServer().getPlayer(userName);
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        store.open(player);
    }
}
