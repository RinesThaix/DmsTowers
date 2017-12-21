package sexy.kostya.towers.game.tower;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import ru.luvas.rmcs.api.DmsMaterials;
import ru.luvas.rmcs.plugin.mg.game.MinigameTeam;
import ru.luvas.rmcs.utils.UtilAlgo;
import sexy.kostya.towers.game.GameHandler;
import sexy.kostya.towers.game.Tower;
import sexy.kostya.towers.game.TowerType;

/**
 * Created by RINES on 21.12.17.
 */
public class KillTower extends Tower {

    public KillTower(GameHandler handler, TowerType type, MinigameTeam team, Location location) {
        super(handler, type, team, location);
    }

    @Override
    public void tick() {
        getNearbyMonsters(getLevel() == 1 ? 6 : getLevel() == 2 ? 8 : 10).forEach(monster -> {
            if(!monster.damage(5) && UtilAlgo.r(100) < (1 << (getLevel() - 1)))
                monster.damage(10000);
        });
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
            block.setType(DmsMaterials.BONE_BLOCK);
            block = block.getRelative(BlockFace.UP);
        }
    }
}
