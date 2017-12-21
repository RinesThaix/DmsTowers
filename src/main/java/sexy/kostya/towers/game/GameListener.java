package sexy.kostya.towers.game;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import ru.luvas.rmcs.api.DmsMaterials;
import ru.luvas.rmcs.plugin.mg.game.MinigamePhase;
import ru.luvas.rmcs.utils.RListener;
import ru.luvas.rmcs.utils.UtilChat;
import ru.luvas.rmcs.utils.inventory.InventoryManager;
import ru.luvas.rmcs.utils.inventory.RButton;
import ru.luvas.rmcs.utils.inventory.REmptyButton;
import ru.luvas.rmcs.utils.inventory.RInventory;
import sexy.kostya.towers.object.TowersPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RINES on 21.12.17.
 */
@RequiredArgsConstructor
public class GameListener extends RListener {

    private final GameHandler handler;

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER)
            e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(this.handler.getPhase() != MinigamePhase.IN_GAME) {
            e.setCancelled(true);
            return;
        }
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == DmsMaterials.RUNIC_BLOCK) {
            e.setCancelled(true);
            Location loc = e.getClickedBlock().getLocation();
            Tower tower = this.handler.towers.get(loc);
            Player p = e.getPlayer();
            if(tower == null) {
                TowersPlayer data = TowersPlayer.get(p);
                float costModifier = (float) data.getMinigameTeam().getMeta("towers-cost");
                RInventory inv = new RInventory("Строительство башни", 1);
                for(TowerType type : TowerType.values()) {
                    int price = (int) (type.getPrice(1) * costModifier);
                    if(data.getEmeralds() >= price) {
                        List<String> desc = new ArrayList<>();
                        desc.add("&7Цена покупки: &f" + price + " изумрудов&7.");
                        desc.add("");
                        desc.addAll(type.getDescription(1));
                        inv.addItem(new RButton(type.getIcon(), "&a" + type.getName(), desc) {
                            @Override
                            public void onClick(Player p, int slot) {
                                p.closeInventory();
                                if(handler.towers.get(loc) != null) {
                                    UtilChat.s(p, "&cКто-то из вашей команды уже успел построить здесь башню!");
                                    return;
                                }
                                data.changeEmeralds(-price);
                                handler.buildTower(type, p, loc);
                                UtilChat.s(p, "&aВы построили новую башню!");
                            }
                        });
                    }else {
                        List<String> desc = new ArrayList<>();
                        desc.add("&7Цена покупки: &c" + price + " изумрудов&7.");
                        desc.add("");
                        desc.addAll(type.getDescription(1));
                        inv.addItem(new REmptyButton(Material.BARRIER, "&c" + type.getName(), desc));
                    }
                }
                InventoryManager.openInventory(p, inv);
            }else {
                if(tower.isUnderUsage()) {
                    UtilChat.s(p, "&cВ данный момент кто-то уже управляет этой башней.");
                    return;
                }
                tower.setUnderUsage(true);
                RInventory inv = new RInventory("Управление башней", 1) {

                    @Override
                    public void onClose(Player p) {
                        tower.setUnderUsage(false);
                    }

                };
                inv.addItem(new REmptyButton(tower.getType().getIcon(), "&a" + tower.getType().getName() + ": Ур. " + tower.getLevel(), tower.getType().getDescription(tower.getLevel())), 1, 5);
                inv.addItem(new RButton(Material.BARRIER, "&cУдалить башню", Lists.newArrayList(
                        "&7Вы ничего не получите за это",
                        "&7дейстие и это действие нельзя",
                        "&7отменить никаким образом!"
                )) {
                    @Override
                    public void onClick(Player p, int slot) {
                        p.closeInventory();
                        handler.destroyTower(tower);
                    }
                }, 1, 7);
                float costModifier = (float) tower.getTeam().getMeta("towers-cost");
                if(tower.getLevel() == tower.getType().getLevels())
                    inv.addItem(new REmptyButton(Material.EMPTY_MAP, "&aМаксимально улучшена", Lists.newArrayList(
                            "&7Эта башня улучшена до",
                            "&7максимального уровня."
                    )), 1, 3);
                else {
                    List<String> desc = new ArrayList<>();
                    int price = (int) (tower.getType().getPrice(tower.getLevel() + 1) * costModifier);
                    desc.add("&7Цена улучшения: &f" + price + " изумрудов&7.");
                    desc.add("");
                    desc.addAll(tower.getType().getDescription(tower.getLevel() + 1));
                    inv.addItem(new RButton(Material.PAPER, "&aПовысить уровень башни", desc) {
                        @Override
                        public void onClick(Player p, int slot) {
                            TowersPlayer data = TowersPlayer.get(p);
                            if(data.getEmeralds() >= price) {
                                p.closeInventory();
                                data.changeEmeralds(-price);
                                handler.upgradeTower(tower);
                                UtilChat.s(p, "&aВы успешно улучшили башню!");
                            }else {
                                UtilChat.s(p, "&cУ вас недостаточно изумрудов для улучшения этой башни!");
                            }
                        }
                    }, 1, 3);
                }
                InventoryManager.openInventory(p, inv);
            }
        }
    }

}
