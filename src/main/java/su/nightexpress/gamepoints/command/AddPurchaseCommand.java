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

public class AddPurchaseCommand extends AbstractCommand<GamePoints> {

    public AddPurchaseCommand(@NotNull GamePoints plugin) {
        super(plugin, new String[]{"addpurchase"}, Perms.COMMAND_ADDPURCHASE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.COMMAND_ADD_PURCHASE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.COMMAND_ADD_PURCHASE_DESC).getLocalized();
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

        PointUser user = plugin.getUserManager().getOrLoadUser(args[1], false);
        if (user == null) {
            this.errorPlayer(sender);
            return;
        }

        String storeId = args[2];
        IPointStore store = this.plugin.getStoreManager().getStore(storeId);
        if (store == null) {
            plugin.getMessage(Lang.STORE_ERROR_INVALID).send(sender);
            return;
        }

        String productId = args[3];
        IPointProduct product = store.getProduct(productId);
        if (product == null) {
            plugin.getMessage(Lang.STORE_ERROR_PRODUCT_INVALID).send(sender);
            return;
        }

        if (product.getPurchaseCooldown() == 0L) {
            plugin.getMessage(Lang.COMMAND_ADD_PURCHASE_ERROR_NO_COOLDOWN).send(sender);
            return;
        }

        long cooldown = product.getPurchaseNextTime();
        user.getPurchases(store).put(product.getId(), cooldown);
        plugin.getMessage(Lang.COMMAND_ADD_PURCHASE_DONE_USER).send(sender);
    }
}
