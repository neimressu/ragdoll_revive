package com.neimressu.ragdollRevive;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

public class PlayerRevivedEvent extends PlayerEvent {
    private final ServerPlayer reviver;

    public PlayerRevivedEvent(ServerPlayer target, @Nullable ServerPlayer reviver) {
        super(target);
        this.reviver = reviver;
    }

    /**
     * @return The player who was successfully revived.
     */
    @Override
    public ServerPlayer getEntity() {
        return (ServerPlayer) super.getEntity();
    }

    /**
     * @return The player who helped revive the character. May be null if the revival occurred via the API without the player's involvement.
     */
    @Nullable
    public ServerPlayer getReviver() {
        return this.reviver;
    }
}