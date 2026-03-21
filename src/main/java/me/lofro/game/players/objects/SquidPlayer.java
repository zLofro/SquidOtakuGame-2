package me.lofro.game.players.objects;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.Setter;

public class SquidPlayer extends SquidParticipant {

    private final @Getter int id;
    private @Getter @Setter boolean dead;

    public SquidPlayer(@Nonnull String name, int id) {
        super(name);
        this.id = id;
    }

    public SquidPlayer(@Nonnull String name, int id, boolean dead) {
        super(name);
        this.id = id;
        this.dead = dead;
    }

}
