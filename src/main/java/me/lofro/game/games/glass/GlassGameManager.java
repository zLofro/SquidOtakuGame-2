package me.lofro.game.games.glass;

import lombok.Getter;
import lombok.Setter;
import me.lofro.game.games.GameManager;
import me.lofro.game.games.glass.enums.GlassGameState;
import me.lofro.game.games.glass.listeners.GlassGameListener;
import me.lofro.game.games.glass.listeners.PreGlassGameListener;
import me.lofro.game.games.glass.types.GlassGameData;
import me.lofro.game.global.utils.Locations;
import me.lofro.game.global.utils.Sounds;
import me.lofro.game.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class GlassGameManager {

    private final GameManager gManager;

    private final @Getter GlassGameListener glassGameListener;
    private final @Getter PreGlassGameListener preGlassGameListener;

    private @Getter @Setter GlassGameState glassGameState;

    private @Getter boolean isRunning = false;

    private @Getter int gameSeconds;
    private int endTaskID;
    private @Getter int maxDepth = 3;

    private final @Getter boolean breakGlass = false;

    private @Getter Location areaCenter;
    private Vector areaCenter2D;

    private final @Getter World world;

    public GlassGameManager(GameManager gManager, World world) {
        this.gManager = gManager;
        this.world = world;

        this.areaCenter = Locations.getCubeCenter(world, areaLower(), areaUpper());
        this.setAreaCenter2D(areaCenter);

        this.glassGameListener = new GlassGameListener(this);
        this.preGlassGameListener = new PreGlassGameListener(this);
    }

    public void runGame(int gameSeconds, int maxDepth) {
        if (this.isRunning)
            throw new IllegalStateException(
                    "The game " + this.getClass().getSimpleName() + " is already running.");

        this.gameSeconds = gameSeconds;
        this.maxDepth = maxDepth;
        this.isRunning = true;
        this.glassGameState = GlassGameState.RUNNING;

        gManager.getTimer().start(gameSeconds);

        gManager.getSquidInstance().unregisterListener(preGlassGameListener);
        gManager.getSquidInstance().registerListener(glassGameListener);

        this.endTaskID = Bukkit.getScheduler().runTaskLater(gManager.getSquidInstance(), this::endGame, (gameSeconds + 2) * 20L).getTaskId();
    }

    public void endGame() {
        this.gameSeconds = 0;
        this.isRunning = false;
        this.glassGameState = null;

        gManager.getSquidInstance().unregisterListener(glassGameListener);

        breakAllGlass();
    }

    public void stopGame() {
        Bukkit.getScheduler().cancelTask(endTaskID);

        this.gameSeconds = 0;
        this.isRunning = false;
        this.glassGameState = null;

        gManager.getTimer().end();
        gManager.getSquidInstance().unregisterListener(glassGameListener);
    }

    public void preGame() {
        this.glassGameState = GlassGameState.PRE_START;

        gManager.getSquidInstance().registerListener(preGlassGameListener);
    }

    public void stopPreGame() {
        this.glassGameState = null;

        gManager.getSquidInstance().unregisterListener(preGlassGameListener);
    }

    public void breakAllGlass() {
        var locations = Locations.getBlocksInsideCube(areaLower(), areaUpper());

        locations.forEach(blocks -> {
            var block = blocks.getBlock();

            if (!isGlass(block)) return;

            Sounds.playSoundDistance(block.getLocation(), 100, "sfx.glass_break", 1f, 1f);
            breakGlass(block, false);
        });
    }

    public void breakGlass(Block block, Boolean playSound) {
        if (isGlass(block)) {

            block.breakNaturally(new ItemStack(Material.AIR), true);

            if (playSound) Sounds.playSoundDistance(block.getLocation(), 100, "sfx.glass_break", 1f, 1f);
        }
    }

    public void recursiveBreak(final Block b, final List<Block> blocks, final Boolean playSound, int depth, final int maxDepth) {
        if (depth >= maxDepth) {
            return;
        }
        if (playSound) Sounds.playSoundDistance(b.getLocation(), 100, "sfx.glass_break", 1f, 1f);
        for (var f : BlockFace.values()) {
            var block = b.getRelative(f);
            if (block.getType() == Material.AIR || blocks.contains(block)) continue;
            if (isWeakGlass(block)) {
                blocks.add(block);
                block.breakNaturally(true);
                recursiveBreak(block, blocks, false, (depth + 1), maxDepth);
            }
        }
    }

    public boolean isWeakGlass(Block block) {
        return block.getType().equals(Material.RED_STAINED_GLASS);
    }

    public boolean isGlass(Block block) {
        return block.getType().name().contains("GLASS");
    }

    public PlayerManager playerManager() {
        return gManager.getSquidInstance().getPManager();
    }

    public GlassGameData glassGameData() {
        return gManager.gameData().glassGameData();
    }

    public Location areaLower() {
        return glassGameData().getAreaLower();
    }

    public Location areaUpper() {
        return glassGameData().getAreaUpper();
    }

    public boolean inArea(Location loc) {
        return Locations.isInCube(glassGameData().getAreaLower(), glassGameData().getAreaUpper(), loc);
    }

    public Vector getAreaCenter2D() {
        return this.areaCenter2D.clone();
    }

    public void setAreaCenter2D(Location location) {
        this.areaCenter = location;
        this.areaCenter2D = new Vector(location.getX(), 0, location.getZ());
    }

}
