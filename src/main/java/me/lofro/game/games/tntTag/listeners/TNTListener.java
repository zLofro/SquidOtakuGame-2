package me.lofro.game.games.tntTag.listeners;

import me.lofro.game.games.tntTag.TNTManager;
import me.lofro.game.global.utils.Sounds;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class TNTListener implements Listener {

    private final TNTManager tntManager;

    public TNTListener(TNTManager tntManager) {
        this.tntManager = tntManager;
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!tntManager.isRunning()) return;

        var entity = e.getEntity();
        var damager = e.getDamager();

        if (entity instanceof Player pEntity) {
            if (damager instanceof Player pDamager) {
                e.setDamage(0);
                if (!tntManager.playerManager().isPlayer(pEntity) || !tntManager.playerManager().isPlayer(pDamager)) return;
                if (tntManager.getTaggeds().size() <= tntManager.getTaggedLimit()) {
                    if (tntManager.isTagged(pEntity) || !tntManager.isTagged(pDamager)) return;

                    Sounds.playSoundDistance(pEntity.getLocation(), 100, Sound.BLOCK_NOTE_BLOCK_BANJO, 15f, 0f);
                    tntManager.switchTag(pDamager, pEntity);
                }
            }
        }
    }

    @EventHandler
    public void onThrowItem(PlayerDropItemEvent e) {
        var player = e.getPlayer();

        if (!tntManager.playerManager().isPlayer(player)) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        var entity = e.getEntity();

        if (entity instanceof Player player) {
            if (tntManager.playerManager().isPlayer(player)) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
                        || e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                    e.setCancelled(true);
                }
            }
        }
    }

}
