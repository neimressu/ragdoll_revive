package com.neimressu.ragdollRevive.Network;

import com.neimressu.ragdollRevive.CommonConfig;
import com.neimressu.ragdollRevive.ReviveManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.neimressu.ragdollRevive.RagdollRevive.MODID;

@EventBusSubscriber(modid = MODID)
public class RefreshData {
    private static int refreshTime;
    @SubscribeEvent
    public static void onLoad(ServerStartedEvent event) {
         refreshTime = CommonConfig.NETWORK_REFRESH.getAsInt();
    }

    @SubscribeEvent
    public static void networkRefreshTicks(ServerTickEvent.Pre event){
        if (event.getServer().getTickCount()%refreshTime==0) {
            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
                if (player.getPersistentData().getBoolean("isDying")) {
                    long gameTime =
                            event.getServer()
                                    .overworld()
                                    .getGameTime();
                    PacketDistributor.sendToPlayer(player, new TimerPayLoad(
                            (int) (ReviveManager.DYING.get(player.getUUID()).expireTick() - gameTime),
                            1
                    ));
                    if (player.getPersistentData().getBoolean("isGivingUp")) {
                        PacketDistributor.sendToPlayer(player, new TimerPayLoad(
                                ReviveManager.GIVINGUP.get(player.getUUID()).givingTick(),
                                2
                        ));
                    }
                }
            }
        }
    }
}
