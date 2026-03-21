package me.lofro.game.games.greenlight.listeners;

import lombok.Getter;
import me.lofro.game.games.greenlight.GreenLightManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;

public class GreenLightListener implements Listener {

    private final GreenLightManager gLightManager;

    private final @Getter ArrayList<Player> movedList = new ArrayList<>();

    public GreenLightListener(GreenLightManager gLightManager) {
        this.gLightManager = gLightManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        var player = e.getPlayer();
        var location = player.getLocation();

        if (gLightManager.getDeadPlayers().size() >= gLightManager.getDeathLimit()) return;

        if (gLightManager.playerManager().isPlayer(player) && gLightManager.inCube(location)) {
            if (gLightManager.isRunning()) {
                if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR) || gLightManager.playerManager().isDead(player))
                    return;
                movedList.add(player);
            } else {
                // Stop the player's moving vector if the Listener is registered, the game is not currently running and the Bukkit player has the SquidPlayer role + is inside the game region.
                if (player.getLocation().getBlockY() != player.getLocation().getY()) return;

                e.setCancelled(true);
            }
        } else if (gLightManager.playerManager().isPlayer(player) && !gLightManager.inCube(location)) {
            if (gLightManager.isRunning()) {
                if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR) || gLightManager.playerManager().isDead(player)) return;
                movedList.remove(player);
            }
        }
    }

    @EventHandler
    public void onShift(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();

        if (gLightManager.getDeadPlayers().size() > gLightManager.getDeathLimit()) return;

        if (!gLightManager.isRunning()) return;

        if (gLightManager.playerManager().isPlayer(player) && gLightManager.inCube(location)) {
            movedList.add(player);
        }
    }

}
