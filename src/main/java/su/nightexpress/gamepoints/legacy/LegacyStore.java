package su.nightexpress.gamepoints.legacy;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.gamepoints.GamePoints;

public class LegacyStore {

    public static void update(@NotNull GamePoints plugin, @NotNull JYML cfg) {
        String id = cfg.getFile().getName().replace(".yml", "").toLowerCase();

        String name = StringUtil.color(cfg.getString("name", ""));
        boolean isPermission = cfg.getBoolean("need-permission");

        JYML configNew = new JYML(plugin.getDataFolder() + "/stores/" + id, "/" + id + ".yml");
        configNew.set("Name", name);
        configNew.set("Permission_Required", isPermission);
        configNew.set("View.Title", cfg.getString("title", name + " Store"));
        configNew.set("View.Size", cfg.getInt("size", 54));
        configNew.saveChanges();

        JYML configProd = new JYML(plugin.getDataFolder() + "/stores/" + id, "/products.yml");
        for (String pId : cfg.getSection("items")) {
            String path = "items." + pId + ".";

            String path2 = pId + ".";
            configProd.set(path2 + "Name", pId);
            configProd.set(path2 + "Description", ItemUtil.getLore(cfg.getItem(path + "icon")));
            configProd.set(path2 + "Price", cfg.getInt(path + "cost"));
            configProd.set(path2 + "Purchase_Cooldown", cfg.getBoolean(path + "one-time-buy") ? -1L : 0L);
            configProd.set(path2 + "Priority", cfg.getInt(path + "priority"));
            configProd.set(path2 + "Inheritance.Rewards", cfg.getStringList(path + "inheritance-item"));
            configProd.set(path2 + "Inheritance.Price", cfg.getStringList(path + "inheritance-price"));
            configProd.setItem(path2 + "Preview", cfg.getItem(path + "icon"));
            configProd.set(path2 + "Preview.lore", null);
            configProd.set(path2 + "Rewards.Commands", cfg.getStringList(path + "commands"));
            configProd.set(path2 + "Store.Slot", cfg.getInt(path + "store-slot"));
        }
        configProd.saveChanges();
        cfg.getFile().delete();

        plugin.info("Updated config for '" + id + "' store!");
    }
}
