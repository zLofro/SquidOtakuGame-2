package me.lofro.game.games.greenlight.utils.tasks;

import me.lofro.game.SquidGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import me.lofro.game.games.greenlight.GreenLightManager;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class PlayerArrayQueueShootTask implements Runnable {

    private final Queue<Player> playerQueue = new ArrayDeque<>();
    private final BukkitTask task;
    private final GreenLightManager gLightManager;
    private final boolean endGame;

    public PlayerArrayQueueShootTask(GreenLightManager gLightManager, List<Player> players, boolean endGame, int delay, int period) {
        playerQueue.addAll(players);
        this.gLightManager = gLightManager;
        this.endGame = endGame;
        this.task = Bukkit.getScheduler().runTaskTimer(gLightManager.getGManager().getSquidInstance(), this, delay, period);
    }

    @Override
    public void run() {
        if (playerQueue.isEmpty()) {
            if (endGame) SquidGame.getInstance().unregisterListener(gLightManager.getGreenLightListener());
            this.task.cancel();
        } else {
            gLightManager.shoot(playerQueue.poll());
        }
    }
}
