package me.lofro.game.games.backrooms.types;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * A class designed to hold all the stateful data for Backrooms mini-game.
 */
public class BackRoomsData {

    private @Getter @Setter Location middleCubeLower, middleCubeUpper, cubeLower, cubeUpper;

    public BackRoomsData() {
        final var baseWorld = Bukkit.getWorlds().getFirst();

        this.cubeLower = new Location(baseWorld, 89.0, -44.0, 13.0);
        this.cubeUpper = new Location(baseWorld, 288.0, -39.0, -154.0);
        this.middleCubeLower = new Location(baseWorld, 90.0,-44.0,-89.0);
        this.middleCubeUpper = new Location(baseWorld, 133.0, -39.0, -54.0);
    }

    public BackRoomsData(Location cubeLower, Location cubeUpper, Location middleCubeLower, Location middleCubeUpper) {
        this.cubeLower = cubeLower;
        this.cubeUpper = cubeUpper;
        this.middleCubeLower = middleCubeLower;
        this.middleCubeUpper = middleCubeUpper;
    }

}
