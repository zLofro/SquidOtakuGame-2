package me.lofro.game.global.utils.scoreboards;

import lombok.Getter;
import me.lofro.game.SquidGame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

/**
 * Class to manage the team-scoreboard prefixes for the game.
 */
public class Scoreboards {

    private static final @Getter ScoreboardManager scoreboardManager = SquidGame.getInstance().getServer().getScoreboardManager();
    private static final @Getter Scoreboard scoreboard = scoreboardManager.getMainScoreboard();

    /**
     *
     * Function that sets the team tag name to a player.
     *
     * @param displayName display name to set.
     * @param player player.
     *
     */
    public static void setDisplayName(Component displayName, NamedTextColor color, Player player) {
        var name = player.getName();
        var team = scoreboard.getTeam(name);

        if (team != null) {
            team.displayName(displayName);
        } else {
            team = scoreboard.registerNewTeam(name);
            team.prefix(displayName);
            team.color(color);
            team.addPlayer(player);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }
    }

    /**
     *
     *Function that removes the team tag name from a player.
     *
     * @param player player.
     */
    public static void removeDisplayName(Player player) {
        var name = player.getName();
        var team = scoreboard.getTeam(name);

        if (team != null) team.unregister();
    }

    /**
     *
     * Function that sets the team tag name to a guard.
     *
     * @param player player.
     */
    public static void setGuard(Player player) {
        var team = scoreboard.getTeam("GUARDS");

        var guardTeam = (team != null) ? team : scoreboard.registerNewTeam("GUARDS");
        guardTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        guardTeam.color(NamedTextColor.RED);

        guardTeam.addPlayer(player);
    }

    /**
     *
     * Function that removes the team tag name from a guard.
     *
     * @param player player.
     */
    public static void removeGuard(Player player) {
        var team = scoreboard.getTeam("GUARDS");

        var guardTeam = (team != null) ? team : scoreboard.registerNewTeam("GUARDS");
        guardTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        guardTeam.color(NamedTextColor.RED);

        guardTeam.removePlayer(player);
    }

}
