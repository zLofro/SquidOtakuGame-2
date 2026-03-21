package me.lofro.game.games.greenlight;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import me.lofro.game.games.GameManager;
import me.lofro.game.games.greenlight.utils.tasks.PlayerArrayQueueShootTask;
import me.lofro.game.global.utils.text.HexFormatter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.title.Title;
import me.lofro.game.games.greenlight.enums.LightState;

@Data
@EqualsAndHashCode(callSuper = false)
public class GreenLightGame extends BukkitRunnable {

    private GameManager gManager;
    private GreenLightManager gLightManager;

    private int greenLowestTimeBound;
    private int greenHighestTimeBound;

    private int redLowestTimeBound;
    private int redHighestTimeBound;

    private int timeBetween;

    private @Getter @Setter int taskID;
    private @Getter @Setter int endTaskID;
    private @Getter @Setter int shootAllTaskID;

    public GreenLightGame(GreenLightManager gLightManager) {
        this.gLightManager = gLightManager;
        this.gManager = gLightManager.getGManager();
        this.greenLowestTimeBound = gLightManager.getGreenLowestTimeBound();
        this.greenHighestTimeBound = gLightManager.getGreenHighestTimeBound();
        this.redLowestTimeBound = gLightManager.getRedLowestTimeBound();
        this.redHighestTimeBound = gLightManager.getRedHighestTimeBound();
        this.timeBetween = ThreadLocalRandom.current().nextInt(greenLowestTimeBound, greenHighestTimeBound);
    }

    @Override
    public void run() {
        if (timeBetween == 0) {
            greenLight(!gLightManager.getLightState().equals(LightState.GREEN_LIGHT));
        }

        if (!gLightManager.getGreenLightListener().getMovedList().isEmpty()) {
            new PlayerArrayQueueShootTask(gLightManager, gLightManager.getGreenLightListener().getMovedList(), false, 0, 20);
            gLightManager.getGreenLightListener().getMovedList().clear();
        }

        timeBetween--;
    }

    /**
     * Function that updates the light state of the game. Makes the logic run.
     *
     * @param bool defines whether the light is green or red.
     */
    public void greenLight(Boolean bool) {
        gLightManager.rotateProgressively(180 ,true, 20);
        if (bool) {
            greenLightTitle("&a¡LUZ VERDE!", "&aPuedes comenzar a moverte.");

            this.timeBetween = ThreadLocalRandom.current().nextInt(greenLowestTimeBound, greenHighestTimeBound);
            gLightManager.setLightState(LightState.GREEN_LIGHT);
            gManager.getSquidInstance().unregisterListener(gLightManager.getGreenLightListener());
        } else {
            greenLightTitle("&c¡LUZ ROJA!", "&cNo muevas ni un pelo.");

            Bukkit.getScheduler().runTaskLater(gManager.getSquidInstance(), () -> {
                this.timeBetween = ThreadLocalRandom.current().nextInt(redLowestTimeBound, redHighestTimeBound);
                gLightManager.setLightState(LightState.RED_LIGHT);
                gManager.getSquidInstance().registerListener(gLightManager.getGreenLightListener());
            }, 30);
        }
    }

    /**
     * Helper function that sends a title to all players and plays the GreenLightGame bell sound effect.
     * 
     * @param title    title to be sent.
     * @param subTitle subTitle to be sent.
     */
    private void greenLightTitle(String title, String subTitle) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.showTitle(Title.title(HexFormatter.hexFormat(title), HexFormatter.hexFormat(subTitle), Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(3))));
            p.playSound(p.getLocation(), "sfx.bell", 1, 1);
        });
    }

}
