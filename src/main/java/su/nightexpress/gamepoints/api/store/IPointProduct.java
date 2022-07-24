package su.nightexpress.gamepoints.api.store;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.gamepoints.GamePoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;

public interface IPointProduct extends IPlaceholder {

    String PLACEHOLDER_ID                = "%product_id%";
    String PLACEHOLDER_NAME              = "%product_name%";
    String PLACEHOLDER_DESCRIPTION       = "%product_description%";
    String PLACEHOLDER_PRICE             = "%product_price%";
    String PLACEHOLDER_PRICE_FINAL       = "%product_price_final%";
    String PLACEHOLDER_PRICE_INHERITED   = "%product_price_inherited%";
    String PLACEHOLDER_COOLDOWN          = "%product_cooldown%";
    String PLACEHOLDER_ONE_TIME_PURCHASE = "%product_one_time_purchase%";
    String PLACEHOLDER_PURCHASE_COOLDOWN = "%product_purchase_cooldown%";

    @Override
    @NotNull
    default UnaryOperator<String> replacePlaceholders() {
        GamePoints plugin = this.getStore().plugin();
        return str -> str
            .replace(PLACEHOLDER_DESCRIPTION, String.join("\n", this.getDescription()))
            .replace(PLACEHOLDER_ID, this.getId())
            .replace(PLACEHOLDER_NAME, this.getName())
            .replace(PLACEHOLDER_ONE_TIME_PURCHASE, LangManager.getBoolean(this.isOneTimedPurchase()))
            .replace(PLACEHOLDER_PURCHASE_COOLDOWN, TimeUtil.formatTime(this.getPurchaseCooldown()))
            .replace(PLACEHOLDER_PRICE, String.valueOf(this.getPrice()))
            .replace(PLACEHOLDER_PRICE_FINAL, String.valueOf(this.getPriceFinal()))
            ;
    }

    default void giveRewards(@NotNull Player player) {
        IPointStore store = this.getStore();

        List<String> commands = new ArrayList<>(this.getRewardCommands());
        this.getInheritedRewards().stream().map(store::getProduct).filter(Objects::nonNull).forEach(inherited -> {
            commands.addAll(inherited.getRewardCommands());
        });
        commands.forEach(command -> PlayerUtil.dispatchCommand(player, command));
    }

    @NotNull IPointStore getStore();

    @NotNull String getId();

    @NotNull String getName();

    void setName(@NotNull String name);

    @NotNull List<String> getDescription();

    void setDescription(@NotNull List<String> description);

    int getPrice();

    void setPrice(int price);

    int getPriceFinal();

    long getPurchaseCooldown();

    void setPurchaseCooldown(long purchaseCooldown);

    default boolean isOneTimedPurchase() {
        return this.getPurchaseCooldown() < 0;
    }

    default long getPurchaseNextTime() {
        long cooldown = this.getPurchaseCooldown();
        if (cooldown == 0L) return 0L;
        return cooldown < 0L ? cooldown : cooldown + System.currentTimeMillis();
    }

    int getPriority();

    void setPriority(int priority);

    @NotNull Set<String> getInheritedRewards();

    void setInheritedRewards(@NotNull Set<String> inheritedRewards);

    @NotNull Set<String> getInheritedPrice();

    void setInheritedPrice(@NotNull Set<String> inheritedPrice);

    @NotNull ItemStack getPreview();

    void setPreview(@NotNull ItemStack preview);

    @NotNull List<String> getRewardCommands();

    void setRewardCommands(@NotNull List<String> rewardCommands);

    int getStorePage();

    void setStorePage(int page);

    int getStoreSlot();

    void setStoreSlot(int storeSlot);
}
