package com.neimressu.ragdollRevive;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = RagdollRevive.MODID)
public class KillDyingLoggedInPlayers {
    @SubscribeEvent
    private static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (player.getPersistentData().getBoolean("isDying")) {
            ReviveAPI.killDyingPlayer(player);
        }
    }
}
