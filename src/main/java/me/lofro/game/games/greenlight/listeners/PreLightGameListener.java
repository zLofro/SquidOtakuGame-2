package me.lofro.game.games.greenlight.listeners;

import me.lofro.game.global.utils.vectors.Vectors;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import me.lofro.game.games.greenlight.GreenLightManager;
import me.lofro.game.games.greenlight.enums.LightState;

public record PreLightGameListener(GreenLightManager gLightManager) implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.hasChangedPosition()) return;
        if (!gLightManager.getLightState().equals(LightState.PRE_START)) return;

        var player = e.getPlayer();
        var location = player.getLocation();

        if (gLightManager.playerManager().isPlayer(player) && gLightManager.inCube(location)) {
            if (player.getGameMode().equals(GameMode.SPECTATOR) || player.getGameMode().equals(GameMode.CREATIVE))
                return;
            Vector opposite = player.getLocation().toVector().subtract(gLightManager.getCubeCenter2D());

            player.setVelocity(opposite.normalize().multiply(Vectors.vector3Dto2D).multiply(Vectors.repulsionVelocity));
        }
    }

}
