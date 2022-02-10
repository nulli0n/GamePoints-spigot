package su.nightexpress.gamepoints.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.ConfigTemplate;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.gamepoints.GamePoints;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.UnaryOperator;

public class Config extends ConfigTemplate {

    public static String POINTS_NAME;
    public static int    GENERAL_START_BALANCE;
    public static int    GENERAL_TOP_UPDATE_MIN;

    public static List<String> STORE_PRODUCT_DISPLAY_FORMAT;
    public static List<String> STORE_PRODUCT_PURCHASE_FORMAT_ALREADY_HAVE;
    public static List<String> STORE_PRODUCT_PURCHASE_FORMAT_COOLDOWN;
    public static List<String> STORE_PRODUCT_PURCHASE_FORMAT_AVAILABLE;

    public static String            TRANSACTION_LOGS_FORMAT;
    public static DateTimeFormatter TRANSACTION_LOGS_DATE;
    public static boolean           TRANSACTION_LOGS_TO_CONSOLE;
    public static boolean           TRANSACTION_LOGS_TO_FILE;
    public static String            TRANSACTION_LOGS_FILENAME = "transactions.log";

    public static final String PLACEHOLDER_POINTS_NAME = "%points_name%";

    @NotNull
    public static UnaryOperator<String> replacePlaceholders() {
        return str -> str.replace(PLACEHOLDER_POINTS_NAME, POINTS_NAME);
    }

    public Config(@NotNull GamePoints plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        String path = "General.";
        POINTS_NAME = StringUtil.color(cfg.getString(path + "Points_Name", "Game Points"));
        GENERAL_START_BALANCE = cfg.getInt(path + "Start_Balance", 0);
        GENERAL_TOP_UPDATE_MIN = cfg.getInt(path + "Balance_Top.Update_Interval", 20);

        path = "Store.Product.";
        STORE_PRODUCT_DISPLAY_FORMAT = StringUtil.color(cfg.getStringList(path + "Display.Format"));
        STORE_PRODUCT_PURCHASE_FORMAT_ALREADY_HAVE = StringUtil.color(cfg.getStringList(path + "Purchase.Format.Already_Have"));
        STORE_PRODUCT_PURCHASE_FORMAT_COOLDOWN = StringUtil.color(cfg.getStringList(path + "Purchase.Format.Cooldown"));
        STORE_PRODUCT_PURCHASE_FORMAT_AVAILABLE = StringUtil.color(cfg.getStringList(path + "Purchase.Format.Available"));

        path = "Transaction_Logs.";
        TRANSACTION_LOGS_TO_CONSOLE = cfg.getBoolean(path + "Console");
        TRANSACTION_LOGS_TO_FILE = cfg.getBoolean(path + "File");
        if (TRANSACTION_LOGS_TO_CONSOLE || TRANSACTION_LOGS_TO_FILE) {
            TRANSACTION_LOGS_FORMAT = cfg.getString(path + "Format", "%user_name% bought ''%product_id%'' in ''%store_id%'' store for %product_price_inherited% points.");
            TRANSACTION_LOGS_DATE = DateTimeFormatter.ofPattern(cfg.getString(path + "Date", "dd/MM/yyyy HH:mm:ss"));
        }
    }
}
