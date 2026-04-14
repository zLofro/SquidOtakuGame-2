package me.lofro.game.games.applepick.listeners;

import me.lofro.game.games.applepick.ApplePickManager;
import me.lofro.game.global.utils.Sounds;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class AppleListener implements Listener {

    private final ApplePickManager appleManager;

    public AppleListener(ApplePickManager appleManager) {
        this.appleManager = appleManager;
    }

    /**
     * When a player WITHOUT the apple hits a player WITH the apple,
     * the apple is transferred: the attacker becomes safe, the victim loses it.
     */
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!appleManager.isRunning()) return;

        var entity = e.getEntity();
        var damager = e.getDamager();

        if (!(entity instanceof Player pEntity) || !(damager instanceof Player pDamager)) return;

        // Cancel all PvP damage — hits are just for apple-stealing
        e.setDamage(0);

        if (!appleManager.playerManager().isPlayer(pEntity)
                || !appleManager.playerManager().isPlayer(pDamager)) return;

        // Only act if the victim has the apple and the attacker does NOT
        if (!appleManager.isSafe(pEntity) || appleManager.isSafe(pDamager)) return;

        // Steal the apple: attacker takes it from the victim
        Sounds.playSoundDistance(pDamager.getLocation(), 100, Sound.ENTITY_ITEM_PICKUP, 15f, 1.5f);
        appleManager.switchApple(pEntity, pDamager);
    }

    /** Prevent players from dropping the apple. */
    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if (!appleManager.isRunning()) return;

        var player = e.getPlayer();

        if (!appleManager.playerManager().isPlayer(player)) return;

        if (e.getItemDrop().getItemStack().getType() == appleManager.getAppleItem().getType()) e.setCancelled(true);
    }
}
