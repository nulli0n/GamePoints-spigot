package su.nightexpress.gamepoints.store.object;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.gamepoints.api.store.IPointDiscount;
import su.nightexpress.gamepoints.api.store.IPointProduct;
import su.nightexpress.gamepoints.api.store.IPointStore;

import java.util.List;
import java.util.Set;

public class PointProduct implements IPointProduct {

    private final IPointStore store;
    private final String      id;

    private String       name;
    private List<String> description;
    private int          page;
    private int          price;
    private long         purchaseCooldown;
    private int          priority;
    private Set<String>  inheritedRewards;
    private Set<String>  inheritedPrice;
    private ItemStack    preview;
    private List<String> rewardCommands;
    private int          storeSlot;

    public PointProduct(
            @NotNull IPointStore store,
            @NotNull String id,
            @NotNull String name,
            @NotNull List<String> description,
            int price,
            long purchaseCooldown,
            int priority,
            @NotNull Set<String> inheritRewards,
            @NotNull Set<String> inheritedPrice,
            @NotNull ItemStack preview,
            @NotNull List<String> rewardCommands,
            int storePage,
            int storeSlot
    ) {
        this.store = store;
        this.id = id.toLowerCase();
        this.setName(name);
        this.setDescription(description);
        this.setPrice(price);
        this.setPurchaseCooldown(purchaseCooldown);
        this.setPriority(priority);
        this.setInheritedRewards(inheritRewards);
        this.setInheritedPrice(inheritedPrice);
        this.setPreview(preview);
        this.setRewardCommands(rewardCommands);
        this.setStorePage(storePage);
        this.setStoreSlot(storeSlot);

        ItemUtil.replace(this.preview, this.replacePlaceholders());
    }

    @Override
    @NotNull
    public IPointStore getStore() {
        return this.store;
    }

    @Override
    @NotNull
    public String getId() {
        return this.id;
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

    @NotNull
    @Override
    public List<String> getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(@NotNull List<String> description) {
        this.description = StringUtil.color(description);
    }

    @Override
    public int getStorePage() {
        return page;
    }

    @Override
    public void setStorePage(int page) {
        this.page = Math.max(1, page);
    }

    @Override
    public int getPrice() {
        return this.price;
    }

    @Override
    public void setPrice(int price) {
        this.price = Math.max(0, price);
    }

    @Override
    public int getPriceFinal() {
        IPointDiscount discount = this.getStore().getDiscount();
        if (discount == null) return this.getPrice();

        return (int) Math.max(0D, (double) this.getPrice() * (1D - (double) discount.getAmount() / 100D));
    }

    @Override
    public long getPurchaseCooldown() {
        return purchaseCooldown;
    }

    @Override
    public void setPurchaseCooldown(long purchaseCooldown) {
        this.purchaseCooldown = purchaseCooldown >= 0 ? purchaseCooldown * 1000L : purchaseCooldown;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @NotNull
    @Override
    public Set<String> getInheritedRewards() {
        return inheritedRewards;
    }

    @Override
    public void setInheritedRewards(@NotNull Set<String> inheritedRewards) {
        this.inheritedRewards = inheritedRewards;
    }

    @NotNull
    @Override
    public Set<String> getInheritedPrice() {
        return inheritedPrice;
    }

    @Override
    public void setInheritedPrice(@NotNull Set<String> inheritedPrice) {
        this.inheritedPrice = inheritedPrice;
    }

    @Override
    @NotNull
    public ItemStack getPreview() {
        return new ItemStack(this.preview);
    }

    @Override
    public void setPreview(@NotNull ItemStack preview) {
        this.preview = new ItemStack(preview);
    }

    @Override
    @NotNull
    public List<String> getRewardCommands() {
        return rewardCommands;
    }

    @Override
    public void setRewardCommands(@NotNull List<String> rewardCommands) {
        this.rewardCommands = rewardCommands;
    }

    @Override
    public int getStoreSlot() {
        return this.storeSlot;
    }

    @Override
    public void setStoreSlot(int storeSlot) {
        this.storeSlot = storeSlot;
    }
}
