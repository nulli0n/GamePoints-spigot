package su.nightexpress.gamepoints.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.Perms;
import su.nightexpress.gamepoints.api.store.IPointProduct;
import su.nightexpress.gamepoints.api.store.IPointStore;
import su.nightexpress.gamepoints.data.PointUser;
import su.nightexpress.gamepoints.lang.Lang;

import java.util.List;

public class RemovePurchaseCommand extends AbstractCommand<GamePoints> {

    public RemovePurchaseCommand(@NotNull GamePoints plugin) {
        super(plugin, new String[]{"removepurchase"}, Perms.COMMAND_REMOVEPURCHASE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.COMMAND_REMOVE_PURCHASE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_REMOVE_PURCHASE_DESC).getLocalized();
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
            return plugin.getStoreManager().getStoreIds();
        }
        if (i == 3) {
            IPointStore store = plugin.getStoreManager().getStore(args[2]);
            if (store != null) return store.getProducts().stream().map(IPointProduct::getId).toList();
        }
        return super.getTab(player, i, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 4) {
            this.printUsage(sender);
            return;
        }

        PointUser user = plugin.getUserManager().getUserData(args[1]);
        if (user == null) {
            this.errorPlayer(sender);
            return;
        }

        String storeId = args[2];
        String productId = args[3];
        user.getPurchases(storeId).remove(productId);
        plugin.getMessage(Lang.COMMAND_REMOVE_PURCHASE_DONE_USER).send(sender);
    }
}
