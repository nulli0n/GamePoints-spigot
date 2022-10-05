package su.nightexpress.gamepoints.conversion.converter;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.gamepoints.GamePoints;

public abstract class AbstractDataConverter {

    protected final GamePoints plugin;
    protected final String pluginName;

    public AbstractDataConverter(@NotNull GamePoints plugin, @NotNull String pluginName) {
        this.plugin = plugin;
        this.pluginName = pluginName;
    }

    @NotNull
    public String getPluginName() {
        return pluginName;
    }

    @Nullable
    public Plugin getTargetPlugin() {
        return this.plugin.getPluginManager().getPlugin(this.getPluginName());
    }

    public abstract void convert();
}
