package com.neimressu.ragdollRevive;

import com.neimressu.ragdollRevive.Network.TimerPayLoad;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;

import static com.neimressu.ragdollRevive.RagdollRevive.MODID;

@EventBusSubscriber(modid = MODID)
public class GiveUpHandler {
    @SubscribeEvent
    public static void onPlayerTick(ServerTickEvent.Post event){
        if (!CommonConfig.CAN_PLAYER_GIVE_UP.getAsBoolean()) return;

        var currGivingUp = new ArrayList<>(
                ReviveManager.GIVINGUP.values()
        );

        for (var data : currGivingUp) {
            ServerPlayer player =
                    event.getServer()
                            .getPlayerList()
                            .getPlayer(data.uuid());
            if (player == null) return;
            if (player.getPersistentData().getBoolean("wantToGiveUp") && player.getPersistentData().getBoolean("isDying")) {
                boolean currentState = player.getPersistentData().getBoolean("wantToGiveUp");

                if (!data.oldKeyState() && currentState) {
                    PacketDistributor.sendToPlayer(player,new TimerPayLoad(CommonConfig.GIVE_UP_TIMER.getAsInt(),2));
                }

                if (currentState) {
                    ReviveManager.GIVINGUP.computeIfPresent(player.getUUID(), (k, d) ->
                            new ReviveManager.GivingUp(
                                    d.uuid(),
                                    d.givingTick() - 1,
                                    true
                            )
                    );
                } else if (event.getServer().getTickCount() % 5 == 0) {
                    ReviveManager.GIVINGUP.computeIfPresent(player.getUUID(), (k, d) ->
                            new ReviveManager.GivingUp(
                                    d.uuid(),
                                    CommonConfig.GIVE_UP_TIMER.get(),
                                    false
                            )
                    );
                }
            } else if (!(player.getPersistentData().getBoolean("wantToGiveUp")) &&
                    event.getServer().getTickCount() % 5 == 0
            ) {
                ReviveManager.GIVINGUP.computeIfPresent(player.getUUID(),(k,dataNew)->new ReviveManager.GivingUp(
                        data.uuid(),
                        CommonConfig.GIVE_UP_TIMER.get(),
                        player.getPersistentData().getBoolean("wantToGiveUp")
                ));
            }

            if (data.givingTick()==0 && player.getPersistentData().getBoolean("wantToGiveUp")) {
                var dyingData = ReviveManager.DYING.get(player.getUUID());
                player.hurt(dyingData.damageSource(),Float.MAX_VALUE);
                ReviveManager.DYING.remove(player.getUUID());
                ReviveManager.GIVINGUP.remove(player.getUUID());
                PacketDistributor.sendToPlayer(player,new TimerPayLoad(0,2));
                PacketDistributor.sendToPlayer(player,new TimerPayLoad(0,1));
                player.getPersistentData().putBoolean("wantToGiveUp",false);
            }
        }
    }
}
