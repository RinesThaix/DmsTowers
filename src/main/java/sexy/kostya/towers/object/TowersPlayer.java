package sexy.kostya.towers.object;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.luvas.rmcs.plugin.mg.player.RPlayerDataHolder;
import sexy.kostya.mineos.MultiUtils;
import sexy.kostya.mineos.player.PlayerDatas;
import sexy.kostya.mineos.sql.table.ColumnType;
import sexy.kostya.mineos.sql.table.TableColumn;
import sexy.kostya.mineos.sql.table.TableConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by RINES on 21.12.17.
 */
public class TowersPlayer extends RPlayerDataHolder {

    static {
        new TableConstructor(
                "towers_data",
                new TableColumn("player_name", ColumnType.VARCHAR_16).primary(true),
                new TableColumn("silver", ColumnType.INT).defaultValue(0),
                new TableColumn("last_leave_time", ColumnType.BIG_INT).defaultValue(0),
                new TableColumn("wins", ColumnType.INT).defaultValue(0)
        ).create(MultiUtils.getGamesConnector());
    }

    public static TowersPlayer get(String player) {
        return PlayerDatas.get(TowersPlayer.class, player);
    }

    public static TowersPlayer get(Player player) {
        return get(player.getName());
    }

    @Setter
    @Getter
    private int emeralds;

    @Setter
    @Getter
    private Material swordType;

    @Setter
    @Getter
    private long lastUnitBought;

    public TowersPlayer(String owner) {
        super(owner);
    }

    @Override
    public void readData(ResultSet set) throws SQLException {

    }

    public void changeEmeralds(int delta) {
        if(delta == 0)
            return;
        if(delta > 0)
            delta = (int) (delta * (1F + .1F * getPerkLevel(1)));
        this.emeralds += delta;
    }

}
