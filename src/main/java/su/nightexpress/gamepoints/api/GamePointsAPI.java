package su.nightexpress.gamepoints.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.api.store.IPointStore;
import su.nightexpress.gamepoints.data.PointUser;
import su.nightexpress.gamepoints.data.PointsUserManager;
import su.nightexpress.gamepoints.store.StoreManager;

import java.util.Collection;
import java.util.UUID;

public class GamePointsAPI {

    private static final GamePoints PLUGIN = GamePoints.getPlugin(GamePoints.class);

    @NotNull
    public static PointUser getUserData(@NotNull Player player) {
        return PLUGIN.getUserManager().getUserData(player);
    }

    @Nullable
    public static PointUser getUserData(@NotNull String name) {
        return PLUGIN.getUserManager().getUserData(name);
    }

    @Nullable
    public static PointUser getUserData(@NotNull UUID uuid) {
        return PLUGIN.getUserManager().getUserData(uuid);
    }

    @Nullable
    public static IPointStore getStore(@NotNull String id) {
        return PLUGIN.getStoreManager().getStore(id);
    }

    @NotNull
    public static Collection<IPointStore> getStores() {
        return PLUGIN.getStoreManager().getStores();
    }

    @NotNull
    public static PointsUserManager getUserManager() {
        return PLUGIN.getUserManager();
    }

    @NotNull
    public static StoreManager getStoreManager() {
        return PLUGIN.getStoreManager();
    }
}
