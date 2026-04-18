package me.lofro.game.games.glass.listeners;

import me.lofro.game.SquidGame;
import me.lofro.game.games.glass.GlassGameManager;
import me.lofro.game.games.glass.enums.GlassGameState;
import me.lofro.game.global.utils.text.HexFormatter;
import me.lofro.game.global.utils.vectors.Vectors;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public record GlassGameListener(GlassGameManager glassGManager) implements Listener {

    @EventHandler
    public void onStep(PlayerMoveEvent e) {
        var player = e.getPlayer();
        var loc = player.getLocation();

        if (!glassGManager.playerManager().isPlayer(player) || player.getGameMode() != GameMode.ADVENTURE) return;
        if (!glassGManager.inArea(loc) || !glassGManager.getGlassGameState().equals(GlassGameState.RUNNING)) return;

        var relativeDown = e.getTo().getBlock().getRelative(BlockFace.DOWN);

        if (!relativeDown.getType().equals(Material.BLACK_STAINED_GLASS)) return;

        glassGManager.recursiveBreak(relativeDown, new ArrayList<>(), true, 0, glassGManager.getMaxDepth());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.hasChangedBlock()) return;

        var player = e.getPlayer();

        if (!glassGManager.playerManager().isPlayer(player)) return;

        if (!glassGManager.inArea(player.getLocation())) {
            if (player.getGameMode().equals(GameMode.SPECTATOR) || player.getGameMode().equals(GameMode.CREATIVE)) return;
            var name = player.getName();

            var squidPlayer = glassGManager.playerManager().pData().getPlayer(name);

            int winners = glassGManager.getWinners();
            int winnerLimit = glassGManager.getWinnerLimit();

            if (winners <= winnerLimit) {
                if (winners == winnerLimit) {
                    Bukkit.getScheduler().runTask(SquidGame.getInstance(), glassGManager::breakAllGlass);
                    glassGManager.stopGame();

                    Bukkit.broadcast(HexFormatter.hexFormat("&bEl jugador &3#" + squidPlayer.getId() + " " + name + " &bha encontrado la salida."));
                    Bukkit.broadcast(HexFormatter.hexFormat("&cNo quedan plazas restantes."));

                    return;
                }

                Bukkit.broadcast(HexFormatter.hexFormat("&bEl jugador &3#" + squidPlayer.getId() + " " + name + " &bha encontrado la salida."));
                Bukkit.broadcast(HexFormatter.hexFormat("&eQuedan &6" + (glassGManager.getWinners() - winners) + "&e plazas restantes."));

                glassGManager.setWinners(glassGManager().getWinners() + 1);
            }
        }
    }

}
