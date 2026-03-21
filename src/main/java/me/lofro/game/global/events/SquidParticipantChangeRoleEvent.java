package me.lofro.game.global.events;

import lombok.Getter;
import me.lofro.game.global.events.types.BaseEvent;
import me.lofro.game.players.enums.Role;
import org.bukkit.entity.Player;

public class SquidParticipantChangeRoleEvent extends BaseEvent {

    private final @Getter Player player;
    private final @Getter Role role;

    public SquidParticipantChangeRoleEvent(Player player, Role role) {
        this.player = player;
        this.role = role;
    }

    public SquidParticipantChangeRoleEvent(Player player, Role role, boolean async) {
        super(async);
        this.player = player;
        this.role = role;
    }

}
