package su.nightexpress.gamepoints.store;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.utils.FileUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.api.store.IPointStore;
import su.nightexpress.gamepoints.config.Config;
import su.nightexpress.gamepoints.store.listener.StoreListener;
import su.nightexpress.gamepoints.store.menu.StoreMenuConfirmation;
import su.nightexpress.gamepoints.store.menu.StoreMenuMain;
import su.nightexpress.gamepoints.store.object.PointStore;
import su.nightexpress.gamepoints.store.task.BalanceTopTask;

import java.io.File;
import java.util.*;

public class StoreManager extends AbstractManager<GamePoints> {

    private Map<String, IPointStore>         stores;
    private List<Map.Entry<String, Integer>> balanceTop;

    private StoreMenuMain         storeMenuMain;
    private StoreMenuConfirmation storeMenuConfirmation;

    private BalanceTopTask topTask;

    public static final String DIR_STORES = "/stores/";

    public StoreManager(@NotNull GamePoints plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.plugin.getConfigManager().extract(DIR_STORES);
        this.stores = new HashMap<>();
        this.balanceTop = new ArrayList<>();

        for (File dir : FileUtil.getFolders(plugin.getDataFolder() + DIR_STORES)) {
            JYML cfg = new JYML(dir.getAbsolutePath(), dir.getName() + ".yml");
            IPointStore store = new PointStore(plugin, cfg);
            this.stores.put(store.getId(), store);
        }
        this.plugin.info("Stores Loaded: " + stores.size());

        this.storeMenuMain = new StoreMenuMain(this.plugin);
        this.storeMenuConfirmation = new StoreMenuConfirmation(this.plugin);
        this.addListener(new StoreListener(this));

        if (Config.GENERAL_TOP_UPDATE_MIN > 0) {
            this.topTask = new BalanceTopTask(this.plugin);
            this.topTask.start();
        }
    }

    @Override
    protected void onShutdown() {
        if (this.topTask != null) {
            this.topTask.stop();
            this.topTask = null;
        }
        if (this.stores != null) {
            this.stores.values().forEach(IPointStore::clear);
            this.stores.clear();
            this.stores = null;
        }
        if (this.storeMenuMain != null) {
            this.storeMenuMain.clear();
            this.storeMenuMain = null;
        }
    }

    @NotNull
    public StoreMenuMain getStoreMainMenu() {
        return storeMenuMain;
    }

    @NotNull
    public StoreMenuConfirmation getStoreConfirmation() {
        return storeMenuConfirmation;
    }

    @NotNull
    public List<String> getStoreIds() {
        return new ArrayList<>(stores.keySet());
    }

    @Nullable
    public IPointStore getStore(@NotNull String id) {
        return this.stores.get(id.toLowerCase());
    }

    @NotNull
    public Collection<IPointStore> getStores() {
        return this.stores.values();
    }

    @NotNull
    public List<Map.Entry<String, Integer>> getBalanceTop() {
        return this.balanceTop;
    }
}
