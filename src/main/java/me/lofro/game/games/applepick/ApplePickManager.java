package me.lofro.game.games.applepick;

import lombok.Getter;
import me.lofro.game.games.GameManager;
import me.lofro.game.games.applepick.listeners.AppleListener;
import me.lofro.game.global.enums.PvPState;
import me.lofro.game.global.item.CustomItems;
import me.lofro.game.global.utils.Sounds;
import me.lofro.game.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ApplePickManager {

    private final @Getter GameManager gManager;

    private final @Getter AppleListener appleListener;

    private @Getter boolean isRunning = false;

    private @Getter int safeLimit;
    private int taskLaterID;

    /**
     * Players who currently HOLD the apple (safe players).
     * At the end of the round, everyone WITHOUT the apple dies.
     */
    private final @Getter List<Player> safePlayers = new ArrayList<>();

    public ApplePickManager(GameManager gManager) {
        this.gManager = gManager;
        this.appleListener = new AppleListener(this);
    }

    public void runGame(int seconds, int safeLimit) {
        if (this.isRunning)
            throw new IllegalStateException(
                    "The game " + this.getClass().getSimpleName() + " is already running.");

        this.isRunning = true;
        this.safeLimit = safeLimit;

        gManager.getTimer().start(seconds);
        gManager.getSquidInstance().registerListener(appleListener);
        gManager.gameData().setPvPState(PvPState.ALL);

        // Give the apple to a random set of players at the start
        randomGiveApple(safeLimit);

        this.taskLaterID = Bukkit.getScheduler()
                .runTaskLater(gManager.getSquidInstance(), this::endGame, (seconds + 2) * 20L)
                .getTaskId();
    }

    public void endGame() {
        this.isRunning = false;

        killUnsafe();

        this.safePlayers.clear();

        gManager.gameData().setPvPState(PvPState.ONLY_GUARDS);
        gManager.getSquidInstance().unregisterListener(appleListener);
    }

    public void stopGame() {
        safePlayers.forEach(p -> removeApple(p, true));
        Bukkit.getOnlinePlayers().forEach(p -> p.removePotionEffect(PotionEffectType.SPEED));
        Bukkit.getScheduler().cancelTask(taskLaterID);

        this.isRunning = false;
        this.safePlayers.clear();

        gManager.getTimer().end();
        gManager.gameData().setPvPState(PvPState.ONLY_GUARDS);
        gManager.getSquidInstance().unregisterListener(appleListener);
    }

    /**
     * Randomly assign the apple to {@code times} players who don't have it yet.
     */
    public void randomGiveApple(int times) {
        for (int i = 0; i < times; i++) {
            var eligible = Bukkit.getOnlinePlayers().stream()
                    .filter(o -> !safePlayers.contains(o))
                    .filter(o -> playerManager().isPlayer(o))
                    .filter(o -> !o.getGameMode().equals(GameMode.SPECTATOR)
                            && !o.getGameMode().equals(GameMode.CREATIVE))
                    .toList();

            if (eligible.isEmpty()) return;

            int index = eligible.size() < 2
                    ? 0
                    : ThreadLocalRandom.current().nextInt(0, eligible.size());

            Player chosen = eligible.get(index);
            if (!isSafe(chosen)) giveApple(chosen);
        }
    }

    /**
     * Transfer the apple from {@code currentHolder} to {@code receiver}.
     * Called when a safe player is hit: the hitter steals the apple.
     */
    public void switchApple(Player currentHolder, Player receiver) {
        removeApple(currentHolder, false);
        giveApple(receiver);
    }

    /** Mark a player as safe and give them the apple item + visual effects. */
    public void giveApple(Player player) {
        safePlayers.add(player);
        player.getInventory().addItem(getAppleItem());
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
    }

    /** Remove the apple from a player. Pass {@code end=true} when cleaning up at round end. */
    public void removeApple(Player player, boolean end) {
        player.getEquipment().setHelmet(null);
        player.getInventory().remove(getAppleItem());
        player.removePotionEffect(PotionEffectType.GLOWING);
        player.removePotionEffect(PotionEffectType.SPEED);

        if (end) return;

        safePlayers.remove(player);

        // Brief speed boost for the player who just lost the apple so they can chase
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Short.MAX_VALUE, 1, false, false));
    }

    /** Kill every player who does NOT hold the apple. */
    private void killUnsafe() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> playerManager().isPlayer(p))
                .filter(p -> !p.getGameMode().equals(GameMode.SPECTATOR)
                        && !p.getGameMode().equals(GameMode.CREATIVE))
                .filter(p -> !isSafe(p))
                .forEach(p -> {
                    p.getInventory().clear();
                    p.getWorld().createExplosion(p.getLocation(), 3, false, false);
                    p.setHealth(0);
                    Sounds.playSoundDistance(p.getLocation(), 100, Sound.ENTITY_GENERIC_EXPLODE, 5f, 1f);
                });

        // Clean up safe players' apple & effects
        safePlayers.forEach(p -> {
            p.getInventory().remove(getAppleItem());
            p.removePotionEffect(PotionEffectType.GLOWING);
            p.removePotionEffect(PotionEffectType.SPEED);
        });

        Bukkit.getOnlinePlayers().forEach(p -> p.removePotionEffect(PotionEffectType.SPEED));
    }

    public ItemStack getAppleItem() {
        // Replace with CustomItems.Decoration.APPLE.get() if you have a custom item
        return new ItemStack(Material.GOLDEN_APPLE);
    }

    public PlayerManager playerManager() {
        return gManager.getSquidInstance().getPManager();
    }

    public boolean isSafe(Player player) {
        return safePlayers.contains(player);
    }

}
