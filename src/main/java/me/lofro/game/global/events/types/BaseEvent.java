package me.lofro.game.global.events.types;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * Template event.
 *
 */
public class BaseEvent extends Event {

    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({ "java:S116", "java:S1170" })
    private final @Getter HandlerList Handlers = HandlerList;

    public BaseEvent(boolean async) {
        super(async);
    }

    public BaseEvent() {
        this(false);
    }

}
