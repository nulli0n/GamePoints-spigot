package su.nightexpress.gamepoints.store.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.api.store.IPointDiscount;
import su.nightexpress.gamepoints.api.store.IPointStore;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StoreMenuMain extends AbstractMenu<GamePoints> {

    private final List<String> formatDiscount;

    public StoreMenuMain(@NotNull GamePoints plugin) {
        super(plugin, JYML.loadOrExtract(plugin, "store.main.yml"), "");

        cfg.addMissing("Format.Discount", Arrays.asList("&7", "&e&l&n%discount_amount%% Discount!"));
        cfg.saveChanges();

        this.formatDiscount = StringUtil.color(cfg.getStringList("Format.Discount"));

        for (String sId : cfg.getSection("Stores")) {
            IPointStore store = plugin.getStoreManager().getStore(sId);
            if (store == null) {
                plugin.error("Invalid store '" + sId + "' in main store!");
                continue;
            }

            MenuItem menuItem = cfg.getMenuItem("Stores." + sId);
            menuItem.setClickHandler((p, type, e) -> store.open(p));

            this.addItem(menuItem);
        }

        for (String id : cfg.getSection("Content")) {
            MenuItem guiItem = cfg.getMenuItem("Content." + id, MenuItemType.class);

            this.addItem(guiItem);
        }
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);

        IPointStore store = plugin.getStoreManager().getStore(menuItem.getId());
        if (store == null) return;

        IPointDiscount discount = store.getDiscount();

        ItemUtil.replaceLore(item, "%discount%", discount == null ? Collections.emptyList() : this.formatDiscount);
        ItemUtil.replace(item, store.replacePlaceholders());
        if (discount != null) ItemUtil.replace(item, discount.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
