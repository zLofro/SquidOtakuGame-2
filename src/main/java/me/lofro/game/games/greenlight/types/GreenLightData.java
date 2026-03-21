package me.lofro.game.games.greenlight.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import lombok.Getter;

/**
 * A class designed to hold all the stateful data for GreenLight RedLight
 * mini-game.
 *
 */
public class GreenLightData {

    private @Getter @Setter Location cubeUpper, cubeLower, senseiLocation;
    private final @Getter List<Location> cannonLocations;

    public GreenLightData() {
        final var baseWorld = Bukkit.getWorlds().get(0);
        this.cubeLower = new Location(baseWorld, -20, -29, -35);
        this.cubeUpper = new Location(baseWorld, -146, 15, 18);
        this.senseiLocation = new Location(baseWorld, -152,-28,-9);
        this.cannonLocations = new ArrayList<>();
    }

    public GreenLightData(Location cuberUpper, Location cubeLower, Location senseiLocation, Location... cannonLocations) {
        this.cubeUpper = cuberUpper;
        this.cubeLower = cubeLower;
        this.senseiLocation = senseiLocation;
        this.cannonLocations = new ArrayList<>();
        this.cannonLocations.addAll(Arrays.asList(cannonLocations));
    }

    public GreenLightData(Location cubeUpper, Location cubeLower, Location senseiLocation, List<Location> cannonLocations) {
        this.cubeUpper = cubeUpper;
        this.cubeLower = cubeLower;
        this.senseiLocation = senseiLocation;
        this.cannonLocations = cannonLocations;
    }
}
