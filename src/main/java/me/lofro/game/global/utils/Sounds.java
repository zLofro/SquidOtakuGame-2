package me.lofro.game.global.utils;

import org.bukkit.Location;
import org.bukkit.Sound;

public class Sounds {

    /**
     *
     * Function to play a sound to the players nearby a given radius.
     *
     * @param loc location of the sound to play.
     * @param distance radius of the sound.
     * @param sound sound to play.
     * @param pitch pìtch of the sound.
     * @param volume volume of the sound.
     *
     */
    public static void playSoundDistance(Location loc, Integer distance, String sound, Float volume, Float pitch) {
        loc.getNearbyPlayers(distance).forEach(player -> player.playSound(loc, sound, volume, pitch));
    }

    /**
     *
     * Function to play a sound to the players nearby a given radius.
     *
     * @param loc location of the sound to play.
     * @param distance radius of the sound.
     * @param sound sound to play.
     * @param pitch pìtch of the sound.
     * @param volume volume of the sound.
     *
     */
    public static void playSoundDistance(Location loc, Integer distance, Sound sound, Float volume, Float pitch) {
        loc.getNearbyPlayers(distance).forEach(player -> player.playSound(loc, sound, volume, pitch));
    }

}
