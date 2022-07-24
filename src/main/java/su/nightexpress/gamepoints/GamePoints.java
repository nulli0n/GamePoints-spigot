package su.nightexpress.gamepoints;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.data.UserDataHolder;
import su.nexmedia.engine.hooks.Hooks;
import su.nightexpress.gamepoints.command.*;
import su.nightexpress.gamepoints.config.Config;
import su.nightexpress.gamepoints.lang.Lang;
import su.nightexpress.gamepoints.data.PointsDataHandler;
import su.nightexpress.gamepoints.data.PointUser;
import su.nightexpress.gamepoints.data.PointsUserManager;
import su.nightexpress.gamepoints.hook.PlaceholderAPIHook;
import su.nightexpress.gamepoints.store.StoreManager;

import java.sql.SQLException;

public class GamePoints extends NexPlugin<GamePoints> implements UserDataHolder<GamePoints, PointUser> {

    private StoreManager      storeManager;
    private PointsDataHandler pointsDataHandler;
    private PointsUserManager pointsUserManager;

    @Override
    @NotNull
    protected GamePoints getSelf() {
        return this;
    }

    @Override
    public void enable() {
        this.storeManager = new StoreManager(this);
        this.storeManager.setup();

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, c -> {
            this.getUserManager().getActiveUsers().forEach(user -> this.getData().updateUserBalance(user));
        }, 0L, 100L);
    }

    @Override
    public void disable() {
        if (this.storeManager != null) {
            this.storeManager.shutdown();
            this.storeManager = null;
        }
    }

    @Override
    public boolean setupDataHandlers() {
        try {
            this.pointsDataHandler = PointsDataHandler.getInstance(this);
            this.pointsDataHandler.setup();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        this.pointsUserManager = new PointsUserManager(this);
        this.pointsUserManager.setup();

        return true;
    }

    @Override
    public void loadConfig() {
        Config.load(this);
    }

    @Override
    public void loadLang() {
        this.getLangManager().loadMissing(Lang.class);
    }

    @Override
    public void registerCommands(@NotNull GeneralCommand<GamePoints> mainCommand) {
        mainCommand.addChildren(new AddCommand(this));
        mainCommand.addChildren(new AddPurchaseCommand(this));
        mainCommand.addChildren(new BalanceCommand(this));
        mainCommand.addChildren(new BalanceTopCommand(this));
        mainCommand.addChildren(new PayCommand(this));
        mainCommand.addChildren(new SetCommand(this));
        mainCommand.addChildren(new StoreCommand(this));
        mainCommand.addChildren(new TakeCommand(this));
        mainCommand.addChildren(new RemovePurchaseCommand(this));
    }

    @Override
    public void registerHooks() {
        if (Hooks.hasPlaceholderAPI()) {
            this.registerHook(Hooks.PLACEHOLDER_API, PlaceholderAPIHook.class);
        }
    }

    @NotNull
    public StoreManager getStoreManager() {
        return this.storeManager;
    }

    @Override
    @NotNull
    public PointsDataHandler getData() {
        return this.pointsDataHandler;
    }

    @NotNull
    @Override
    public PointsUserManager getUserManager() {
        return pointsUserManager;
    }
}
