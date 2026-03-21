package me.lofro.game.global.utils.credits;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Credits {

    public static void showCredits(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "credits " + player.getName());
        player.playSound(player.getLocation(), "sfx.credit_music", 1f, 1f);
    }

}
