package sexy.kostya.towers.game;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.luvas.rmcs.MainClass;
import ru.luvas.rmcs.MainScoreboard;
import ru.luvas.rmcs.api.DmsMaterials;
import ru.luvas.rmcs.plugin.GameTeamHelper;
import ru.luvas.rmcs.plugin.mg.game.MinigameGameHandler;
import ru.luvas.rmcs.plugin.mg.game.MinigamePhase;
import ru.luvas.rmcs.plugin.mg.game.MinigameTeam;
import ru.luvas.rmcs.plugin.mg.player.PlayerDataHolder;
import ru.luvas.rmcs.queues.renewed.PartySplittingQueuedArena;
import ru.luvas.rmcs.utils.Task;
import ru.luvas.rmcs.utils.UtilChat;
import ru.luvas.rmcs.utils.UtilPlayer;
import ru.luvas.rmcs.utils.UtilSideBoard;
import ru.luvas.rmcs.utils.inventory.*;
import ru.luvas.rmcs.utils.items.ActionType;
import ru.luvas.rmcs.utils.items.UsableItem;
import sexy.kostya.mineos.localizer.Localizer;
import sexy.kostya.reflexive.protos.Protos;
import sexy.kostya.reflexive.protos.meta.WrappedEntityUseAction;
import sexy.kostya.reflexive.protos.wrapper.WrappedClientUseEntityIn;
import sexy.kostya.towers.object.TowersPlayer;

import java.util.*;

/**
 * Created by RINES on 21.12.17.
 */
public class GameHandler extends MinigameGameHandler {

    final Map<Integer, Monster> monsters = new HashMap<>();
    final Map<Location, Tower> towers = new HashMap<>();

    private final ItemStack spawner = new UsableItem(new SimpleItemStack(DmsMaterials.CROSS_RUNE, "&c&lРуна призыва", Lists.newArrayList(
            "&7Позволяет призывать монстров,",
            "&7идущих против команды противника."
    )), ActionType.RIGHT) {
        @Override
        public void onUse(Player p, ActionType actionType) {
            RInventory inv = new RInventory("Призыв монстров", 5);
            TowersPlayer data = TowersPlayer.get(p);
            inv.addItem(get(MonsterType.PIG, data), 1, 1);
            inv.addItem(get(MonsterType.SHEEP, data), 1, 2);
            inv.addItem(get(MonsterType.COW, data), 1, 3);
            inv.addItem(get(MonsterType.ZOMBIE, data), 1, 4);
            inv.addItem(get(MonsterType.SKELETON, data), 1, 5);
            inv.addItem(get(MonsterType.PIG_ZOMBIE, data), 1, 6);
            inv.addItem(get(MonsterType.CREEPER, data), 1, 7);
            inv.addItem(get(MonsterType.CHICKEN, data), 1, 8);
            inv.addItem(get(MonsterType.CAVE_SPIDER, data), 1, 9);
            inv.addItem(get(MonsterType.SPIDER, data), 2, 2);
            inv.addItem(get(MonsterType.BLAZE, data), 2, 3);
            inv.addItem(get(MonsterType.IRON_GOLEM, data), 2, 4);
            inv.addItem(get(MonsterType.WOLF, data), 2, 5);
            inv.addItem(get(MonsterType.IRON_ZOMBIE, data), 2, 6);
            inv.addItem(get(MonsterType.DIAMOND_ZOMBIE, data), 2, 7);
            inv.addItem(get(MonsterType.WITHER, data), 2, 8);
            InventoryManager.openInventory(p, inv);
        }

        private RButton get(MonsterType type, TowersPlayer data) {
            List<String> description = new ArrayList<>();
            description.add("&7Цена: &f" + type.getPrice() + " изумрудов");
            description.add("&7Здоровье: &f" + type.getHealth());
            description.add("&7Урон замку: &f" + type.getDamage());
            description.add("&7Скорость: &f" + type.getSpeed());
            description.add("");
            if(type.getPrice() > data.getEmeralds()) {
                description.add("&cНедостаточно изумрудов для покупки");
                return new REmptyButton(Material.BARRIER, "&c" + type.getName(), description);
            }
            description.add("&aНажми для покупки");
            return new RButton(type.getIcon().getItemType(), type.getIcon().getData(), "&a" + type.getName(), description) {
                @Override
                public void onClick(Player p, int slot) {
                    long current = System.currentTimeMillis();
                    if(current - data.getLastUnitBought() < 3000L) {
                        UtilChat.s(p, "&cВы не можете покупать монстров чаще, чем раз в 3 секунды.");
                        return;
                    }
                    data.setLastUnitBought(current);
                    data.changeEmeralds(-type.getPrice());
                    spawnMonster(type, p);
                    UtilChat.s(p, "&a%s отправлен к замку противников!", type.getName());
                    onUse(p, ActionType.RIGHT);
                }
            };
        }

    }.getIcon();
    private final ItemStack swordUpgrader = new UsableItem(new SimpleItemStack(Material.PRISMARINE_SHARD, "&3&lЗаточка для мечей", Lists.newArrayList(
            "&7Здесь можно улучшить ваш меч"
    )), ActionType.RIGHT) {
        @Override
        public void onUse(Player p, ActionType actionType) {
            RInventory inv = new RInventory("Заточка меча", 5);
            TowersPlayer data = TowersPlayer.get(p);
            Material current = data.getSwordType();
            int price;
            Material next;
            switch(current) {
                case WOOD_SWORD: {
                    price = 300;
                    next = Material.STONE_SWORD;
                    break;
                }case STONE_SWORD: {
                    price = 600;
                    next = Material.IRON_SWORD;
                    break;
                }case IRON_SWORD: {
                    price = 1200;
                    next = Material.DIAMOND_SWORD;
                    break;
                }case DIAMOND_SWORD: {
                    price = 2400;
                    next = Material.GOLD_SWORD;
                    break;
                }case GOLD_SWORD: {
                    price = 3600;
                    next = DmsMaterials.BLOOD_SWORD;
                    break;
                }default: {
                    UtilChat.s(p, "&aВаш меч улучшен до максимального уровня!");
                    return;
                }
            }
            if(data.getEmeralds() >= price) {
                inv.addItem(new RButton(next, "&aЗаточить свой клинок", Lists.newArrayList(
                        "&7Вы можете заточить свой клинок,",
                        "&7заплатив за это &f" + price + " изумрудов&7.",
                        "&7Нажми, чтобы сделать это."
                )) {
                    @Override
                    public void onClick(Player p, int slot) {
                        data.changeEmeralds(-price);
                        data.setSwordType(next);
                        p.closeInventory();
                        p.getInventory().setItem(0, new ItemStack(next, 1));
                        UtilChat.s(p, "&aТвой меч был заточен и улучшен!");
                    }
                }, 3, 5);
            }else {
                inv.addItem(new REmptyButton(Material.BARRIER, "&cЗаточить свой клинок", Lists.newArrayList(
                        "&7К сожалению, у вас недостаточно",
                        "&7изумрудов для улучшения своего меча.",
                        "&7Стоимость заточки: &f" + price + " изумрудов&7."
                )), 3, 5);
            }
            InventoryManager.openInventory(p, inv);
        }
    }.getIcon();

