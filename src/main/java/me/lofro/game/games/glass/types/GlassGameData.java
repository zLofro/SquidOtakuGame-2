package me.lofro.game.games.glass.types;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * A class designed to hold all the stateful data for Glass mini-game.
 */
public class GlassGameData {

    @Getter @Setter Location areaLower, areaUpper;

    public GlassGameData() {
        var baseWorld = Bukkit.getWorlds().getFirst();

        this.areaLower = new Location(baseWorld, 19,-18,-149);
        this.areaUpper = new Location(baseWorld, 11,-10,-221);
    }

    public GlassGameData(Location areaLower, Location areaUpper) {
        this.areaLower = areaLower;
        this.areaUpper = areaUpper;
    }

}
