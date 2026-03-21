package me.lofro.game.games.backrooms.listeners;

import lombok.Getter;
import me.lofro.game.games.backrooms.BackRoomsManager;
import me.lofro.game.games.backrooms.enums.BackRoomsState;
import me.lofro.game.games.backrooms.events.BackRoomsChangeStateEvent;
import me.lofro.game.global.enums.PvPState;
import me.lofro.game.global.utils.text.HexFormatter;
import me.lofro.game.global.utils.vectors.Vectors;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;


public class BackRoomsListener implements Listener {

    private final BackRoomsManager bRManager;

    private @Getter final HashMap<String, Player> losers = new HashMap<>();

    boolean limit = false;

    public BackRoomsListener(BackRoomsManager bRManager) {
        this.bRManager = bRManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.hasChangedBlock()) return;

        var player = e.getPlayer();

        if (!bRManager.playerManager().isPlayer(player)) return;

        if (!bRManager.inCube(player.getLocation())) {
            if (player.getGameMode().equals(GameMode.SPECTATOR) || player.getGameMode().equals(GameMode.CREATIVE)) return;
            var name = player.getName();

            var squidPlayer = bRManager.playerManager().pData().getPlayer(name);

            var winnerList = bRManager.getWinners();
            var winnerLimit = bRManager.getWinnerLimit();

            if (winnerList.size() < winnerLimit) {
                if (winnerList.containsKey(name)) return;

                winnerList.put(name, squidPlayer);
                player.removePotionEffect(PotionEffectType.BLINDNESS);

                int winners = winnerList.size();

                if (winners == winnerLimit) {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if (p.getGameMode().equals(GameMode.SPECTATOR) || p.getGameMode().equals(GameMode.CREATIVE)) return;
                        if (bRManager.playerManager().isPlayer(p) && !winnerList.containsKey(p.getName()) && bRManager.inCube(p.getLocation())) {
                            losers.put(p.getName(), p);
                        }
                    });
                    this.limit = true;
                    Bukkit.getScheduler().runTaskLater(bRManager.gameManager().getSquidInstance(), bRManager::killLosers, 5 * 10);

                    if (losers.isEmpty()) bRManager.endGame();

                    Bukkit.broadcast(HexFormatter.hexFormat("&bEl jugador &3#" + squidPlayer.getId() + " " + name + " &bha encontrado la salida."));
                    Bukkit.broadcast(HexFormatter.hexFormat("&cNo quedan plazas restantes."));

                    return;
                }

                Bukkit.broadcast(HexFormatter.hexFormat("&bEl jugador &3#" + squidPlayer.getId() + " " + name + " &bha encontrado la salida."));
                Bukkit.broadcast(HexFormatter.hexFormat("&eQuedan &6" + (bRManager.getWinnerLimit() - winners) + "&e plazas restantes."));
            } else {
                if (winnerList.containsKey(name)) return;

                Vector opposite = bRManager.cubeCenter2D().subtract(player.getLocation().toVector());

                player.setVelocity(opposite.normalize().multiply(Vectors.vector3Dto2D).multiply(Vectors.repulsionVelocity));
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        var player = e.getPlayer();
        var name = player.getName();

        if (!limit) return;

        losers.remove(name);
        if (losers.isEmpty()) {
            bRManager.endGame();
        }
    }

    @EventHandler
    public void onBackRoomsStateChange(BackRoomsChangeStateEvent e) {
        var state = e.getBackRoomsState();

        if (state == BackRoomsState.PRE_START || state == BackRoomsState.SAFE) {
            bRManager.gameManager().gameData().setPvPState(PvPState.NONE);
        } else if (state == BackRoomsState.NONE) {
            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(p -> bRManager.getWinners().containsKey(p.getName()) || bRManager.playerManager().isPlayer(p)
                            && bRManager.playerManager().isDead(p))
                    .forEach(p -> p.removePotionEffect(PotionEffectType.BLINDNESS));

            bRManager.gameManager().gameData().setPvPState(PvPState.ONLY_GUARDS);
        } else {
            bRManager.gameManager().gameData().setPvPState(PvPState.ONLY_GUARDS);
        }
    }

}
