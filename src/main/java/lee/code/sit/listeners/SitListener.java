package lee.code.sit.listeners;

import lee.code.sit.lang.Lang;
import lee.code.sit.nms.Chair;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;

public class SitListener implements Listener {

  @EventHandler
  public void onSit(PlayerInteractEvent e) {
    if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
    if (!e.getPlayer().getInventory().getItemInMainHand().getType().isAir()) return;
    final Block block = e.getClickedBlock();
    if (block == null) return;
    if (!(block.getBlockData() instanceof Stairs) && !(block.getBlockData() instanceof Slab) && !block.getType().name().endsWith("CARPET")) return;
    if (block.getBlockData() instanceof Stairs stairs && stairs.getHalf().equals(Bisected.Half.TOP)) return;
    if (block.getBlockData() instanceof Slab slab && slab.getType().equals(Slab.Type.DOUBLE)) return;
    e.setCancelled(true);
    if (hasSitter(block)) {
      e.getPlayer().sendActionBar(Lang.ERROR_CHAIR_OCCUPIED.getComponent(null));
      return;
    }
    final Chair chair = new Chair(block);
    final ServerLevel world = ((CraftWorld) block.getWorld()).getHandle();
    world.addFreshEntity(chair);
    final Entity entityChair = chair.getBukkitEntity();
    entityChair.addPassenger(e.getPlayer());
  }

  @EventHandler
  public void onChairBreak(BlockBreakEvent e) {
    final Block block = e.getBlock();
    if (!(block.getBlockData() instanceof Stairs) && !(block.getBlockData() instanceof Slab) && !block.getType().name().endsWith("CARPET")) return;
    if (hasSitter(block)) {
      e.setCancelled(true);
      e.getPlayer().sendActionBar(Lang.ERROR_CHAIR_OCCUPIED_BREAK.getComponent(null));
    }
  }

  @EventHandler
  public void onChairMoveExtend(BlockPistonExtendEvent e) {
    for (Block block : e.getBlocks()) {
      if (!(block.getBlockData() instanceof Stairs) && !(block.getBlockData() instanceof Slab) && !block.getType().name().endsWith("CARPET")) continue;
      if (hasSitter(block)) {
        e.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler
  public void onChairMoveRetract(BlockPistonRetractEvent e) {
    for (Block block : e.getBlocks()) {
      if (!(block.getBlockData() instanceof Stairs) && !(block.getBlockData() instanceof Slab) && !block.getType().name().endsWith("CARPET")) continue;
      if (hasSitter(block)) {
        e.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler
  public void onChairBurn(BlockBurnEvent e) {
    final Block block = e.getBlock();
    if (!(block.getBlockData() instanceof Stairs) && !(block.getBlockData() instanceof Slab) && !block.getType().name().endsWith("CARPET")) return;
    if (hasSitter(block)) e.setCancelled(true);
  }

  private boolean hasSitter(Block block) {
    for (Entity entity : block.getWorld().getNearbyEntities(block.getBoundingBox())) {
      if (entity instanceof Player foundPlayer) {
        final Entity vehicle = foundPlayer.getVehicle();
        if (vehicle != null && vehicle.getType().equals(EntityType.ARMOR_STAND)) {
          final Component name = vehicle.customName();
          if (name != null && name.equals(Component.text("chair"))) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
