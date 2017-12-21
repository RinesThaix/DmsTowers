package sexy.kostya.towers.game.tower;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import ru.luvas.rmcs.plugin.mg.game.MinigameTeam;
import sexy.kostya.towers.game.GameHandler;
import sexy.kostya.towers.game.Tower;
import sexy.kostya.towers.game.TowerType;

import java.util.List;

/**
 * Created by RINES on 21.12.17.
 */
public class EmeraldTower extends Tower {

    public EmeraldTower(GameHandler handler, TowerType type, MinigameTeam team, Location location) {
        super(handler, type, team, location);
    }

    @Override
    public void tick() {
        //Nothing is here
    }

    @Override
    public void onCreation() {
        getTeam().setMeta("eps", (int) getTeam().getMeta("eps") + 2);
    }

    @Override
    public void onUpgrade() {
        getTeam().setMeta("eps", (int) getTeam().getMeta("eps") + (getLevel() == 2 ? 3 : 5));
    }

    @Override
    public void onDeletion() {
        int before = (int) getTeam().getMeta("eps");
        getTeam().setMeta("eps", before - (getLevel() == 1 ? 2 : getLevel() == 2 ? 5 : 10));
    }

    @Override
    public void rebuild() {
        Block block = getLocation().getBlock().getRelative(BlockFace.UP);
        for(int i = 0; i < getLevel(); ++i) {
            block.setType(Material.EMERALD_BLOCK);
            block = block.getRelative(BlockFace.UP);
        }
    }
}
