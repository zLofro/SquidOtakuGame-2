package me.lofro.game.games.smashbros;

import lombok.Getter;
import lombok.Setter;
import me.lofro.game.SquidGame;
import me.lofro.game.games.GameManager;
import me.lofro.game.games.smashbros.listeners.SmashBrosListener;
import me.lofro.game.global.enums.PvPState;
import me.lofro.game.players.PlayerManager;
import org.bukkit.Bukkit;

public class SmashBrosManager {

    private final GameManager gameManager;

    private @Getter @Setter boolean running;

    private final SmashBrosListener listener;

    private @Getter int deathsToWin;

    private @Getter int deaths;

    public SmashBrosManager(final GameManager gameManager) {
        this.gameManager = gameManager;
        this.running = false;
        this.listener = new SmashBrosListener(this);
    }

    public void start(int deathsToWin) {
        running = true;
        this.deathsToWin = deathsToWin;
        deaths = 0;
        SquidGame.getInstance().getGManager().gameData().setPvPState(PvPState.ALL);
        gameManager.getSquidInstance().registerListener(listener);

        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), "smash1", 0.5f, 1));

        //TODO EFECTOS Y MUSICA
    }

    public void stop() {
        running = false;
        SquidGame.getInstance().getGManager().gameData().setPvPState(PvPState.ONLY_GUARDS);
        gameManager.getSquidInstance().unregisterListener(listener);
    }

    public void endGame() {
        running = false;
        SquidGame.getInstance().getGManager().gameData().setPvPState(PvPState.ONLY_GUARDS);
        gameManager.getSquidInstance().unregisterListener(listener);

        //TODO EFECTOS
    }

    public void incrementDeaths() {deaths++;}

    public PlayerManager playerManager() {
        return gameManager.getSquidInstance().getPManager();
    }

}