    public GameHandler() {
        super(new PartySplittingQueuedArena.PartySplittingArenaConfiguration(2, Bukkit.getMaxPlayers(), PartySplittingQueuedArena.PartySplittingArenaConfiguration.Limiter.LIMIT_TEAMS_AMOUNT, 2));
        new GameListener(this);
        Task.schedule(new Runnable() {

            private int ticks = 0;

            @Override
            public void run() {
                if(getPhase() != MinigamePhase.IN_GAME) {
                    this.ticks = 0;
                    return;
                }
                if(++this.ticks == 20) {
                    this.ticks = 0;
                    getTeams().forEach(team -> {
                        int eps = (int) team.getMeta("eps");
                        team.getAlivePlayers().forEach(p -> TowersPlayer.get(p).changeEmeralds(eps));
                        if(team.getAlivePlayersAmount() == 0)
                            endTheGame(getTeam(team.getId() == 1 ? 2 : 1));
                    });
                    updateSideboards(false);
                    towers.values().forEach(Tower::tick);
                }
                Set<Integer> toBeRemoved = new HashSet<>();
                monsters.values().forEach(monster -> {
                    if (monster.tick(this.ticks == 0))
                        toBeRemoved.add(monster.getId());
                });
                toBeRemoved.forEach(monsters::remove);
            }

        }, 1L, 1L);
        Protos.addListener(WrappedClientUseEntityIn.class, (p, wrapper) -> {
            Task.schedule(() -> {
                if(wrapper.getType() != WrappedEntityUseAction.ATTACK)
                    return;
                Monster monster = this.monsters.get(wrapper.getEntityId());
                if(monster != null) {
                    ItemStack hand = p.getItemInHand();
                    int damage = 1;
                    if(hand != null) {
                        Material type = hand.getType();
                        if(type == Material.WOOD_SWORD)
                            damage = 2;
                        else if(type == Material.STONE_SWORD)
                            damage = 3;
                        else if(type == Material.IRON_SWORD)
                            damage = 4;
                        else if(type == Material.DIAMOND_SWORD)
                            damage = 5;
                        else if(type == Material.GOLD_SWORD)
                            damage = 7;
                        else if(type == DmsMaterials.BLOOD_SWORD)
                            damage = 9;
                    }
                    if(monster.damage(damage))
                        this.monsters.remove(monster.getId());
                }
            });
            return true;
        });
    }

