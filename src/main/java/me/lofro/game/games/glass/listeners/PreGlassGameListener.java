package me.lofro.game.games.glass.listeners;

import me.lofro.game.games.glass.GlassGameManager;
import me.lofro.game.games.glass.enums.GlassGameState;
import me.lofro.game.global.utils.vectors.Vectors;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public record PreGlassGameListener(GlassGameManager glassGManager) implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!glassGManager.getGlassGameState().equals(GlassGameState.PRE_START)) return;

        var player = e.getPlayer();
        var location = player.getLocation();

        if (glassGManager.playerManager().isPlayer(player) && glassGManager.inArea(location)) {
            if (player.getGameMode().equals(GameMode.SPECTATOR) || player.getGameMode().equals(GameMode.CREATIVE)) return;
            Vector opposite = player.getLocation().toVector().subtract(glassGManager.getAreaCenter2D());

            player.setVelocity(opposite.normalize().multiply(Vectors.vector3Dto2D).multiply(Vectors.repulsionVelocity));
        }

    }

}
