package su.nightexpress.gamepoints.store.task;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.task.AbstractTask;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.config.Config;
import su.nightexpress.gamepoints.store.StoreManager;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;

public class BalanceTopTask extends AbstractTask<GamePoints> {

    public BalanceTopTask(@NotNull GamePoints plugin) {
        super(plugin, Config.GENERAL_TOP_UPDATE_MIN * 60, true);
    }

    @Override
    public void action() {
        plugin.info("Updating balance top...");
        long took = System.currentTimeMillis();

        StoreManager storeManager = this.plugin.getStoreManager();
        storeManager.getBalanceTop().clear();

        Map<String, Integer> map = plugin.getData().getUserBalance();
        map.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEach((entry) -> {
            storeManager.getBalanceTop().add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
        });

        took = System.currentTimeMillis() - took;
        plugin.info("Balance top updated in " + took + " ms!");
    }
}
