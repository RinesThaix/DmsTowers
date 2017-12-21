package sexy.kostya.towers.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import ru.luvas.rmcs.api.DmsMaterials;
import ru.luvas.rmcs.plugin.mg.game.MinigameTeam;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by RINES on 21.12.17.
 */
@Getter
@RequiredArgsConstructor
public abstract class Tower {

    private final GameHandler handler;
    private final TowerType type;
    private final MinigameTeam team;
    private final Location location;
    private int level = 1;

    @Setter
    private boolean underUsage;

    public void upgrade() {
        ++this.level;
        onUpgrade();
    }

    public abstract void tick();

    public abstract void onCreation();

    public abstract void onUpgrade();

    public abstract void onDeletion();

    public abstract void rebuild();

    public void destroy() {
        this.level = 0;
        int X = this.location.getBlockX(), Z = this.location.getBlockZ(), Y = this.location.getBlockY();
        World w = this.location.getWorld();
        for(int x = X - 1; x <= X + 1; ++x)
            for(int z = Z - 1; z <= Z + 1; ++z)
                for(int y = Y; y <= Y + 5; ++y)
                    w.getBlockAt(x, y, z).setType(Material.AIR);
        this.location.getBlock().setType(DmsMaterials.RUNIC_BLOCK);
    }

    public Collection<Monster> getNearbyMonsters(int radius) {
        return handler.monsters.values().stream().filter(m -> m.getLocation().distanceSquared(this.location) <= radius * radius).collect(Collectors.toSet());
    }

}