    public void spawnMonster(MonsterType type, Player p) {
        MinigameTeam team = getDataHolder(p).getMinigameTeam();
        int t = team.getId() == 1 ? 2 : 1;
        Monster monster = type.wrap(t, getMap().getPosition("mob-path-" + t + "-0"));
        this.monsters.put(monster.getId(), monster);
    }

    public void buildTower(TowerType type, Player p, Location position) {
        Tower tower = type.wrap(this, getDataHolder(p).getMinigameTeam(), position);
        tower.rebuild();
        tower.onCreation();
        this.towers.put(position, tower);
    }

    public void destroyTower(Tower tower) {
        this.towers.remove(tower.getLocation());
        tower.destroy();
        tower.onDeletion();
    }

    public void upgradeTower(Tower tower) {
        tower.upgrade();
        tower.rebuild();
    }

    private void updateSideboards(boolean initial) {
        UtilPlayer.getOnlinePlayers().forEach(p -> {
            TowersPlayer data = TowersPlayer.get(p);
            MinigameTeam team = data.getMinigameTeam();
            if(team == null) { //spectator
                UtilSideBoard.send(p, 5, "Towers", MainScoreboard.AnimationGamma.RED, "");
                team = getTeam(1);
                UtilSideBoard.send(p, 4, team.getChatColorCode() + team.getName(), team.getMeta("health") + " здоровья");
                team = getTeam(2);
                UtilSideBoard.send(p, 3, team.getChatColorCode() + team.getName(), team.getMeta("health") + " здоровья");
                UtilSideBoard.send(p, 2, Localizer.build("SB_SERVER"), "&a" + MainClass.getServerName());
                UtilSideBoard.send(p, 1, Localizer.build("SB_WEBSITE"), "&bwww.dms.yt");
                return;
            }
            if(initial) {
                UtilSideBoard.send(p, 7, "Towers", MainScoreboard.AnimationGamma.RED, "");
                UtilSideBoard.send(p, 6, "Ваша команда:", team.getChatColorCode() + GameTeamHelper.getTeamName(team.getId()));
                UtilSideBoard.send(p, 2, Localizer.build("SB_SERVER"), "&a" + MainClass.getServerName());
                UtilSideBoard.send(p, 1, Localizer.build("SB_WEBSITE"), "&bwww.dms.yt");
            }
            UtilSideBoard.send(p, 5, team.getChatColorCode() + team.getName() + "&r:", team.getMeta("health") + " здоровья");
            MinigameTeam enemies = getTeam(team.getId() == 1 ? 2 : 1);
            UtilSideBoard.send(p, 4, enemies.getChatColorCode() + enemies.getName() + "&r:", enemies.getMeta("health") + " здоровья");
            UtilSideBoard.send(p, 3, "Изумруды:", "&2" + data.getEmeralds());
        });
    }

    @Override
    public void startPreGame() {
        getTeams().forEach(team -> {
            Location spawn = getMap().getPosition("team-spawn-" + team.getId());
            float teamHealth = 1F;
            float towersCost = 1F;
            for(Player p : team.getPlayers()) {
                p.teleport(spawn);
                UtilPlayer.resetPlayer(p);
                UtilPlayer.resetPlayerInventory(p);
                TowersPlayer data = TowersPlayer.get(p);
                Material sword = data.getPerkLevel(0) == 1 ? Material.STONE_SWORD : Material.WOOD_SWORD;
                p.getInventory().setItem(0, new ItemStack(sword, 1));
                data.setSwordType(sword);
                p.getInventory().setItem(2, this.spawner);
                p.getInventory().setItem(3, this.swordUpgrader);
                data.setEmeralds(250 + 25 * data.getPerkLevel(4));
                teamHealth += .01F * data.getPerkLevel(2);
                towersCost -= .01F * data.getPerkLevel(3);
            }
            team.setMeta("health", (int) (100 * teamHealth));
            team.setMeta("eps", 10);
            team.setMeta("towers-cost", towersCost);
        });
        updateSideboards(true);
    }

    @Override
    public void startInGame() {

    }

    @Override
    public void printEndGameStats(MinigameTeam winner) {

    }

    @Override
    public void giveAwards(MinigameTeam winner) {

    }

    @Override
    public PlayerDataHolder getDataHolder(String player) {
        return TowersPlayer.get(player);
    }

    @Override
    public void restore() {
        this.monsters.clear();
        this.towers.values().forEach(Tower::destroy);
        this.towers.clear();
    }
}
