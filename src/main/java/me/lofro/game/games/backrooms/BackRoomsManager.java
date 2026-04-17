package me.lofro.game.games.backrooms;

import lombok.Getter;
import me.lofro.game.games.GameManager;
import me.lofro.game.games.backrooms.enums.BackRoomsState;
import me.lofro.game.games.backrooms.events.BackRoomsChangeStateEvent;
import me.lofro.game.games.backrooms.listeners.BackRoomsListener;
import me.lofro.game.games.backrooms.listeners.PreBackRoomsListener;
import me.lofro.game.games.backrooms.types.BackRoomsData;
import me.lofro.game.global.utils.Locations;
import me.lofro.game.global.utils.text.HexFormatter;
import me.lofro.game.players.PlayerManager;
import me.lofro.game.players.objects.SquidPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class BackRoomsManager {

    private final GameManager gManager;

    private final @Getter BackRoomsListener backRoomsListener;
    private final @Getter PreBackRoomsListener preBackRoomsListener;

    private final @Getter HashMap<String, SquidPlayer> winners = new HashMap<>();

    private @Getter BackRoomsState backRoomsState;

    private @Getter boolean isRunning = false;

    private @Getter int winnerLimit;

    private int changeStateLaterTaskID;

    private final @Getter World world;

    private Vector middleCubeCenter2D;
    private @Getter Location middleCubeCenter;

    private Vector cubeCenter2D;
    private @Getter Location cubeCenter;

    private final @Getter Component gamePrefix = HexFormatter.hexFormat("&6&lBack&e&lRooms &7>> &r");

    public BackRoomsManager(GameManager gameManager, World world) {
        this.gManager = gameManager;
        this.world = world;
        this.cubeCenter = Locations.getCubeCenter(world, cubeLower(), cubeUpper());
        this.middleCubeCenter = Locations.getCubeCenter(world, middleCubeLower(), middleCubeUpper());
        setCubeCenter2D(cubeCenter);
        setMiddleCubeCenter(middleCubeCenter);
        this.preBackRoomsListener = new PreBackRoomsListener(this);
        this.backRoomsListener = new BackRoomsListener(this);
    }

    public void preGame() {
        changeState(BackRoomsState.PRE_START);

        gManager.getSquidInstance().registerListener(preBackRoomsListener);
    }

    public void stopPreGame() {
        this.backRoomsState = null;

        gManager.getSquidInstance().unregisterListener(preBackRoomsListener);
    }

    public void runGame(int safeSeconds, int winnerLimit) {
        if (this.isRunning)
            throw new IllegalStateException(
                    "The game " + this.getClass().getSimpleName() + " is already running.");

        this.winnerLimit = winnerLimit;
        this.isRunning = true;

        gManager.getSquidInstance().unregisterListener(preBackRoomsListener);
        gManager.getSquidInstance().registerListener(backRoomsListener);

        changeState(BackRoomsState.SAFE);

        gManager.getTimer().start(safeSeconds);

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.playSound(p.getLocation(), "zelda", 0.5f, 1f);

            if (!playerManager().isPlayer(p) || p.getGameMode().equals(GameMode.SPECTATOR) || p.getGameMode().equals(GameMode.CREATIVE)) return;

            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, false, false));
        });

        this.changeStateLaterTaskID = Bukkit.getScheduler().runTaskLater
                (gManager.getSquidInstance(), () -> {
                    changeState(BackRoomsState.HUNTING);
                }, (safeSeconds + 2) * 20L).getTaskId();
    }

    public void stopGame() {
        Bukkit.getScheduler().cancelTask(changeStateLaterTaskID);

        this.isRunning = false;

        changeState(BackRoomsState.NONE);

        gManager.getTimer().end();

        this.winners.clear();
        backRoomsListener.getLosers().clear();

        Bukkit.getOnlinePlayers().forEach(p -> p.removePotionEffect(PotionEffectType.BLINDNESS));

        gManager.getSquidInstance().unregisterListener(backRoomsListener);
    }

    public void killLosers() {
        var losers = backRoomsListener.getLosers();

        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), "zelda2", 1, 1));

        losers.values().forEach(p -> p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 1, false, false)));
    }

    public void endGame() {
        changeState(BackRoomsState.NONE);
        this.winners.clear();
        backRoomsListener.getLosers().clear();
        this.isRunning = false;
        gameManager().getSquidInstance().unregisterListener(backRoomsListener);
    }

    public void changeState(BackRoomsState backRoomsState) {
        this.backRoomsState = backRoomsState;
        Bukkit.getPluginManager().callEvent(new BackRoomsChangeStateEvent(backRoomsState));
    }

    public GameManager gameManager() {
        return gManager;
    }

    public PlayerManager playerManager() {
        return gManager.getSquidInstance().getPManager();
    }

    public BackRoomsData backRoomsData() {
        return gManager.gameData().backRoomsData();
    }

    public Location cubeLower() {
        return backRoomsData().getCubeLower();
    }

    public Location cubeUpper() {
        return backRoomsData().getCubeUpper();
    }

    public Location middleCubeLower() {
        return backRoomsData().getMiddleCubeLower();
    }

    public Location middleCubeUpper() {
        return backRoomsData().getMiddleCubeUpper();
    }

    public boolean inCube(Location location) {
        return Locations.isInCube(backRoomsData().getCubeLower(), backRoomsData().getCubeUpper(), location);
    }

    public boolean isMiddleCube(Location location) {
        return Locations.isInCube(middleCubeLower(), middleCubeUpper(), location);
    }

    public Vector cubeCenter2D() {
        return this.cubeCenter2D.clone();
    }

    public void setCubeCenter2D(Location location) {
        this.cubeCenter = location;
        this.cubeCenter2D = new Vector(location.getX(), 0, location.getZ());
    }

    public void setMiddleCubeCenter(Location location) {
        this.middleCubeCenter = location;
        this.middleCubeCenter2D = new Vector(location.getX(), 0, location.getZ());
    }

    public Vector middleCubeCenter2D() {
        return this.middleCubeCenter2D.clone();
    }

}
