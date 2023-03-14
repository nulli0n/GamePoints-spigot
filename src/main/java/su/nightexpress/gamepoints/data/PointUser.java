package su.nightexpress.gamepoints.data;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUser;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.api.event.PointProductPurchaseEvent;
import su.nightexpress.gamepoints.api.event.PointUserChangeBalanceEvent;
import su.nightexpress.gamepoints.api.store.IPointProduct;
import su.nightexpress.gamepoints.api.store.IPointStore;
import su.nightexpress.gamepoints.config.Config;
import su.nightexpress.gamepoints.lang.Lang;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class PointUser extends AbstractUser<GamePoints> implements IPlaceholder {

    private       int                            balance;
    private final Map<String, Map<String, Long>> purchases;

    public static final String PLACEHOLDER_NAME    = "%user_name%";
    public static final String PLACEHOLDER_BALANCE = "%user_balance%";

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
                .replace(PLACEHOLDER_NAME, this.getName())
                .replace(PLACEHOLDER_BALANCE, String.valueOf(this.getBalance()))
                ;
    }

    public PointUser(@NotNull GamePoints plugin, @NotNull UUID uuid, @NotNull String name) {
        this(plugin, uuid, name, System.currentTimeMillis(), System.currentTimeMillis(),
                Config.GENERAL_START_BALANCE, new HashMap<>());
    }

    public PointUser(
            @NotNull GamePoints plugin,
            @NotNull UUID uuid,
            @NotNull String name,
            long dateCreated,
            long lastLogin,
            int balance,
            @NotNull Map<String, Map<String, Long>> purchases
    ) {
        super(plugin, uuid, name, dateCreated, lastLogin);
        this.setBalanceRaw(balance);
        this.purchases = purchases;
    }

    public int getBalance() {
        return this.balance;
    }

    public void addPoints(int amount) {
        this.setBalance(this.getBalance() + amount);
    }

    public void takePoints(int amount) {
        this.addPoints(-amount);
    }

    public void setBalance(int balance) {
        PointUserChangeBalanceEvent balanceEvent = new PointUserChangeBalanceEvent(this, this.getBalance(), balance);
        plugin.getPluginManager().callEvent(balanceEvent);
        if (balanceEvent.isCancelled()) return;

        this.setBalanceRaw(balance);
        this.saveData(this.plugin);
    }

    public void setBalanceRaw(int balance) {
        this.balance = Math.max(0, balance);
    }

    @NotNull
    public Map<String, Map<String, Long>> getPurchases() {
        return this.purchases;
    }

    @NotNull
    public Map<String, Long> getPurchases(@NotNull IPointStore store) {
        return this.getPurchases(store.getId());
    }

    @NotNull
    public Map<String, Long> getPurchases(@NotNull String store) {
        return this.purchases.computeIfAbsent(store.toLowerCase(), set -> new HashMap<>());
    }

    public long getProductCooldown(@NotNull IPointProduct product) {
        return this.getProductCooldown(product.getStore(), product.getId());
    }

    public long getProductCooldown(@NotNull IPointStore store, @NotNull String productId) {
        return this.getProductCooldown(store.getId(), productId);
    }

    public long getProductCooldown(@NotNull String storeId, @NotNull String productId) {
        this.getPurchases(storeId).values().removeIf(cooldown -> cooldown >= 0 && System.currentTimeMillis() >= cooldown);
        return this.getPurchases(storeId).getOrDefault(productId, 0L);
    }

    public boolean isProductOnCooldown(@NotNull IPointProduct product) {
        return this.isProductOnCooldown(product.getStore(), product.getId());
    }

    public boolean isProductOnCooldown(@NotNull IPointStore store, @NotNull String productId) {
        return this.isProductOnCooldown(store.getId(), productId);
    }

    public boolean isProductOnCooldown(@NotNull String storeId, @NotNull String productId) {
        return this.getProductCooldown(storeId, productId) != 0L;
    }

    public boolean canPurchase(@NotNull Player player, @NotNull IPointProduct product, boolean notify) {
        long cooldown = this.getProductCooldown(product);
        if (cooldown != 0L) {
            if (product.getPurchaseCooldown() == 0 || cooldown < 0 && !product.isOneTimedPurchase()) {
                this.getPurchases(product.getStore()).remove(product.getId());
            }
            else {
                if (!notify) return false;

                if (product.isOneTimedPurchase()) {
                    plugin.getMessage(Lang.STORE_BUY_ERROR_SINGLE_PURCHASE).send(player);
                }
                else {
                    plugin.getMessage(Lang.STORE_BUY_ERROR_COOLDOWN).replace(IPointProduct.PLACEHOLDER_COOLDOWN, TimeUtil.formatTimeLeft(cooldown)).send(player);
                }
            }
            return false;
        }

        int priceInherited = this.getInheritedPriceForItem(product);
        if (this.getBalance() < priceInherited) {
            if (notify) plugin.getMessage(Lang.STORE_BUY_ERROR_NO_MONEY).replace(Config.replacePlaceholders()).send(player);
            return false;
        }

        int priceOrig = product.getPriceFinal();
        if (priceInherited == 0 && priceOrig > 0) {
            if (notify) plugin.getMessage(Lang.STORE_BUY_ERROR_INHERITED).send(player);
            return false;
        }

        return true;
    }

    public boolean purchaseProduct(@NotNull Player player, @NotNull IPointProduct product) {
        if (!this.canPurchase(player, product, true)) return false;
        int price = this.getInheritedPriceForItem(product);

        PointProductPurchaseEvent purchaseEvent = new PointProductPurchaseEvent(player, this, product, price);
        plugin.getPluginManager().callEvent(purchaseEvent);
        if (purchaseEvent.isCancelled()) return false;

        // Take user points.
        price = purchaseEvent.getPrice();
        this.takePoints(price);

        // Give product rewards.
        product.giveRewards(player);

        // Add purchase cooldown.
        IPointStore store = product.getStore();
        if (product.getPurchaseCooldown() != 0) {
            long cooldown = product.getPurchaseNextTime();
            this.getPurchases(store).put(product.getId(), cooldown);
        }

        this.saveData(this.plugin);

        plugin.getMessage(Lang.STORE_BUY_SUCCESS)
            .replace(Config.replacePlaceholders())
            .replace(product.replacePlaceholders())
            .replace(this.replacePlaceholders())
            .replace(IPointProduct.PLACEHOLDER_PRICE_INHERITED, price)
            .send(player);

        return true;
    }

    public int getInheritedPriceForItem(@NotNull IPointProduct product) {
        int price = product.getPriceFinal();
        if (!product.isOneTimedPurchase()) return price;

        IPointStore store = product.getStore();

        Set<IPointProduct> inherited = product.getInheritedPrice().stream()
            .map(store::getProduct)
            .filter(Objects::nonNull).filter(IPointProduct::isOneTimedPurchase).filter(this::isProductOnCooldown)
            .collect(Collectors.toSet());

        IPointProduct best = inherited.stream().max(Comparator.comparingInt(IPointProduct::getPriority)).orElse(null);
        return best != null ? Math.max(0, price - best.getPriceFinal()) : price;
    }
}
