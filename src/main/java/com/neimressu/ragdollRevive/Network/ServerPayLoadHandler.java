package com.neimressu.ragdollRevive.Network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayLoadHandler {
    public static void handle(GiveUpPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        player.getPersistentData().putBoolean("wantToGiveUp",payload.down());
        if (!payload.down()) {
            PacketDistributor.sendToPlayer(player,new TimerPayLoad(0,2));
        }
    }
}