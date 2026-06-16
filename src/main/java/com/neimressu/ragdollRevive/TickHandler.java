package com.neimressu.ragdollRevive;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;

@EventBusSubscriber(modid = RagdollRevive.MODID)
public class TickHandler {

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {

        long gameTime =
                event.getServer()
                        .overworld()
                        .getGameTime();

        var expired = new ArrayList<>(
                ReviveManager.DYING.values()
        );

        for (var data : expired) {

            if (gameTime < data.expireTick())
                continue;

            ServerPlayer player =
                    event.getServer()
                            .getPlayerList()
                            .getPlayer(data.uuid());

            if (player == null)
                continue;

            player.setInvulnerable(false);

            player.getPersistentData()
                    .putBoolean("mustDie", true);

            if (!ReviveManager.isRagdollCorpseEnabled()) data.session().release();

            player.hurt(data.damageSource(),Float.MAX_VALUE);
            player.setDeltaMovement(Vec3.ZERO);

            ReviveManager.DYING.remove(
                    player.getUUID()
            );
        }
    }
}