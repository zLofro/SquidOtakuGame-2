package me.lofro.game.games.backrooms.events;

import lombok.Getter;
import me.lofro.game.games.backrooms.enums.BackRoomsState;
import me.lofro.game.global.events.types.BaseEvent;

public class BackRoomsChangeStateEvent extends BaseEvent {

    private final @Getter BackRoomsState backRoomsState;

    public BackRoomsChangeStateEvent(BackRoomsState backRoomsState, boolean async) {
        super(async);
        this.backRoomsState = backRoomsState;
    }

    public BackRoomsChangeStateEvent(BackRoomsState backRoomsState) {
        this.backRoomsState = backRoomsState;
    }

}
