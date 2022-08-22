package su.nightexpress.gamepoints.store.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.api.store.IPointProduct;
import su.nightexpress.gamepoints.config.Config;
import su.nightexpress.gamepoints.data.PointUser;

import java.util.Map;
import java.util.WeakHashMap;

public class StoreMenuConfirmation extends AbstractMenu<GamePoints> {

    private final int                        productSlot;
    private final Map<Player, IPointProduct> productMap;

    public StoreMenuConfirmation(@NotNull GamePoints plugin) {
        super(plugin, JYML.loadOrExtract(plugin, "store.confirm.yml"), "");
        this.productSlot = cfg.getInt("Product_Slot");
        this.productMap = new WeakHashMap<>();

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                IPointProduct product = this.productMap.remove(player);
                if (product == null) {
                    player.closeInventory();
                    return;
                }

                if (type2 == MenuItemType.CONFIRMATION_ACCEPT) {
                    PointUser user = plugin.getUserManager().getUserData(player);
                    user.purchaseProduct(player, product);
                    product.getStore().open(player);
                }
                else if (type2 == MenuItemType.CONFIRMATION_DECLINE) {
                    product.getStore().open(player);
                }
            }
        };

        for (String sId : cfg.getSection("Content")) {
            IMenuItem menuItem = cfg.getMenuItem("Content." + sId, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClick(click);
            }
            this.addItem(menuItem);
        }
    }

    public void open(@NotNull Player player, @NotNull IPointProduct product, @NotNull ItemStack item) {
        this.productMap.put(player, product);
        this.addItem(player, item, this.productSlot);
        this.open(player, 1);
    }

    @Override
    public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);

        PointUser user = plugin.getUserManager().getUserData(player);
        IPointProduct product = this.productMap.get(player);
        if (product == null) return;

        ItemUtil.replace(item, str -> product.replacePlaceholders().apply(str
                .replace(IPointProduct.PLACEHOLDER_PRICE_INHERITED, String.valueOf(user.getInheritedPriceForItem(product)))
        ));
        ItemUtil.replace(item, Config.replacePlaceholders());
    }

    @Override
    public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
        super.onClose(player, e);
        this.productMap.remove(player);
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
