package sexy.kostya.towers.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import ru.luvas.rmcs.api.DmsMaterials;

/**
 * Created by RINES on 21.12.17.
 */
@Getter
@RequiredArgsConstructor
public enum MonsterType {
    PIG("Свинюша", new MaterialData(383, (byte) 90), EntityType.PIG, 50, 20, 2, 4, null),
    SHEEP("Овечка", new MaterialData(383, (byte) 91), EntityType.SHEEP, 75, 30, 3, 4, null),
    COW("Коровка", new MaterialData(383, (byte) 92), EntityType.COW, 100, 40, 5, 4, null),
    ZOMBIE("Зомби", new MaterialData(383, (byte) 54), EntityType.ZOMBIE, 150, 50, 7, 3, null),
    SKELETON("Скелет", new MaterialData(383, (byte) 51), EntityType.SKELETON, 200, 60, 6, 5, null),
    PIG_ZOMBIE("Свинозомби", new MaterialData(383, (byte) 57), EntityType.PIG_ZOMBIE, 350, 100, 10, 4, null),
    CREEPER("Крипер", new MaterialData(383, (byte) 50), EntityType.CREEPER, 500, 150, 15, 2, null),
    CHICKEN("Куряха", new MaterialData(383, (byte) 93), EntityType.CHICKEN, 600, 100, 10, 7, null),
    CAVE_SPIDER("Пещерный паук", new MaterialData(383, (byte) 59), EntityType.CAVE_SPIDER, 700, 120, 10, 6, null),
    SPIDER("Паукан", new MaterialData(383, (byte) 52), EntityType.SPIDER, 800, 150, 12, 4, null),
    BLAZE("Ифрит", new MaterialData(383, (byte) 61), EntityType.BLAZE, 1000, 200, 13, 4, null),
    IRON_GOLEM("Железный великан", new MaterialData(383, (byte) 68), EntityType.IRON_GOLEM, 1250, 500, 30, 1, null),
    WOLF("Волк", new MaterialData(383, (byte) 95), EntityType.WOLF, 1250, 160, 20, 7, null),
    IRON_ZOMBIE("Железный воитель", new MaterialData(Material.IRON_SWORD), EntityType.ZOMBIE, 1750, 300, 17, 4, new ItemStack[]{
            new ItemStack(Material.IRON_SWORD, 1),
            new ItemStack(Material.IRON_BOOTS, 1),
            new ItemStack(Material.IRON_LEGGINGS, 1),
            new ItemStack(Material.IRON_CHESTPLATE, 1),
            new ItemStack(Material.IRON_HELMET, 1)
    }),
    DIAMOND_ZOMBIE("Алмазный воитель", new MaterialData(Material.DIAMOND_SWORD), EntityType.ZOMBIE, 2500, 400, 25, 3, new ItemStack[]{
            new ItemStack(Material.DIAMOND_SWORD, 1),
            new ItemStack(Material.DIAMOND_BOOTS, 1),
            new ItemStack(Material.DIAMOND_LEGGINGS, 1),
            new ItemStack(Material.DIAMOND_CHESTPLATE, 1),
            new ItemStack(Material.DIAMOND_HELMET, 1)
    }),
    WITHER("Иссушитель", new MaterialData(DmsMaterials.PURPLE_BOOK), EntityType.WITHER, 6000, 1500, 50, 1, null);

    private final String name;
    private final MaterialData icon;
    private final EntityType entityType;
    private final int price;
    private final int health, damage;
    private final float speed;
    private final ItemStack[] items;

    public Monster wrap(int team, Location spawn) {
        return new Monster(team, this.entityType, spawn, this.speed, this.health, this.damage, this.items);
    }

}
