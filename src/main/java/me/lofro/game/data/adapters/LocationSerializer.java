package me.lofro.game.data.adapters;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * A json serializer & deserializer for the {@link Location} class.
 *
 */
public class LocationSerializer implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        var jsonObject = json.getAsJsonObject();
        // Obtain elements from given json object.
        var world = jsonObject.get("world");
        var x = jsonObject.get("x");
        var y = jsonObject.get("y");
        var z = jsonObject.get("z");
        // Get the values of the json elements as primitives.
        double xVal = x.getAsDouble();
        double yVal = y.getAsDouble();
        double zVal = z.getAsDouble();
        // If a world is provided, parse it, otherwise use index 0th world.
        if (world != null && world.isJsonPrimitive() && world.getAsString() != null) {
            var worldName = world.getAsString();
            return new Location(Bukkit.getWorld(worldName), xVal, yVal, zVal);
        }

        return new Location(getDefaultWorld(), xVal, yVal, zVal);
    }

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        var json = new JsonObject();

        json.addProperty("world", src.getWorld().getName());
        json.addProperty("x", src.getX());
        json.addProperty("y", src.getY());
        json.addProperty("z", src.getZ());

        return json;
    }

    /**
     * Static constructor for gson.
     * 
     * @return A new instance of {@link LocationArraySerializer}.
     */
    public static LocationArraySerializer getArraySerializer() {
        return new LocationArraySerializer();
    }

    /**
     * Static constructor from gson.
     * 
     * @return A new instance of {@link LocationSerializer}.
     */
    public static LocationSerializer getSerializer() {
        return new LocationSerializer();
    }

    /**
     * LocationArraySerializer
     */
    public static class LocationArraySerializer implements JsonSerializer<Location[]>, JsonDeserializer<Location[]> {

        @Override
        public Location[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            var jsonArray = json.getAsJsonArray();

            var list = new ArrayList<Location>();
            var iter = jsonArray.iterator();

            while (iter.hasNext()) {
                var next = iter.next();
                list.add(context.deserialize(next, Location.class));
            }

            return list.toArray(new Location[list.size()]);
        }

        @Override
        public JsonElement serialize(Location[] src, Type typeOfSrc, JsonSerializationContext context) {
            var json = new JsonArray();

            for (var l : src)
                json.add(context.serialize(l));

            return json;
        }

    }

    /**
     * Utility function to get the default world.
     * 
     * @return The default world.
     */
    private static World getDefaultWorld() {
        return Bukkit.getWorlds().get(0);
    }

}
