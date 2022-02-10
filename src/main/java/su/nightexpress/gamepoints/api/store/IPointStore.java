package su.nightexpress.gamepoints.api.store;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.api.menu.IMenu;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.Perms;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public interface IPointStore extends ConfigHolder, IPlaceholder, ICleanable {

    String PLACEHOLDER_ID                  = "%store_id%";
    String PLACEHOLDER_NAME                = "%store_name%";
    String PLACEHOLDER_PERMISSION_REQUIRED = "%store_permission_required%";
    String PLACEHOLDER_PERMISSION          = "%store_permission%";

    @Override
    @NotNull
    default UnaryOperator<String> replacePlaceholders() {
        return str -> str
                .replace(PLACEHOLDER_ID, this.getId())
                .replace(PLACEHOLDER_NAME, this.getName())
                .replace(PLACEHOLDER_PERMISSION_REQUIRED, plugin().lang().getBoolean(this.isPermissionRequired()))
                .replace(PLACEHOLDER_PERMISSION, Perms.STORE + this.getId())
                ;
    }

    @NotNull GamePoints plugin();

    @NotNull String getId();

    @NotNull
    String getName();

    void setName(@NotNull String name);

    boolean isPermissionRequired();

    void setPermissionRequired(boolean permissionRequired);

    int getPages();

    void setPages(int pages);

    @NotNull Set<IPointDiscount> getDiscounts();

    @Nullable IPointDiscount getDiscount();

    @NotNull Map<String, IPointProduct> getProductsMap();

    @NotNull
    default Collection<IPointProduct> getProducts() {
        return this.getProductsMap().values();
    }

    @Nullable
    default IPointProduct getProduct(@NotNull String id) {
        return this.getProductsMap().get(id.toLowerCase());
    }

    default boolean open(@NotNull Player player) {
        if (!this.hasPermission(player)) {
            plugin().lang().Store_Open_Error_Permission.replace(this.replacePlaceholders()).send(player);
            return false;
        }
        this.getView().open(player, 1);
        return true;
    }

    boolean hasPermission(@NotNull Player player);

    @NotNull IMenu getView();
}
