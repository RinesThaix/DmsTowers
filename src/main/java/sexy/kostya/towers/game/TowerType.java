package sexy.kostya.towers.game;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import ru.luvas.rmcs.api.DmsMaterials;
import ru.luvas.rmcs.plugin.mg.game.MinigameTeam;
import sexy.kostya.towers.game.tower.DamageTower;
import sexy.kostya.towers.game.tower.EmeraldTower;
import sexy.kostya.towers.game.tower.KillTower;
import sexy.kostya.towers.game.tower.SlowerTower;

import java.util.List;
import java.util.function.Function;

/**
 * Created by RINES on 21.12.17.
 */
@RequiredArgsConstructor
public enum TowerType {
    EMERALD_TOWER(EmeraldTower::new, "Изумрудная башня", Material.EMERALD_BLOCK, 3, new int[]{500, 1000, 2000}, level -> Lists.newArrayList(
            "&7Увеличивает прирост изумрудов",
            "&7вашей команды на " + (level == 1 ? 2 : level == 2 ? 5 : 10) + " изумрудов",
            "&7в секунду."
    )),
    DAMAGE_TOWER(DamageTower::new, "Уничтожатус-башнятус", Material.BEDROCK, 3, new int[]{300, 900, 2700}, level -> Lists.newArrayList(
            "&7Наносит урон всем монстрам",
            "&7в радиусе " + (level == 1 ? 6 : level == 2 ? 8 : 10) + " блоков вокруг себя,",
            "&7количество урона за удар: " + (level == 1 ? 5 : level == 2 ? 10 : 15) + "."
    )),
    SLOWER_TOWER(SlowerTower::new, "Ледяная башня", Material.ICE, 3, new int[]{250, 500, 1000}, level -> Lists.newArrayList(
            "&7Замедляет всех монстров",
            "&7в радиусе 10 блоков на " + (level == 1 ? 10 : level == 2 ? 15 : 20) + "%.",
            "&7Несколько башен этого типа",
            "&7сочетаются между собой."
    )),
    KILL_TOWER(KillTower::new, "Башня смерти", DmsMaterials.HUMAN_SKULL, 3, new int[]{700, 1400, 2800}, level -> Lists.newArrayList(
            "&7Наносит 5 ед. урона всем монстрам",
            "&7в радиусе " + (level == 1 ? 6 : level == 2 ? 8 : 10) + " блоков вокруг себя,",
            "&7имея при этом " + (1 << (level - 1)) + "% шанса",
            "&7моментально убить их."
    ));

    private final TowerGenerator generator;

    @Getter
    private final String name;

    @Getter
    private final Material icon;

    @Getter
    private final int levels;
    private final int[] prices;
    private final Function<Integer, List<String>> descriptionGen;

    public Tower wrap(GameHandler handler, MinigameTeam team, Location position) {
        return this.generator.generate(handler, this, team, position);
    }

    public List<String> getDescription(int level) {
        return this.descriptionGen.apply(level);
    }

    public int getPrice(int level) {
        return this.prices[level - 1];
    }

    private interface TowerGenerator {

        Tower generate(GameHandler handler, TowerType type, MinigameTeam team, Location position);

    }

}
