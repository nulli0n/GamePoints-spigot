package su.nightexpress.gamepoints.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.gamepoints.data.PointUser;

public class PointUserChangeBalanceEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    private final PointUser user;
    private final int balanceOld;
    private final int balanceNew;

    private boolean isCancelled;

    public PointUserChangeBalanceEvent(@NotNull PointUser user, int balanceOld, int balanceNew) {
        super(!Bukkit.isPrimaryThread());
        this.user = user;
        this.balanceOld = balanceOld;
        this.balanceNew = balanceNew;
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
    public PointUser getUser() {
        return user;
    }

    public int getBalanceOld() {
        return balanceOld;
    }

    public int getBalanceNew() {
        return balanceNew;
    }
}
