package su.nightexpress.gamepoints.conversion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.hooks.Hooks;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.api.GamePointsAPI;
import su.nightexpress.gamepoints.conversion.converter.AbstractDataConverter;
import su.nightexpress.gamepoints.conversion.converter.PlayerPointsConverter;

import java.util.stream.Stream;

public enum DataConverter {

    PLAYER_POINTS("PlayerPoints", PlayerPointsConverter.class);

    private final String                                 pluginName;
    private final Class<? extends AbstractDataConverter> clazz;

    DataConverter(@NotNull String pluginName, @NotNull Class<? extends AbstractDataConverter> clazz) {
        this.pluginName = pluginName;
        this.clazz = clazz;
    }

    @NotNull
    public String getPluginName() {
        return pluginName;
    }

    @Nullable
    public AbstractDataConverter getConverter() {
        try {
            return this.clazz.getConstructor(GamePoints.class).newInstance(GamePointsAPI.PLUGIN);
        }
        catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static AbstractDataConverter getConverter(@NotNull String pluginName) {
        return Stream.of(values())
            .filter(type -> Hooks.hasPlugin(type.getPluginName()))
            .filter(type -> type.getPluginName().equalsIgnoreCase(pluginName))
            .map(DataConverter::getConverter).findFirst().orElse(null);
    }
}
