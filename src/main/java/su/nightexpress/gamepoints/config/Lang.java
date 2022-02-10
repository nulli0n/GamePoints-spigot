package su.nightexpress.gamepoints.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.LangMessage;
import su.nexmedia.engine.core.config.CoreLang;
import su.nightexpress.gamepoints.GamePoints;

public class Lang extends CoreLang {

    public Lang(@NotNull GamePoints plugin) {
        super(plugin);
    }

    public LangMessage Command_Add_Usage       = new LangMessage(this, "<player> <amount>");
    public LangMessage Command_Add_Desc        = new LangMessage(this, "Add points to player account.");
    public LangMessage Command_Add_Done_Sender = new LangMessage(this, "&7Added &a%amount% %points_name% &7to &a%user_name%&7. New balance: &a%user_balance% %points_name%&7.");
    public LangMessage Command_Add_Done_User   = new LangMessage(this, "&7You received &a%amount% %points_name%&7!");

    public LangMessage Command_Take_Usage       = new LangMessage(this, "<player> <amount>");
    public LangMessage Command_Take_Desc        = new LangMessage(this, "Take points from player account.");
    public LangMessage Command_Take_Done_Sender = new LangMessage(this, "&7Taken &a%amount% %points_name% &7from &a%user_name%&7. New balance: &a%user_balance% %points_name%&7.");
    public LangMessage Command_Take_Done_User   = new LangMessage(this, "&7You lost &a%amount% %points_name%&7!");

    public LangMessage Command_Set_Usage       = new LangMessage(this, "<player> <amount>");
    public LangMessage Command_Set_Desc        = new LangMessage(this, "Set balance of player account.");
    public LangMessage Command_Set_Done_Sender = new LangMessage(this, "&7Set &a%amount% %points_name% &7for &a%user_name%&7. New balance: &a%user_balance% %points_name%&7.");
    public LangMessage Command_Set_Done_User   = new LangMessage(this, "&7Your balance has been set to &a%amount% %points_name%&7!");

    public LangMessage Command_Pay_Usage         = new LangMessage(this, "<player> <amount>");
    public LangMessage Command_Pay_Desc          = new LangMessage(this, "Transfer points to player.");
    public LangMessage Command_Pay_Error_NoMoney = new LangMessage(this, "You don't have enought points!");
    public LangMessage Command_Pay_Done_Sender   = new LangMessage(this, "&7You sent &a%amount% %points_name% &7to &a%player%&7!");
    public LangMessage Command_Pay_Done_User     = new LangMessage(this, "&7You received &a%amount% %points_name%&7 from &a%player%&7!");

    public LangMessage Command_AddPurchase_Usage     = new LangMessage(this, "<player> <store> <product>");
    public LangMessage Command_AddPurchase_Desc      = new LangMessage(this, "Adds specified purchase to user data.");
    public LangMessage Command_AddPurchase_Done_User = new LangMessage(this, "&7Purchase added!");
    public LangMessage Command_AddPurchase_Error_NoCooldown = new LangMessage(this, "&cSpecified product does not have a cooldown! It's useless to add it.");

    public LangMessage Command_RemovePurchase_Usage     = new LangMessage(this, "<player> <store> <product>");
    public LangMessage Command_RemovePurchase_Desc      = new LangMessage(this, "Removes specified purchase from user data.");
    public LangMessage Command_RemovePurchase_Done_User = new LangMessage(this, "&7Purchase removed!");

    public LangMessage Command_Balance_Usage = new LangMessage(this, "[player]");
    public LangMessage Command_Balance_Desc  = new LangMessage(this, "Displays player balance.");
    public LangMessage Command_Balance_Done  = new LangMessage(this, "&a%user_name%&7's balance: &a%user_balance% %points_name%");

    public LangMessage Command_BalanceTop_Usage = new LangMessage(this, "[page]");
    public LangMessage Command_BalanceTop_Desc  = new LangMessage(this, "Displays top balances.");
    public LangMessage Command_BalanceTop_List  = new LangMessage(
            this, """
            &6&m             &6&l[ &e&lGame Points &7- &e&lTop &f%page_min%&7/&f%page_max% &6&l]&6&m             &7
            &6%pos%. &e%user_name%: &a%user_balance% %points_name%
            &6&m             &6&l[ &e&lEnd Game Points Top &6&l]&6&m              &7""");

    public LangMessage Command_Store_Usage = new LangMessage(this, "[store] [player]");
    public LangMessage Command_Store_Desc  = new LangMessage(this, "Opens specified store.");

    public LangMessage Store_Error_Product_Invalid = new LangMessage(this, "&cInvalid product!");
    public LangMessage Store_Error_Invalid         = new LangMessage(this, "&cInvalid store!");
    public LangMessage Store_Open_Error_Permission = new LangMessage(this, "&cWhoops! &7You don't access to %store_name%&7 store!");

    public LangMessage Store_Buy_Error_NoMoney   = new LangMessage(this, "&cWhoops! &7You don't have enough %points_name%&7!");
    public LangMessage Store_Buy_Error_Inherited = new LangMessage(this, "&cWhoops! &7You already have purchased a higher version of this product!");
    public LangMessage Store_Buy_Error_SinglePurchase = new LangMessage(this, "&cWhoops! &7This product can be purchased only once! You already have purchased this product.");
    public LangMessage Store_Buy_Error_Cooldown       = new LangMessage(this, "&cWhoops! &7You can purchase this product again in: &c%product_cooldown%");

    public LangMessage Store_Buy_Success = new LangMessage(this, "&7You successfully bought &a%product_name%&7 for &a%product_price_inherited% %points_name%&7!");
}
