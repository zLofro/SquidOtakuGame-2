package me.lofro.game.games.backrooms.listeners;

import me.lofro.game.games.backrooms.BackRoomsManager;
import me.lofro.game.games.backrooms.enums.BackRoomsState;
import me.lofro.game.global.utils.vectors.Vectors;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public record PreBackRoomsListener(BackRoomsManager bRManager) implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.hasChangedPosition()) return;
        var player = e.getPlayer();

        if (bRManager.getBackRoomsState() == BackRoomsState.PRE_START) {
            if (player.getGameMode().equals(GameMode.SPECTATOR)) return;

            if (bRManager.playerManager().isPlayer(player) && bRManager.inCube(player.getLocation()) && !bRManager.isMiddleCube(player.getLocation())) {
                //Mover hacia atrás.
                Vector directorVec = bRManager.middleCubeCenter2D().subtract(player.getLocation().toVector());

                player.setVelocity(directorVec.normalize().multiply(Vectors.vector3Dto2D).multiply(Vectors.repulsionVelocity));
            }
        }
    }

}
