package me.lofro.game.games.smashbros.listeners;

import me.lofro.game.games.smashbros.SmashBrosManager;
import me.lofro.game.global.utils.text.HexFormatter;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class SmashBrosListener implements Listener {

    private final SmashBrosManager manager;

    public SmashBrosListener(SmashBrosManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        var player = e.getPlayer();
        var name = player.getName();
        var killer = player.getKiller();

        if (!manager.playerManager().isPlayer(player) || killer == null) return;

        var nameKiller = killer.getName();

        var squidPlayer = manager.playerManager().pData().getPlayer(name);
        var id = squidPlayer.getId();

        manager.incrementDeaths();

        if (manager.getDeaths() <= manager.getDeathsToWin()) return;

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.playSound(p.getLocation(), "sfx.winner", 1f, 1f);
            p.showTitle(Title.title(HexFormatter.hexFormat("&6¡GANADOR!"), HexFormatter.hexFormat("&e #" + id + " " + nameKiller)));
            p.sendMessage(HexFormatter.hexFormat("&eEl participante &6#" + id + " " + nameKiller + " &eha ganado los &6SQUID OTAKU GAMES."));
        });
        manager.endGame();
    }

}
