package me.lofro.game.global.utils.timer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;

public class GameTimer extends BukkitRunnable {

    private int seconds;

    private final @Getter BossBar bossBar;
    private final @Getter List<Audience> audience = new ArrayList<>();

    private @Setter @Getter boolean isActive = false;

    public GameTimer() {
        this.seconds = 0;
        this.bossBar = BossBar.bossBar(formatTime(this.seconds), 1.0f, Color.WHITE, Overlay.PROGRESS);
    }

    @Override
    public void run() {
        if (!isActive)
            return;
        if (this.seconds >= 0) {
            this.bossBar.name(formatTime(this.seconds--));
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), "sfx.tic", 2f, 1f));
        } else {
            end();
        }
    }

    public void update(int seconds) {
        this.seconds = seconds;
        this.bossBar.name(formatTime(this.seconds));
    }

    public int getTime() {
        return seconds;
    }

    public void setPreStart(int time) {
        this.seconds = time;
        this.bossBar.name(formatTime(time));
        this.addPlayers();
    }

    public void start(int seconds) {
        setPreStart(seconds);
        this.isActive = true;
        addPlayers();
    }

    public void end() {
        this.isActive = false;
        this.seconds = 0;
        removePlayers();
    }

    public void addPlayer(Audience player) {
        if (audience.contains(player))
            return;
        audience.add(player);
        player.showBossBar(this.bossBar);
    }

    public void addPlayers() {
        Bukkit.getOnlinePlayers().forEach(this::addPlayer);
    }

    public void removePlayer(Audience player) {
        audience.remove(player);
        player.hideBossBar(this.bossBar);
    }

    public void removePlayers() {
        Bukkit.getOnlinePlayers().forEach(this::removePlayer);
    }

    private static Component formatTime(final int time) {
        return Component.text(timeConvert(time));
    }

    private static String timeConvert(int t) {
        int hours = t / 3600;

        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return (hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds));
    }

}
