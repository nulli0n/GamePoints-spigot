package su.nightexpress.gamepoints.store.object;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractLoadableItem;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.Perms;
import su.nightexpress.gamepoints.api.store.IPointDiscount;
import su.nightexpress.gamepoints.api.store.IPointProduct;
import su.nightexpress.gamepoints.api.store.IPointStore;
import su.nightexpress.gamepoints.store.StoreView;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PointStore extends AbstractLoadableItem<GamePoints> implements IPointStore {

    private String  name;
    private boolean isPermissionRequired;
    private       int                 pages;
    private final Set<IPointDiscount> discounts;

    private final Map<String, IPointProduct> productMap;

    private StoreView view;

    public PointStore(@NotNull GamePoints plugin, @NotNull JYML cfg) {
        super(plugin, cfg);

        cfg.addMissing("Pages", 1);
        cfg.saveChanges();

        this.setName(cfg.getString("Name", this.getId()));
        this.setPermissionRequired(cfg.getBoolean("Permission_Required"));
        this.setPages(cfg.getInt("Pages"));
        this.discounts = new HashSet<>();
        for (String discountId : cfg.getSection("Discounts")) {
            String path = "Discounts." + discountId + ".";

            int dAmount = cfg.getInt(path + "Amount");
            Map<DayOfWeek, Set<LocalTime[]>> dTimes = new HashMap<>();
            for (String dDayNames : cfg.getSection(path + "Times")) {
                Set<LocalTime[]> dDayTimes = new HashSet<>();
                for (String dDayTimeRaw : cfg.getStringSet(path + "Times." + dDayNames)) {
                    String[] split = dDayTimeRaw.split("-");
                    if (split.length < 2) {
                        this.plugin.error("Discount time range is invalid for '" + dDayNames + "' days of '" + discountId + "' discount in '" + getId() + "' store!");
                        continue;
                    }

                    LocalTime timeStart = LocalTime.parse(split[0], DateTimeFormatter.ofPattern("HH:mm"));
                    LocalTime timeEnd = LocalTime.parse(split[1], DateTimeFormatter.ofPattern("HH:mm"));
                    dDayTimes.add(new LocalTime[]{timeStart, timeEnd});
                }

                for (String dDayName : dDayNames.split(",")) {
                    DayOfWeek dDay = CollectionsUtil.getEnum(dDayName, DayOfWeek.class);
                    if (dDay == null) {
                        this.plugin.error("Invalid day '" + dDayName + "' in '" + discountId + "' discount in '" + getId() + "' store!");
                        continue;
                    }
                    dTimes.put(dDay, dDayTimes);
                }
            }
            IPointDiscount discount = new PointDiscount(dAmount, dTimes);
            this.discounts.add(discount);
        }

        JYML cfgProducts = new JYML(cfg.getFile().getParentFile().getAbsolutePath(), "products.yml");
        this.productMap = new HashMap<>();
        for (String sId : cfgProducts.getSection("")) {
            String path = sId + ".";

            cfgProducts.addMissing(path + "Store.Page", 1);
            cfgProducts.saveChanges();

            String pName = cfgProducts.getString(path + "Name", sId);
            List<String> description = cfgProducts.getStringList(path + "Description");

            int pCost = cfgProducts.getInt(path + "Price");
            long pPurchaseCooldown = cfgProducts.getLong(path + "Purchase_Cooldown");
            int pPriority = cfgProducts.getInt(path + "Priority");
            Set<String> pInherRewards = cfgProducts.getStringSet(path + "Inheritance.Rewards");
            Set<String> pInherPrice = cfgProducts.getStringSet(path + "Inheritance.Price");
            ItemStack pPreview = cfgProducts.getItem(path + "Preview");
            if (pPreview.getType().isAir()) {
                plugin.error("Invalid product preview for '" + sId + "' in '" + getId() + "' store!");
                continue;
            }
            List<String> pCommands = cfgProducts.getStringList(path + "Rewards.Commands");
            int pStorePage = cfgProducts.getInt(path + "Store.Page");
            int pStoreSlot = cfgProducts.getInt(path + "Store.Slot");

            IPointProduct product = new PointProduct(this, sId, pName, description, pCost, pPurchaseCooldown,
                    pPriority, pInherRewards, pInherPrice, pPreview, pCommands, pStorePage, pStoreSlot);
            this.productMap.put(product.getId(), product);
        }

        this.getProducts().stream().filter(IPointProduct::isOneTimedPurchase).forEach(productParent -> {
            this.getProducts().stream().filter(IPointProduct::isOneTimedPurchase).forEach(productChild -> {
                if (productChild.getInheritedPrice().contains(productParent.getId()) || productChild.equals(productParent)) {
                    productParent.getInheritedPrice().add(productChild.getId());
                    //System.out.println("Added inherited price for parent: '" + productParent.getId() + "' children: '" + productChild.getId() + "'");
                }
            });
        });

        this.view = new StoreView(this, "View.");
    }

    @Override
    public void clear() {
        if (this.view != null) {
            this.view.clear();
            this.view = null;
        }
    }

    @Override
    public void onSave() {
        // TODO
    }

    @Override
    public boolean hasPermission(@NotNull Player player) {
        return !this.isPermissionRequired() || player.hasPermission(Perms.STORE + this.getId());
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = StringUtil.color(name);
    }

    @Override
    public boolean isPermissionRequired() {
        return isPermissionRequired;
    }

    @Override
    public void setPermissionRequired(boolean permissionRequired) {
        this.isPermissionRequired = permissionRequired;
    }

    @Override
    public int getPages() {
        return pages;
    }

    @Override
    public void setPages(int pages) {
        this.pages = Math.max(1, pages);
    }

    @NotNull
    @Override
    public Set<IPointDiscount> getDiscounts() {
        return discounts;
    }

    @Nullable
    @Override
    public IPointDiscount getDiscount() {
        return this.getDiscounts().stream().filter(IPointDiscount::isAvailable)
            .max(Comparator.comparingInt(IPointDiscount::getAmount)).orElse(null);
    }

    @Override
    @NotNull
    public Map<String, IPointProduct> getProductsMap() {
        return this.productMap;
    }

    @NotNull
    @Override
    public StoreView getView() {
        return view;
    }
}
