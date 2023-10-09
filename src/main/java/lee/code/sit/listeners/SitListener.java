package lee.code.sit.listeners;

import lee.code.sit.lang.Lang;
import lee.code.sit.nms.Chair;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.WoolCarpetBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SitListener implements Listener {

  @EventHandler
  public void onSit(PlayerInteractEvent e) {
    if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
    if (!e.getPlayer().getInventory().getItemInMainHand().getType().isAir()) return;
    final Block block = e.getClickedBlock();
    final Player player = e.getPlayer();
    if (block == null) return;
    if (!(block.getBlockData() instanceof Stairs) && !(block.getBlockData() instanceof Slab) && !block.getType().name().endsWith("CARPET")) return;
    if (block.getBlockData() instanceof Stairs stairs && stairs.getHalf().equals(Bisected.Half.TOP)) return;
    for (Entity entity : block.getWorld().getNearbyEntities(block.getBoundingBox())) {
      if (entity instanceof Player foundPlayer) {
        final Entity vehicle = foundPlayer.getVehicle();
        if (vehicle != null && vehicle.getType().equals(EntityType.ARMOR_STAND)) {
          final Component name = vehicle.customName();
          if (name != null && name.equals(Component.text("chair"))) {
            e.setCancelled(true);
            player.sendActionBar(Lang.ERROR_CHAIR_OCCUPIED.getComponent(null));
            return;
          }
        }
      }
    }
    e.setCancelled(true);
    final Chair chair = new Chair(block);
    final ServerLevel world = ((CraftWorld) block.getWorld()).getHandle();
    world.addFreshEntity(chair);
    final Entity entityChair = chair.getBukkitEntity();
    entityChair.addPassenger(player);
  }
}
