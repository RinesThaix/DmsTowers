package sexy.kostya.towers.game.tower;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import ru.luvas.rmcs.plugin.mg.game.MinigameTeam;
import sexy.kostya.towers.game.GameHandler;
import sexy.kostya.towers.game.Tower;
import sexy.kostya.towers.game.TowerType;

/**
 * Created by RINES on 21.12.17.
 */
public class SlowerTower extends Tower {

    public SlowerTower(GameHandler handler, TowerType type, MinigameTeam team, Location location) {
        super(handler, type, team, location);
    }

    @Override
    public void tick() {

    }

    @Override
    public void onCreation() {

    }

    @Override
    public void onUpgrade() {

    }

    @Override
    public void onDeletion() {

    }

    @Override
    public void rebuild() {
        Block block = getLocation().getBlock().getRelative(BlockFace.UP);
        for(int i = 0; i < getLevel(); ++i) {
            block.setType(Material.PACKED_ICE);
            block = block.getRelative(BlockFace.UP);
        }
    }
}
