package su.nightexpress.gamepoints.lang;

import su.nexmedia.engine.api.lang.LangKey;

public class Lang {

    public static final LangKey COMMAND_ADD_USAGE       = new LangKey("Command.Add.Usage", "<player> <amount>");
    public static final LangKey COMMAND_ADD_DESC        = new LangKey("Command.Add.Desc", "Add points to player account.");
    public static final LangKey COMMAND_ADD_DONE_SENDER = new LangKey("Command.Add.Done.Sender", "&7Added &a%amount% %points_name% &7to &a%user_name%&7. New balance: &a%user_balance% %points_name%&7.");
    public static final LangKey COMMAND_ADD_DONE_USER   = new LangKey("Command.Add.Done.User", "&7You received &a%amount% %points_name%&7!");

    public static final LangKey COMMAND_TAKE_USAGE       = new LangKey("Command.Take.Usage", "<player> <amount>");
    public static final LangKey COMMAND_TAKE_DESC        = new LangKey("Command.Take.Desc", "Take points from player account.");
    public static final LangKey COMMAND_TAKE_DONE_SENDER = new LangKey("Command.Take.Done.Sender", "&7Taken &a%amount% %points_name% &7from &a%user_name%&7. New balance: &a%user_balance% %points_name%&7.");
    public static final LangKey COMMAND_TAKE_DONE_USER   = new LangKey("Command.Take.Done.User", "&7You lost &a%amount% %points_name%&7!");

    public static final LangKey COMMAND_SET_USAGE       = new LangKey("Command.Set.Usage", "<player> <amount>");
    public static final LangKey COMMAND_SET_DESC        = new LangKey("Command.Set.Desc", "Set balance of player account.");
    public static final LangKey COMMAND_SET_DONE_SENDER = new LangKey("Command.Set.Done.Sender", "&7Set &a%amount% %points_name% &7for &a%user_name%&7. New balance: &a%user_balance% %points_name%&7.");
    public static final LangKey COMMAND_SET_DONE_USER   = new LangKey("Command.Set.Done.User", "&7Your balance has been set to &a%amount% %points_name%&7!");

    public static final LangKey COMMAND_PAY_USAGE         = new LangKey("Command.Pay.Usage", "<player> <amount>");
    public static final LangKey COMMAND_PAY_DESC           = new LangKey("Command.Pay.Desc", "Transfer points to player.");
    public static final LangKey COMMAND_PAY_ERROR_NO_MONEY = new LangKey("Command.Pay.Error.NoMoney", "You don't have enought points!");
    public static final LangKey COMMAND_PAY_DONE_SENDER = new LangKey("Command.Pay.Done.Sender", "&7You sent &a%amount% %points_name% &7to &a%player%&7!");
    public static final LangKey COMMAND_PAY_DONE_USER   = new LangKey("Command.Pay.Done.User", "&7You received &a%amount% %points_name%&7 from &a%player%&7!");

    public static final LangKey COMMAND_ADD_PURCHASE_USAGE    = new LangKey("Command.AddPurchase.Usage", "<player> <store> <product>");
    public static final LangKey COMMAND_ADD_PURCHASE_DESC            = new LangKey("Command.AddPurchase.Desc", "Adds specified purchase to user data.");
    public static final LangKey COMMAND_ADD_PURCHASE_DONE_USER         = new LangKey("Command.AddPurchase.Done.User", "&7Purchase added!");
    public static final LangKey COMMAND_ADD_PURCHASE_ERROR_NO_COOLDOWN = new LangKey("Command.AddPurchase.Error.NoCooldown", "&cSpecified product does not have a cooldown! It's useless to add it.");

    public static final LangKey COMMAND_REMOVE_PURCHASE_USAGE    = new LangKey("Command.RemovePurchase.Usage", "<player> <store> <product>");
    public static final LangKey COMMAND_REMOVE_PURCHASE_DESC      = new LangKey("Command.RemovePurchase.Desc", "Removes specified purchase from user data.");
    public static final LangKey COMMAND_REMOVE_PURCHASE_DONE_USER = new LangKey("Command.RemovePurchase.Done.User", "&7Purchase removed!");

    public static final LangKey COMMAND_BALANCE_USAGE = new LangKey("Command.Balance.Usage", "[player]");
    public static final LangKey COMMAND_BALANCE_DESC = new LangKey("Command.Balance.Desc", "Displays player balance.");
    public static final LangKey COMMAND_BALANCE_DONE = new LangKey("Command.Balance.Done", "&a%user_name%&7's balance: &a%user_balance% %points_name%");

    public static final LangKey COMMAND_BALANCE_TOP_USAGE = new LangKey("Command.BalanceTop.Usage", "[page]");
    public static final LangKey COMMAND_BALANCE_TOP_DESC = new LangKey("Command.BalanceTop.Desc", "Displays top balances.");
    public static final LangKey COMMAND_BALANCE_TOP_LIST = new LangKey("Command.BalanceTop.List", """
            &6&m             &6&l[ &e&lGame Points &7- &e&lTop &f%page_min%&7/&f%page_max% &6&l]&6&m             &7
            &6%pos%. &e%user_name%: &a%user_balance% %points_name%
            &6&m             &6&l[ &e&lEnd Game Points Top &6&l]&6&m              &7""");

    public static final LangKey COMMAND_STORE_USAGE = new LangKey("Command.Store.Usage", "[store] [player]");
    public static final LangKey COMMAND_STORE_DESC  = new LangKey("Command.Store.Desc", "Opens specified store.");

    public static final LangKey STORE_ERROR_PRODUCT_INVALID = new LangKey("Store.Error.Product.Invalid", "&cInvalid product!");
    public static final LangKey STORE_ERROR_INVALID         = new LangKey("Store.Error.Invalid", "&cInvalid store!");
    public static final LangKey STORE_OPEN_ERROR_PERMISSION = new LangKey("Store.Open.Error.Permission", "&cWhoops! &7You don't access to %store_name%&7 store!");

    public static final LangKey STORE_BUY_ERROR_NO_MONEY       = new LangKey("Store.Buy.Error.NoMoney", "&cWhoops! &7You don't have enough %points_name%&7!");
    public static final LangKey STORE_BUY_ERROR_INHERITED       = new LangKey("Store.Buy.Error.Inherited", "&cWhoops! &7You already have purchased a higher version of this product!");
    public static final LangKey STORE_BUY_ERROR_SINGLE_PURCHASE = new LangKey("Store.Buy.Error.SinglePurchase", "&cWhoops! &7This product can be purchased only once! You already have purchased this product.");
    public static final LangKey STORE_BUY_ERROR_COOLDOWN        = new LangKey("Store.Buy.Error.Cooldown", "&cWhoops! &7You can purchase this product again in: &c%product_cooldown%");

    public static final LangKey STORE_BUY_SUCCESS = new LangKey("Store.Buy.Success", "&7You successfully bought &a%product_name%&7 for &a%product_price_inherited% %points_name%&7!");
}
