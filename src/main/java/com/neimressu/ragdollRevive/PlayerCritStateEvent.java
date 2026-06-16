package com.neimressu.ragdollRevive;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerCritStateEvent extends PlayerEvent implements ICancellableEvent {
    public PlayerCritStateEvent(ServerPlayer player) {
        super(player);
    }
}
