package su.nightexpress.gamepoints.conversion.converter;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.manager.DataManager;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.gamepoints.GamePoints;
import su.nightexpress.gamepoints.conversion.DataConverter;
import su.nightexpress.gamepoints.data.PointUser;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerPointsConverter extends AbstractDataConverter {

    public PlayerPointsConverter(@NotNull GamePoints plugin) {
        super(plugin, DataConverter.PLAYER_POINTS.getPluginName());
    }

    @Override
    public void convert() {
        PlayerPoints playerPoints = (PlayerPoints) this.getTargetPlugin();
        if (playerPoints == null) return;

        Map<UUID, Integer> pointsMap = new HashMap<>();
        Map<UUID, String> nameMap = new HashMap<>();

        DataManager dataManager = playerPoints.getManager(DataManager.class);
        try {
            dataManager.getDatabaseConnector().connect(connection -> {
                String query = "SELECT * FROM " + dataManager.getTablePrefix() + "points";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    int points = resultSet.getInt("points");

                    pointsMap.put(uuid, points);
                }
                statement.close();


                query = "SELECT * FROM " + dataManager.getTablePrefix() + "username_cache";
                statement = connection.createStatement();
                resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    String name = resultSet.getString("username");

                    nameMap.put(uuid, name);
                }
                statement.close();
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        pointsMap.keySet().removeIf(uuid -> !nameMap.containsKey(uuid));
        pointsMap.values().removeIf(points -> points == 0);
        pointsMap.forEach((uuid, points) -> {
            String name = nameMap.get(uuid);
            PointUser user = new PointUser(plugin, uuid, name);
            user.setBalanceRaw(points);

            plugin.getData().addUser(user);
        });

        this.plugin.info("Converted " + pointsMap.size() + " user datas from " + this.getPluginName());
    }
}
