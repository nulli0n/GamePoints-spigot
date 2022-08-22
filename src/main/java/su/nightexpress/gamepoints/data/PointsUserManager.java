package su.nightexpress.gamepoints.data;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserManager;
import su.nightexpress.gamepoints.GamePoints;

import java.util.UUID;

public class PointsUserManager extends AbstractUserManager<GamePoints, PointUser> {

    public PointsUserManager(@NotNull GamePoints plugin) {
        super(plugin, plugin);
    }

    @Override
    @NotNull
    protected PointUser createData(@NotNull UUID uuid, @NotNull String name) {
        return new PointUser(plugin, uuid, name);
    }

    @Override
    protected void onSynchronize() {
        this.plugin.getUserManager().getUsersLoaded().forEach(user -> this.plugin.getData().updateUserBalance(user));
    }
}
