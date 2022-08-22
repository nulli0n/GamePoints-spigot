package su.nightexpress.gamepoints.data;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserDataHandler;
import su.nexmedia.engine.api.data.DataTypes;
import su.nightexpress.gamepoints.GamePoints;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class PointsDataHandler extends AbstractUserDataHandler<GamePoints, PointUser> {

    private static PointsDataHandler instance;

    private final Function<ResultSet, PointUser> FUNC_USER;

    private static final String COL_BALANCE = "balance";
    private static final String COL_PURCHASES = "purchases";

    PointsDataHandler(@NotNull GamePoints plugin) throws SQLException {
        super(plugin);

        this.FUNC_USER = (resultSet) -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COL_USER_UUID));
                String name = resultSet.getString(COL_USER_NAME);
                long dateCreated = resultSet.getLong(COL_USER_DATE_CREATED);
                long lastOnline = resultSet.getLong(COL_USER_LAST_ONLINE);

                int balance = resultSet.getInt(COL_BALANCE);
                Map<String, Map<String, Long>> items = gson.fromJson(resultSet.getString(COL_PURCHASES), new TypeToken<Map<String, Map<String, Long>>>() {}.getType());

                return new PointUser(plugin, uuid, name, dateCreated, lastOnline, balance, items);
            }
            catch (SQLException e) {
                return null;
            }
        };
    }

    @NotNull
    public static synchronized PointsDataHandler getInstance(@NotNull GamePoints plugin) throws SQLException {
        if (instance == null) {
            instance = new PointsDataHandler(plugin);
        }
        return instance;
    }

    @Override
    protected void onTableCreate() {
        super.onTableCreate();
        this.addColumn(this.tableUsers, "purchases", DataTypes.STRING.build(this.dataType));
    }

    public void updateUserBalance(@NotNull PointUser user) {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT `" + COL_BALANCE + "` FROM " + this.tableUsers + " WHERE `" + COL_USER_NAME + "` = ?";
            try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
                statement.setString(1, user.getName());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(COL_BALANCE);
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            return -1;
        });

        future.thenAccept(balance -> {
            if (balance >= 0) {
                user.setBalance(balance);
            }
        });

        /*try {
            future.get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/
    }

    @NotNull
    public Map<String, Integer> getUserBalance() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT `name`, `balance` FROM " + this.tableUsers;

        try (Statement statement = this.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString(COL_USER_NAME);
                int balance = resultSet.getInt(COL_BALANCE);

                map.put(name, balance);
            }
            return map;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return map;
        }
    }

    @Override
    @NotNull
    protected LinkedHashMap<String, String> getColumnsToCreate() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(COL_PURCHASES, DataTypes.STRING.build(this.dataType));
        map.put(COL_BALANCE, DataTypes.INTEGER.build(this.dataType));
        return map;
    }

    @Override
    @NotNull
    protected LinkedHashMap<String, String> getColumnsToSave(@NotNull PointUser user) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(COL_PURCHASES, this.gson.toJson(user.getPurchases()));
        map.put(COL_BALANCE, String.valueOf(user.getBalance()));
        if (this.hasColumn(this.tableUsers, "items")) {
            map.put("items", "{}");
        }
        return map;
    }

    @Override
    @NotNull
    protected Function<ResultSet, PointUser> getFunctionToUser() {
        return this.FUNC_USER;
    }
}
