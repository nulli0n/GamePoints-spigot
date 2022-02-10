package su.nightexpress.gamepoints.store.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.api.event.PointProductPurchaseEvent;
import su.nightexpress.gamepoints.api.store.IPointProduct;
import su.nightexpress.gamepoints.config.Config;
import su.nightexpress.gamepoints.data.PointUser;
import su.nightexpress.gamepoints.store.StoreManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class StoreListener extends AbstractListener<GamePoints> {

    public StoreListener(@NotNull StoreManager storeManager) {
        super(storeManager.plugin());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPointPurchase(PointProductPurchaseEvent event) {
        if (!Config.TRANSACTION_LOGS_TO_CONSOLE && !Config.TRANSACTION_LOGS_TO_FILE) return;

        IPointProduct product = event.getProduct();
        PointUser user = event.getUser();
        int price = event.getPrice();

        String format = Config.TRANSACTION_LOGS_FORMAT.replace(IPointProduct.PLACEHOLDER_PRICE_INHERITED, String.valueOf(price));
        format = user.replacePlaceholders().apply(format);
        format = product.getStore().replacePlaceholders().apply(format);
        format = product.replacePlaceholders().apply(format);

        if (Config.TRANSACTION_LOGS_TO_CONSOLE) {
            this.plugin.info(format);
        }
        if (Config.TRANSACTION_LOGS_TO_FILE) {
            String date = LocalDateTime.now().format(Config.TRANSACTION_LOGS_DATE);
            String path = plugin.getDataFolder() + "/" + Config.TRANSACTION_LOGS_FILENAME;
            BufferedWriter output;
            try {
                output = new BufferedWriter(new FileWriter(path, true));
                output.append("[").append(date).append("] ").append(format);
                output.newLine();
                output.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
