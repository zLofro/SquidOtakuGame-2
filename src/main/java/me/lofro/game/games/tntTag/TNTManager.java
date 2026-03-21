package me.lofro.game.games.tntTag;

import lombok.Getter;
import me.lofro.game.games.GameManager;
import me.lofro.game.games.tntTag.listeners.TNTListener;
import me.lofro.game.global.enums.PvPState;
import me.lofro.game.global.item.CustomItems;
import me.lofro.game.global.utils.Sounds;
import me.lofro.game.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TNTManager {

    private final @Getter GameManager gManager;

    private final @Getter TNTListener tntListener;

    private @Getter boolean isRunning = false;

    private @Getter int taggedLimit;
    private int taskLaterID;

    private final @Getter List<Player> taggeds = new ArrayList<>();

    public TNTManager(GameManager gManager) {
        this.gManager = gManager;

        this.tntListener = new TNTListener(this);
    }

    public void runGame(int seconds, int taggedLimit) {
        if (this.isRunning)
            throw new IllegalStateException(
                    "The game " + this.getClass().getSimpleName() + " is already running.");

        this.isRunning = true;
        this.taggedLimit = taggedLimit;

        gManager.getTimer().start(seconds);

        gManager.getSquidInstance().registerListener(tntListener);

        gManager.gameData().setPvPState(PvPState.ALL);

        randomTag(taggedLimit);

        this.taskLaterID = Bukkit.getScheduler().runTaskLater(gManager.getSquidInstance(), this::endGame, (seconds + 2) * 20L).getTaskId();
    }

    public void endGame() {
        this.isRunning = false;
        killTaggeds();

        this.taggeds.clear();

        gManager.gameData().setPvPState(PvPState.ONLY_GUARDS);

        gManager.getSquidInstance().unregisterListener(tntListener);
    }

    public void stopGame() {
        taggeds.forEach(t -> unTag(t, true));
        Bukkit.getOnlinePlayers().forEach(p -> p.removePotionEffect(PotionEffectType.SPEED));
        Bukkit.getScheduler().cancelTask(taskLaterID);

        this.isRunning = false;

        this.taggeds.clear();
        gManager.getTimer().end();

        gManager.gameData().setPvPState(PvPState.ONLY_GUARDS);

        gManager.getSquidInstance().unregisterListener(tntListener);
    }

    public void randomTag(int times) {
        for (int i = 0; i < times; i++) {
            var notTaggeds = Bukkit.getOnlinePlayers().stream()
                    .filter(o -> !taggeds.contains(o))
                    .filter(o -> playerManager().isPlayer(o))
                    .filter(o -> !o.getGameMode().equals(GameMode.SPECTATOR) && !o.getGameMode().equals(GameMode.CREATIVE))
                    .toList();

            Player randomTagged;

            if (notTaggeds.isEmpty()) return;

            if (notTaggeds.size() < 2) {
                randomTagged = notTaggeds.get(0);
            } else {
                var random = ThreadLocalRandom.current().nextInt(0, notTaggeds.size());

                randomTagged = notTaggeds.get(random);
            }

            if (!isTagged(randomTagged)) tag(randomTagged);
        }
    }

    public void switchTag(Player tagger, Player tagged) {
        unTag(tagger, false);
        tag(tagged);
    }

    public void tag(Player tagged) {
        taggeds.add(tagged);
        tagged.getInventory().addItem(CustomItems.Decoration.TNT.get());
        tagged.getEquipment().setHelmet(CustomItems.Decoration.TNT.get());
        tagged.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
        tagged.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
    }

    public void unTag(Player tagged, boolean end) {
        tagged.getEquipment().setHelmet(null);
        tagged.getInventory().remove(CustomItems.Decoration.TNT.get());
        tagged.removePotionEffect(PotionEffectType.GLOWING);
        tagged.removePotionEffect(PotionEffectType.SPEED);
        if (end) return;
        taggeds.remove(tagged);
        tagged.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Short.MAX_VALUE, 0, false, false));
    }

    public void killTaggeds() {
        taggeds.forEach(t -> {
            t.getInventory().clear();
            t.getWorld().createExplosion(t.getLocation(), 3, false, false);
            t.setHealth(0);
            Sounds.playSoundDistance(t.getLocation(), 100, Sound.ENTITY_GENERIC_EXPLODE, 5f, 1f);
        });
        Bukkit.getOnlinePlayers().forEach(p -> p.removePotionEffect(PotionEffectType.SPEED));
    }

    public PlayerManager playerManager() {
        return gManager.getSquidInstance().getPManager();
    }

    public boolean isTagged(Player player) {
        return taggeds.contains(player);
    }

}
