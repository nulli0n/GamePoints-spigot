package su.nightexpress.gamepoints.data;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserDataHandler;
import su.nexmedia.engine.api.data.sql.SQLColumn;
import su.nexmedia.engine.api.data.sql.SQLValue;
import su.nexmedia.engine.api.data.sql.column.ColumnType;
import su.nexmedia.engine.api.data.sql.executor.SelectQueryExecutor;
import su.nightexpress.gamepoints.GamePoints;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class PointsDataHandler extends AbstractUserDataHandler<GamePoints, PointUser> {

    private static PointsDataHandler instance;

    private final Function<ResultSet, PointUser> userFunction;

    private static final SQLColumn COLUMN_BALANCE   = SQLColumn.of("balance", ColumnType.INTEGER);
    private static final SQLColumn COLUMN_PURCHASES = SQLColumn.of("purchases", ColumnType.STRING);

    PointsDataHandler(@NotNull GamePoints plugin) {
        super(plugin, plugin);

        this.userFunction = (resultSet) -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());

                int balance = resultSet.getInt(COLUMN_BALANCE.getName());
                Map<String, Map<String, Long>> items = gson.fromJson(resultSet.getString(COLUMN_PURCHASES.getName()), new TypeToken<Map<String, Map<String, Long>>>() {}.getType());

                return new PointUser(plugin, uuid, name, dateCreated, lastOnline, balance, items);
            }
            catch (SQLException e) {
                return null;
            }
        };
    }

    @NotNull
    public static PointsDataHandler getInstance(@NotNull GamePoints plugin) {
        if (instance == null) {
            instance = new PointsDataHandler(plugin);
        }
        return instance;
    }

    @Override
    protected boolean useNewMethods() {
        return true;
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
        instance = null;
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getUsersLoaded().forEach(this::updateUserBalance);
    }

    @Override
    protected boolean createUserTable() {
        super.createUserTable();
        this.dropColumn(this.tableUsers, SQLColumn.of("items", ColumnType.STRING));
        return true;
    }

    public void updateUserBalance(@NotNull PointUser user) {
        PointUser fromDb = this.getUser(user.getId());
        if (fromDb == null) return;

        user.setBalanceRaw(fromDb.getBalance());
    }

    @NotNull
    public Map<String, Integer> getUserBalance() {
        Map<String, Integer> map = new HashMap<>();

        Function<ResultSet, Void> function = resultSet -> {
            try {
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                int balance = resultSet.getInt(COLUMN_BALANCE.getName());

                map.put(name, balance);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };

        SelectQueryExecutor.builder(this.tableUsers, function)
            .columns(COLUMN_USER_NAME, COLUMN_BALANCE)
            .execute(this.getConnector());
        return map;
    }

    @Override
    @NotNull
    protected List<SQLColumn> getExtraColumns() {
        return Arrays.asList(COLUMN_BALANCE, COLUMN_PURCHASES);
    }

    @Override
    @NotNull
    protected List<SQLValue> getSaveColumns(@NotNull PointUser user) {
        return Arrays.asList(
            COLUMN_BALANCE.toValue(user.getBalance()),
            COLUMN_PURCHASES.toValue(this.gson.toJson(user.getPurchases()))
        );
    }

    @Override
    @NotNull
    protected LinkedHashMap<String, String> getColumnsToCreate() {
        //map.put(COL_PURCHASES, DataTypes.STRING.build(this.getDataType()));
        //map.put(COL_BALANCE, DataTypes.INTEGER.build(this.getDataType()));
        return new LinkedHashMap<>();
    }

    @Override
    @NotNull
    protected LinkedHashMap<String, String> getColumnsToSave(@NotNull PointUser user) {
        //map.put(COL_PURCHASES, this.gson.toJson(user.getPurchases()));
        //map.put(COL_BALANCE, String.valueOf(user.getBalance()));
        //if (this.hasColumn(this.tableUsers, "items")) {
        //    map.put("items", "{}");
        //}
        return new LinkedHashMap<>();
    }

    @Override
    @NotNull
    protected Function<ResultSet, PointUser> getFunctionToUser() {
        return this.userFunction;
    }
}
