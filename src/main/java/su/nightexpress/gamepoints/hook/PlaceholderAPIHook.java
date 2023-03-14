package su.nightexpress.gamepoints.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.api.GamePointsAPI;
import su.nightexpress.gamepoints.api.store.IPointProduct;
import su.nightexpress.gamepoints.api.store.IPointStore;
import su.nightexpress.gamepoints.data.PointUser;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlaceholderAPIHook {

    private static PointsExpansion pointsExpansion;

    public static void setup() {
        if (pointsExpansion == null) {
            pointsExpansion = new PointsExpansion();
            pointsExpansion.register();
        }
    }

    public static void shutdown() {
        if (pointsExpansion != null) {
            pointsExpansion.unregister();
            pointsExpansion = null;
        }
    }

    public static class PointsExpansion extends PlaceholderExpansion {

        @Override
        @NotNull
        public String getAuthor() {
            return GamePointsAPI.PLUGIN.getDescription().getAuthors().get(0);
        }

        @Override
        @NotNull
        public String getIdentifier() {
            return "gamepoints";
        }

        @Override
        @NotNull
        public String getVersion() {
            return GamePointsAPI.PLUGIN.getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest(Player player, String holder) {
            GamePoints plugin = GamePointsAPI.PLUGIN;

            if (holder.startsWith("item_rawprice")) {
                String[] ss = this.getProductStoreIds("item_rawprice_", holder);
                String storeId = ss[0];
                String itemId = ss[1];

                IPointStore store = plugin.getStoreManager().getStore(storeId);
                if (store == null) return null;

                IPointProduct item = store.getProduct(itemId);
                if (item == null) return null;

                return String.valueOf(item.getPrice());
            }

            if (holder.startsWith("top")) {
                // top_balance_1, top_player_1
                String[] split = holder.split("top_");
                if (split.length < 2) return "N/A";

                String[] splitTypePos = split[1].split("_");
                if (splitTypePos.length < 2) return "N/A";

                String type = splitTypePos[0];

                int pos = StringUtil.getInteger(splitTypePos[1], 0);
                if (pos == 0) return "-";

                List<Map.Entry<String, Integer>> baltop = plugin.getStoreManager().getBalanceTop();
                if (pos > baltop.size()) return "-";

                Map.Entry<String, Integer> top = baltop.get(pos - 1);
                return type.equalsIgnoreCase("balance") ? String.valueOf(top.getValue()) : top.getKey();
            }

            if (player == null) return null;

            PointUser user = plugin.getUserManager().getUserData(player);
            if (holder.startsWith("item_price")) {
                String[] ss = this.getProductStoreIds("item_price_", holder);
                String storeId = ss[0];
                String itemId = ss[1];

                IPointStore store = plugin.getStoreManager().getStore(storeId);
                if (store == null) return null;

                IPointProduct item = store.getProduct(itemId);
                if (item == null) return null;

                return String.valueOf(user.getInheritedPriceForItem(item));
            }
            if (holder.equalsIgnoreCase("balance")) {
                return String.valueOf(user.getBalance());
            }
            if (holder.equalsIgnoreCase("balance_formatted")) {
                NumberFormat format = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
                return format.format(user.getBalance());
            }

            return null;
        }

        @NotNull
        private String[] getProductStoreIds(@NotNull String prefix, @NotNull String holder) {
            // ranks_rank-vip
            String left = holder.replace(prefix, "");
            int index = left.indexOf('_');
            String storeId = left.substring(0, index);
            String itemId = left.substring(index + 1);

            //System.out.println("store: " + storeId);
            //System.out.println("item: " + itemId);

            return new String[]{storeId, itemId};
        }
    }
}
