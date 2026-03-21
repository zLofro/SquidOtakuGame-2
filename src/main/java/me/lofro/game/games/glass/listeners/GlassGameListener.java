package me.lofro.game.games.glass.listeners;

import me.lofro.game.games.glass.GlassGameManager;
import me.lofro.game.games.glass.enums.GlassGameState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

public record GlassGameListener(GlassGameManager glassGManager) implements Listener {

    @EventHandler
    public void onStep(PlayerMoveEvent e) {
        var player = e.getPlayer();
        var loc = player.getLocation();

        if (!glassGManager.playerManager().isPlayer(player) || player.getGameMode() != GameMode.ADVENTURE) return;
        if (!glassGManager.inArea(loc) || !glassGManager.getGlassGameState().equals(GlassGameState.RUNNING)) return;

        var relativeDown = e.getTo().getBlock().getRelative(BlockFace.DOWN);

        if (!relativeDown.getType().equals(Material.RED_STAINED_GLASS)) return;

        glassGManager.recursiveBreak(relativeDown, new ArrayList<>(), true, 0, glassGManager.getMaxDepth());
    }

}
