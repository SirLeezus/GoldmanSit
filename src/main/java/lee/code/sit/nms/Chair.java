package lee.code.sit.nms;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.util.Vector;

public class Chair extends ArmorStand {
  private final Block block;

  public Chair(Block block) {
    super(EntityType.ARMOR_STAND, ((CraftWorld)block.getWorld()).getHandle());
    this.block = block;
    final Location chairLocation = getChairLocation();
    setPos(chairLocation.getX(), chairLocation.getY(), chairLocation.getZ());
    setNoGravity(true);
    setInvulnerable(true);
    setSilent(true);
    setInvisible(true);
    setSmall(true);
    setCustomName(Component.literal("chair"));
    getAttribute(Attributes.MAX_HEALTH).setBaseValue(0);
    setStairDirection();
  }

  private Location getChairLocation() {
    if (block.getType().name().endsWith("CARPET")) {
      return block.getLocation().add(0.5, -0.9, 0.5);
    } else {
      return block.getLocation().add(0.5, -0.5, 0.5);
    }
  }

  private void setStairDirection() {
    if (block.getBlockData() instanceof Stairs stairs) {
      // Get the facing direction vector of the stairs
      final Vector stairsDirection = stairs.getFacing().getDirection();

      // Calculate the rotation for the entity based on stairs direction vector
      double entityYaw = Math.toDegrees(Math.atan2(-stairsDirection.getX(), stairsDirection.getZ()));

      // Add 180 degrees to make the entity face the correct direction
      entityYaw += 180.0;

      // Update the entity's rotation to match the stairs direction
      setRot((float) entityYaw, 0);
    }
  }

  @Override
  public float tickHeadTurn(float f, float f1) {
    // Remove chair if no passengers
    if (getPassengers().size() == 0) {
      remove(RemovalReason.DISCARDED);
      return 0.0F;
    }
    if (block.getBlockData() instanceof Stairs stairs) {
      if (stairs.getShape().equals(Stairs.Shape.STRAIGHT)) return 0.0F;
    }
    // Update the entity's rotation to match the player's direction
    if (getPassengers().get(0) instanceof Player player) {
      setRot(player.getYRot(), 0);
    }
    return 0.0F;
  }

  @Override
  public void load(CompoundTag compoundTag) {
  }

  @Override
  public boolean save(CompoundTag compoundTag) {
    return false;
  }
}
