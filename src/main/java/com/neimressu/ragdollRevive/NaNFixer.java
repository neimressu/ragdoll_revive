package com.neimressu.ragdollRevive;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = RagdollRevive.MODID)
public class NaNFixer {
    @SubscribeEvent static void onRespawn(PlayerEvent.PlayerRespawnEvent event){
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (Float.isNaN(player.getHealth())) player.setHealth(player.getMaxHealth());
    }
}
