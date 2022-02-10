package su.nightexpress.gamepoints.data;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserManager;
import su.nightexpress.gamepoints.GamePoints;

public class UserManager extends AbstractUserManager<GamePoints, PointUser> {

    public UserManager(@NotNull GamePoints plugin) {
        super(plugin, plugin);
    }

    @Override
    @NotNull
    protected PointUser createData(@NotNull Player player) {
        return new PointUser(plugin, player);
    }
}
