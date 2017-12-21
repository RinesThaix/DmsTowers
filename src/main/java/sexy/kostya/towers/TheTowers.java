package sexy.kostya.towers;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import ru.luvas.rmcs.api.DmsMaterials;
import ru.luvas.rmcs.plugin.mg.MinigamePlugin;
import ru.luvas.rmcs.plugin.mg.game.MinigameGameHandler;
import ru.luvas.rmcs.plugin.mg.game.additional.MinigamePerk;
import ru.luvas.rmcs.plugin.mg.game.additional.MinigamePerkManager;
import ru.luvas.rmcs.plugin.mg.lobby.MinigameLobbyHandler;
import ru.luvas.rmcs.plugin.mg.player.PlayerDataHolder;
import sexy.kostya.mineos.player.PlayerDatas;
import sexy.kostya.mineos.queue.QueueGamemode;
import sexy.kostya.towers.game.GameHandler;
import sexy.kostya.towers.object.TowersPlayer;

import java.util.List;

/**
 * Created by RINES on 21.12.17.
 */
public class TheTowers extends MinigamePlugin {
    @Override
    public boolean limitPlayersPerTeam() {
        return false;
    }

    @Override
    public QueueGamemode getGamemode() {
        return QueueGamemode.TOWERS;
    }

    @Override
    public String getPrefix() {
        return "&c&lTowers";
    }

    @Override
    public MinigameGameHandler generateGameHandler() {
        return new GameHandler().addPerks(generatePerks());
    }

    @Override
    public MinigameLobbyHandler generateLobbyHandler() {
        return new MinigameLobbyHandler() {
            @Override
            public PlayerDataHolder getDataHolder(String player) {
                return TowersPlayer.get(player);
            }
        }.addPerks(generatePerks());
    }

    @Override
    public void enabling() {
        PlayerDatas.register(TowersPlayer.class);
        TowersPlayer.setup(QueueGamemode.TOWERS, true, false);
    }

    @Override
    public void disabling() {

    }

    private MinigamePerkManager generatePerks() {
        return new MinigamePerkManager(
                new MinigamePerk( //0
                        1,
                        50000
                ).icon(Material.STONE_SWORD, "Кузнечество", Lists.<List<String>>newArrayList(
                        Lists.newArrayList(
                                "&7Начните свою игру с каменным,",
                                "&7но не деревянным мечом!"
                        )
                )),
                new MinigamePerk( //1
                        5,
                        5000, 10000, 15000, 20000, 25000
                ).icon(Material.EMERALD, "Мастер над изумрудом", Lists.<List<String>>newArrayList(
                        Lists.newArrayList(
                                "&7Вы получаете на 10% больше",
                                "&7изумрудов за любые действия в игре."
                        ),
                        Lists.newArrayList(
                                "&7Вы получаете на 20% больше",
                                "&7изумрудов за любые действия в игре."
                        ),
                        Lists.newArrayList(
                                "&7Вы получаете на 30% больше",
                                "&7изумрудов за любые действия в игре."
                        ),
                        Lists.newArrayList(
                                "&7Вы получаете на 40% больше",
                                "&7изумрудов за любые действия в игре."
                        ),
                        Lists.newArrayList(
                                "&7Вы получаете на 50% больше",
                                "&7изумрудов за любые действия в игре."
                        )
                )),
                new MinigamePerk( //2
                        3,
                        25000, 50000, 75000
                ).icon(DmsMaterials.BLOOD_SHARD, "Заклинатель замков", Lists.<List<String>>newArrayList(
                        Lists.newArrayList(
                                "&7Первоначальное здоровье вашего",
                                "&7замка увеличено на 1%.",
                                "&7Этот эффект суммируется, но не",
                                "&7перемножается с подобными эффектами",
                                "&7у других игроков вашей команды."
                        ),
                        Lists.newArrayList(
                                "&7Первоначальное здоровье вашего",
                                "&7замка увеличено на 2%.",
                                "&7Этот эффект суммируется, но не",
                                "&7перемножается с подобными эффектами",
                                "&7у других игроков вашей команды."
                        ),
                        Lists.newArrayList(
                                "&7Первоначальное здоровье вашего",
                                "&7замка увеличено на 3%.",
                                "&7Этот эффект суммируется, но не",
                                "&7перемножается с подобными эффектами",
                                "&7у других игроков вашей команды."
                        )
                )),
                new MinigamePerk( //3
                        3,
                        25000, 50000, 75000
                ).icon(Material.WOOD, "Знатный архитектор", Lists.<List<String>>newArrayList(
                        Lists.newArrayList(
                                "&7Стоимость строительства и улучшения",
                                "&7башен для вашей команды снижена",
                                "&7на 1%. Этот эффект суммируется, но не",
                                "&7перемножается с подобными эффектами",
                                "&7у других игроков вашей команды."
                        ),
                        Lists.newArrayList(
                                "&7Стоимость строительства и улучшения",
                                "&7башен для вашей команды снижена",
                                "&7на 2%. Этот эффект суммируется, но не",
                                "&7перемножается с подобными эффектами",
                                "&7у других игроков вашей команды."
                        ),
                        Lists.newArrayList(
                                "&7Стоимость строительства и улучшения",
                                "&7башен для вашей команды снижена",
                                "&7на 3%. Этот эффект суммируется, но не",
                                "&7перемножается с подобными эффектами",
                                "&7у других игроков вашей команды."
                        )
                )),
                new MinigamePerk( //4
                        4,
                        10000, 20000, 30000, 40000
                ).icon(DmsMaterials.SATCHEL, "Казначей", Lists.<List<String>>newArrayList(
                        Lists.newArrayList(
                                "&7Вы начинаете игру с 275 изумрудами",
                                "&7вместо стандартных 250."
                        ),
                        Lists.newArrayList(
                                "&7Вы начинаете игру с 300 изумрудами",
                                "&7вместо стандартных 250."
                        ),
                        Lists.newArrayList(
                                "&7Вы начинаете игру с 325 изумрудами",
                                "&7вместо стандартных 250."
                        ),
                        Lists.newArrayList(
                                "&7Вы начинаете игру с 350 изумрудами",
                                "&7вместо стандартных 250."
                        )
                ))
        );
    }
}
