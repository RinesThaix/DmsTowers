package sexy.kostya.towers.game;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import ru.luvas.rmcs.plugin.mg.MinigamePlugin;
import ru.luvas.rmcs.plugin.mg.game.MinigameGameHandler;
import ru.luvas.rmcs.plugin.mg.game.MinigameTeam;
import ru.luvas.rmcs.utils.UtilAlgo;
import ru.luvas.rmcs.utils.UtilChat;
import ru.luvas.rmcs.utils.UtilPlayer;
import sexy.kostya.reflexive.protos.meta.WrappedDataWatcher;
import sexy.kostya.reflexive.protos.wrapper.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by RINES on 21.12.17.
 */
public class Monster {

    @Getter
    private final int id, team;

    @Getter
    private final Location location;
    private final float speedInBlocks;
    private final int damage;
    private int health;
    private float speed;

    private int goalId;
    private Location goalPosition;

    private final WrappedDataWatcher meta = new WrappedDataWatcher();

    public Monster(int team, EntityType entityType, Location spawn, float speed, int health, int damage, ItemStack... items) {
        this.id = UtilAlgo.r(Integer.MAX_VALUE - 1000) + 500;
        this.team = team;
        this.location = spawn.clone();
        this.speedInBlocks = this.speed = speed / 20;
        this.health = health;
        this.damage = damage;
        spawn(entityType);
        if(items != null && items.length > 0)
            equip(items);
    }

    private void spawn(EntityType entityType) {
        WrappedSpawnEntityLivingOut wrapper = new WrappedSpawnEntityLivingOut();
        wrapper.setEntityId(this.id);
        wrapper.setX(this.location.getX());
        wrapper.setY(this.location.getY());
        wrapper.setZ(this.location.getZ());
        wrapper.setYaw(this.location.getYaw());
        wrapper.setHeadYaw(this.location.getYaw());
        this.meta.setObject(2, UtilChat.c("&4%d♥", this.health));
        this.meta.setObject(3, (byte) 1);
        wrapper.setEntityType(entityType);
        wrapper.setMetadata(this.meta);
        UtilPlayer.getOnlinePlayers().forEach(wrapper::sendPacket);
    }

    private void equip(ItemStack[] items) {
        Set<WrappedEntityEquipmentOut> wrappers = new HashSet<>();
        for(int i = 0; i < items.length; ++i) {
            if(items[i] == null)
                continue;
            WrappedEntityEquipmentOut wrapper = new WrappedEntityEquipmentOut();
            wrapper.setEntityId(this.id);
            wrapper.setSlot(i);
            wrapper.setItem(items[i]);
            wrappers.add(wrapper);
        }
        UtilPlayer.getOnlinePlayers().forEach(p -> wrappers.forEach(wrapper -> wrapper.sendPacket(p)));
    }

    private void despawn() {
        WrappedEntityDestroyOut wrapper = new WrappedEntityDestroyOut();
        wrapper.setEntityIds(this.id);
        UtilPlayer.getOnlinePlayers().forEach(wrapper::sendPacket);
    }

    private boolean updateGoalPosition() {
        MinigameGameHandler handler = MinigamePlugin.getInstance().getMinigame().getGame();
        Location next = handler.getMap().getPositionNullable("mob-path-" + this.team + "-" + (++this.goalId));
        if(next == null) {
            despawn();
            MinigameTeam team = handler.getTeam(this.team);
            int health = (int) team.getMeta("health");
            health -= this.damage;
            if(health > 0)
                team.setMeta("health", health);
            else
                handler.endTheGame(handler.getTeam(this.team == 1 ? 2 : 1));
            return true;
        }else {
            this.goalPosition = next;
            float[] angles = getRotationAngles(next.getX() - this.location.getX(), next.getY() - this.location.getY(), next.getZ() - this.location.getZ());
            move(this.location.getX(), this.location.getY(), this.location.getZ(), angles[0], angles[1]);
            return false;
        }
    }

    public boolean tick(boolean secondTick) {
        if(this.goalPosition == null || this.location.distanceSquared(this.goalPosition) < .25D)
            return updateGoalPosition();
        Vector to = new Vector(this.goalPosition.getX() - this.location.getX(), this.goalPosition.getY() - this.location.getY(), this.goalPosition.getZ() - this.location.getZ());
        double length = to.length();
        if(secondTick) {
            this.speed = this.speedInBlocks;
            for(int i = 0; i < ((GameHandler) MinigamePlugin.getInstance().getMinigame().getGame()).towers.values().stream()
                    .filter(t -> t.getLocation().distanceSquared(this.location) <= 100).count(); ++i)
                this.speed *= .8F;
        }
        Vector movement = to.clone().normalize().multiply(this.speed);
        if(movement.length() > length)
            movement = to;
        move(this.location.getX() + movement.getX(), this.location.getY() + movement.getY(), this.location.getZ() + movement.getZ(), this.location.getYaw(), this.location.getPitch());
        return false;
    }

    public boolean damage(int damage) {
        this.health -= damage;
        WrappedAnimationOut wrapper = new WrappedAnimationOut();
        wrapper.setEntityId(this.id);
        wrapper.setAnimation(1);
        UtilPlayer.getOnlinePlayers().forEach(wrapper::sendPacket);
        if(this.health <= 0) {
            despawn();
            return true;
        }
        this.meta.setObject(2, UtilChat.c("&4%d♥", this.health));
        WrappedEntityMetadataOut meta = new WrappedEntityMetadataOut();
        meta.setEntityId(this.id);
        meta.setMetadata(this.meta);
        UtilPlayer.getOnlinePlayers().forEach(meta::sendPacket);
        return false;
    }

    private void move(double x, double y, double z, float yaw, float pitch) {
        WrappedEntityTeleportOut wrapper = new WrappedEntityTeleportOut();
        wrapper.setEntityId(this.id);
        wrapper.setX(x);
        wrapper.setY(y);
        wrapper.setZ(z);
        wrapper.setYaw(yaw);
        wrapper.setPitch(pitch);
        this.location.setX(x);
        this.location.setY(y);
        this.location.setZ(z);
        this.location.setYaw(yaw);
        this.location.setPitch(pitch);
        UtilPlayer.getOnlinePlayers().forEach(wrapper::sendPacket);
    }

    private float[] getRotationAngles(double x, double y, double z) {
        float[] result = new float[2];
        if (x == 0D && z == 0D) {
            result[1] = y > 0D ? -90 : 90;
            return result;
        }
        double theta = Math.atan2(-x, z);
        result[0] = (float) Math.toDegrees((theta + 6.283185307179586D) % 6.283185307179586D);
        double x2 = NumberConversions.square(x), z2 = NumberConversions.square(z);
        double xz = Math.sqrt(x2 + z2);
        result[1] = (float) Math.toDegrees(Math.atan(-y / xz));
        return result;
    }

}
