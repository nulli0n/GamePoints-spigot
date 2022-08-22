package su.nightexpress.gamepoints.store;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.*;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.api.store.IPointProduct;
import su.nightexpress.gamepoints.api.store.IPointStore;
import su.nightexpress.gamepoints.config.Config;
import su.nightexpress.gamepoints.data.PointUser;

import java.util.ArrayList;
import java.util.List;

public class StoreView extends AbstractMenu<GamePoints> {

    private final IPointStore store;

    public StoreView(@NotNull IPointStore store, @NotNull String path) {
        super(store.plugin(), store.getConfig(), path);
        this.store = store;

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    plugin.getStoreManager().getStoreMainMenu().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
        };

        for (String sId : cfg.getSection(path + "Content")) {
            IMenuItem menuItem = cfg.getMenuItem(path + "Content." + sId, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClick(click);
            }
            this.addItem(menuItem);
        }
    }

    @Override
    public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
        this.setPage(player, this.getPage(player), this.store.getPages());

        PointUser user = plugin.getUserManager().getUserData(player);
        for (IPointProduct product : this.store.getProducts()) {
            if (product.getStorePage() != this.getPage(player)) continue;

            ItemStack item = product.getPreview();
            this.replaceProduct(item, product, user);

            IMenuItem menuItem = new MenuItem(item, product.getStoreSlot());
            menuItem.setClick((player1, type, e) -> {
                ItemStack clicked = e.getCurrentItem();
                if (clicked == null) return;

                if (!user.canPurchase(player, product, true)) return;
                plugin.getStoreManager().getStoreConfirmation().open(player1, product, clicked);
            });

            this.addItem(player, menuItem);
        }
    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

    private void replaceProduct(@NotNull ItemStack item, @NotNull IPointProduct product, @NotNull PointUser user) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        int priceInherited = user.getInheritedPriceForItem(product);

        String userProdPrice = String.valueOf(priceInherited);
        String userProdCooldown = TimeUtil.formatTimeLeft(user.getProductCooldown(product));

        List<String> formatProduct = new ArrayList<>(Config.STORE_PRODUCT_DISPLAY_FORMAT);
        List<String> formatState;
        if (user.isProductOnCooldown(product)) {
            if (product.isOneTimedPurchase()) formatState = new ArrayList<>(Config.STORE_PRODUCT_PURCHASE_FORMAT_ALREADY_HAVE);
            else formatState = new ArrayList<>(Config.STORE_PRODUCT_PURCHASE_FORMAT_COOLDOWN);
        }
        else {
            if (priceInherited == 0) formatState = new ArrayList<>(Config.STORE_PRODUCT_PURCHASE_FORMAT_ALREADY_HAVE);
            else formatState = new ArrayList<>(Config.STORE_PRODUCT_PURCHASE_FORMAT_AVAILABLE);
        }

        formatProduct = StringUtil.replace(formatProduct, IPointProduct.PLACEHOLDER_DESCRIPTION, false, product.getDescription());
        formatProduct = StringUtil.replace(formatProduct, "%purchase%", false, formatState);
        formatProduct.replaceAll(str -> str
            .replace(IPointProduct.PLACEHOLDER_COOLDOWN, userProdCooldown)
            .replace(IPointProduct.PLACEHOLDER_PRICE_INHERITED, userProdPrice)
        );
        formatProduct.replaceAll(product.replacePlaceholders());
        formatProduct.replaceAll(Config.replacePlaceholders());
        formatProduct.replaceAll(user.replacePlaceholders());

        meta.setDisplayName(product.getName());
        meta.setLore(formatProduct);
        item.setItemMeta(meta);
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
