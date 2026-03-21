package me.lofro.game.data.types;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import lombok.Getter;
import me.lofro.game.global.events.SquidParticipantChangeRoleEvent;
import me.lofro.game.players.types.IdProvider;
import me.lofro.game.players.enums.Role;
import me.lofro.game.players.objects.SquidGuard;
import me.lofro.game.players.objects.SquidParticipant;
import me.lofro.game.players.objects.SquidPlayer;
import org.bukkit.Bukkit;

/**
 * A class that holds all the state of the players in the game.
 *
 */
public class PlayerData {

    private final @Getter IdProvider idProvider;
    private Map<String, SquidParticipant> participants = new ConcurrentHashMap<>();

    public PlayerData() {
        this.idProvider = new IdProvider();

    }

    public PlayerData(IdProvider idProvider, Map<String, SquidParticipant> participants) {
        this.idProvider = idProvider;
        this.participants = participants;
    }

    /**
     * Function that adds a new player to the game.
     * 
     * @param name The name of the player.
     * @return The new player.
     */
    public SquidPlayer addPlayer(String name) {
        // If already present, throw exception
        if (participants.containsKey(name))
            throw new IllegalArgumentException("Player already exists in the data set.");
        // Get the next available ID
        int id = idProvider.getID();
        // Generate and put the player in the map
        var player = new SquidPlayer(name, id);
        participants.put(name, player);
        return player;
    }

    /**
     * Function that adds a new guard to the game.
     * 
     * @param name The name of the guard.
     * @return The new guard.
     */
    public SquidGuard addGuard(String name) {
        // If already present, throw exception
        if (participants.containsKey(name))
            throw new IllegalArgumentException("Player already exists in the data set.");
        // Generate and put the player in the map
        var guard = new SquidGuard(name);
        participants.put(name, guard);
        return guard;
    }

    /**
     * Function that queries the dataset and obtains a player by name.
     * 
     * @param name The name of the player.
     */
    public SquidPlayer getPlayer(String name) {
        var p = participants.get(name);
        if (p instanceof SquidPlayer squidPlayer)
            return squidPlayer;
        else
            return null;
    }

    /**
     * Function that queries the dataset and obtains a guard by name.
     * 
     * @param name The name of the guard.
     * @return The guard.
     */
    public SquidGuard getGuard(String name) {
        var p = participants.get(name);
        if (p instanceof SquidGuard squidGuard)
            return squidGuard;
        else
            return null;
    }

    /**
     * Function that queries dataset and obtains a participant by name.
     * 
     * @param name The name of the participant.
     * @return The participant.
     */
    public SquidParticipant getParticipant(String name) {
        return participants.get(name);
    }

    /**
     * Function that queries dataset and returns player by Id.
     * 
     * @param id The id of the player.
     * @return The player if present, null otherwise.
     */
    public SquidPlayer getPlayerById(int id) {
        for (var p : participants.values()) {
            if (p instanceof SquidPlayer squidPlayer) {
                if (squidPlayer.getId() == id)
                    return squidPlayer;
            }
        }
        return null;
    }

    /**
     * Function that removes player from the dataset and returns it.
     * 
     * @param name The name of the player.
     * @return The player if present.
     * @throws IllegalArgumentException if player is not present
     */
    public SquidPlayer removePlayer(String name) throws IllegalArgumentException {
        // Check if player is a participant, if so check if that pa
        var squidParticipant = getParticipant(name);
        // If not a participant, throw exception
        if (squidParticipant == null)
            throw new IllegalArgumentException("Player " + name + " is not a SquidParticipant.");
        // Short-circut if not a player
        if (!(squidParticipant instanceof SquidPlayer sPlayer))
            throw new IllegalArgumentException("Player " + name + " is not a SquidPlayer.");

        return removePlayer(sPlayer);
    }

    private SquidPlayer removePlayer(final SquidPlayer squidPlayer) {
        // Add ID back to the pool
        idProvider.addId(squidPlayer.getId());
        // remove player
        participants.remove(squidPlayer.getName());
        return squidPlayer;
    }

    /**
     * Function that removes guard from the dataset and returns it.
     * 
     * @param name The name of the guard.
     * @return The guard if present.
     * @throws IllegalArgumentException if guard is not present
     */
    public SquidGuard removeGuard(String name) throws IllegalArgumentException {
        // Check if player is a participant, if so check if that pa
        var squidParticipant = getParticipant(name);
        // If not a participant, throw exception
        if (squidParticipant == null)
            throw new IllegalArgumentException("Player " + name + " is not a SquidParticipant.");
        // Short-circut if not a guard
        if (!(squidParticipant instanceof SquidGuard sGuard))
            throw new IllegalArgumentException("Player " + name + " is not a SquidGuard.");

        participants.remove(name);
        return sGuard;
    }

    private SquidGuard removeGuard(final SquidGuard squidGuard) {
        // remove player
        participants.remove(squidGuard.getName());
        return squidGuard;
    }

    // Change roles function
    public void toggleRoles(final SquidParticipant participant) {
        if (participant instanceof SquidPlayer squidPlayer) {
            // Remove the player, add him back to set as guard.
            addGuard(removePlayer(squidPlayer).getName());
        } else if (participant instanceof SquidGuard squidGuard) {
            // Remove the guard, add him back to set as player.
            addPlayer(removeGuard(squidGuard).getName());
        }
    }

    public boolean changeRoles(SquidParticipant participant, Role role) {
        if (participant instanceof SquidPlayer squidPlayer) {
            if (role == Role.PLAYER)
                return false;
            // Remove the player, add him back to set as guard.
            addGuard(removePlayer(squidPlayer).getName());
        } else if (participant instanceof SquidGuard squidGuard) {
            if (role == Role.GUARD)
                return false;
            // Remove the guard, add him back to set as player.
            addPlayer(removeGuard(squidGuard).getName());
        }

        Bukkit.getPluginManager().callEvent(new SquidParticipantChangeRoleEvent(participant.player(), role));

        return true;

    }

    public Role getRole(SquidParticipant squidParticipant) {
        return (squidParticipant instanceof SquidPlayer) ? Role.PLAYER : Role.GUARD;
    }

    public List<SquidPlayer> getPlayers() {
        return participants.values().stream().filter(p -> p instanceof SquidPlayer).map(p -> (SquidPlayer) p)
                .collect(Collectors.toList());
    }

    public List<SquidGuard> getGuards() {
        return participants.values().stream().filter(p -> p instanceof SquidGuard).map(p -> (SquidGuard) p)
                .collect(Collectors.toList());
    }

}
