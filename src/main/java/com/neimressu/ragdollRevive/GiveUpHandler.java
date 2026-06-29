package com.neimressu.ragdollRevive;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;

import static com.neimressu.ragdollRevive.RagdollRevive.MODID;

@EventBusSubscriber(modid = MODID)
public class GiveUpHandler {
    private static int giveUpTimer;
    @SubscribeEvent
    public static void onPlayerTick(ServerTickEvent.Post event){

        var currGivingUp = new ArrayList<>(
                ReviveManager.GIVINGUP.values()
        );

        for (var data : currGivingUp) {
            ServerPlayer player =
                    event.getServer()
                            .getPlayerList()
                            .getPlayer(data.uuid());
            if (player.getPersistentData().getBoolean("wantToGiveUp")) {
                boolean currentState = player.getPersistentData().getBoolean("wantToGiveUp");

                if (!data.oldKeyState() && currentState) {
                    player.sendSystemMessage(Component.literal(String.format(
                            "You started giving up. You will give up in %.2f seconds...",
                            (float) Config.GIVE_UP_TIMER.getAsInt()/20
                    )).withStyle(ChatFormatting.RED));
                }

                if (currentState) {
                    ReviveManager.GIVINGUP.computeIfPresent(player.getUUID(), (k, d) ->
                            new ReviveManager.GivingUp(
                                    d.uuid(),
                                    d.givingTick() - 1,
                                    true
                            )
                    );
                    if (data.givingTick()%20==0 &&
                            (data.givingTick()!=Config.GIVE_UP_TIMER.getAsInt()) &&
                            data.givingTick()!=0) {
                        player.sendSystemMessage(Component.literal(String.format(
                                "%.0f...",
                                (float) data.givingTick()/20
                        )).withStyle(ChatFormatting.RED));
                    }
                } else if (event.getServer().getTickCount() % 5 == 0) {
                    ReviveManager.GIVINGUP.computeIfPresent(player.getUUID(), (k, d) ->
                            new ReviveManager.GivingUp(
                                    d.uuid(),
                                    Config.GIVE_UP_TIMER.get(),
                                    false
                            )
                    );
                }
            } else if (!(player.getPersistentData().getBoolean("wantToGiveUp")) &&
                    event.getServer().getTickCount() % 5 == 0
            ) {
                ReviveManager.GIVINGUP.computeIfPresent(player.getUUID(),(k,dataNew)->new ReviveManager.GivingUp(
                        data.uuid(),
                        Config.GIVE_UP_TIMER.get(),
                        player.getPersistentData().getBoolean("wantToGiveUp")
                ));
            }

            if (data.givingTick()==0 && player.getPersistentData().getBoolean("wantToGiveUp")) {
                var dyingData = ReviveManager.DYING.get(player.getUUID());
                player.hurt(dyingData.damageSource(),Float.MAX_VALUE);
                ReviveManager.DYING.remove(player.getUUID());
                ReviveManager.GIVINGUP.remove(player.getUUID());
                player.getPersistentData().putBoolean("wantToGiveUp",false);
            }
        }
    }
}
