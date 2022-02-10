package su.nightexpress.gamepoints.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.gamepoints.api.store.IPointProduct;
import su.nightexpress.gamepoints.api.store.IPointStore;
import su.nightexpress.gamepoints.data.PointUser;

public class PointProductPurchaseEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    private final Player        player;
    private final PointUser     user;
    private final IPointProduct product;
    private       int           price;

    private boolean isCancelled;

    public PointProductPurchaseEvent(@NotNull Player player, @NotNull PointUser user, @NotNull IPointProduct product, int price) {
        this.player = player;
        this.user = user;
        this.product = product;
        this.setPrice(price);
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public PointUser getUser() {
        return user;
    }

    @NotNull
    public IPointProduct getProduct() {
        return product;
    }

    @NotNull
    public final IPointStore getStore() {
        return this.getProduct().getStore();
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
