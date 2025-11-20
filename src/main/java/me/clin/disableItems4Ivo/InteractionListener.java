package me.clin.disableItems4Ivo;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionListener implements Listener {
    private static final String BYPASS_PERMISSION = "disableitems4ivo.bypass";
    private static final String BLOCKED_MESSAGE = ChatColor.RED + "You are not allowed to do that.";

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (player.hasPermission(BYPASS_PERMISSION)) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Material type = block.getType();
        if (isBlockedBlock(type)) {
            event.setCancelled(true);
            player.sendMessage(BLOCKED_MESSAGE);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(BYPASS_PERMISSION)) {
            return;
        }

        Entity entity = event.getRightClicked();
        if (isProtectedEntity(entity.getType())) {
            event.setCancelled(true);
            player.sendMessage(BLOCKED_MESSAGE);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemFrameDamage(EntityDamageByEntityEvent event) {
        if (!isProtectedEntity(event.getEntityType())) {
            return;
        }

        Player attacker = getResponsiblePlayer(event.getDamager());
        if (attacker == null || attacker.hasPermission(BYPASS_PERMISSION)) {
            return;
        }

        event.setCancelled(true);
        attacker.sendMessage(BLOCKED_MESSAGE);
    }

    private boolean isBlockedBlock(Material material) {
        if (material == null) {
            return false;
        }

        if (material.name().endsWith("_TRAPDOOR")) {
            return true;
        }

        if (material == Material.FLOWER_POT || material.name().startsWith("POTTED_")) {
            return true;
        }

        return material == Material.CHEST
                || material == Material.TRAPPED_CHEST
                || material == Material.BARREL
                || material == Material.DROPPER
                || material == Material.HOPPER
                || material == Material.DECORATED_POT;
    }

    private boolean isProtectedEntity(EntityType type) {
        return type == EntityType.ITEM_FRAME
                || type == EntityType.GLOW_ITEM_FRAME
                || type == EntityType.ARMOR_STAND;
    }

    private Player getResponsiblePlayer(Entity entity) {
        if (entity instanceof Player player) {
            return player;
        }

        if (entity instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                return shooter;
            }
        }

        return null;
    }
}
