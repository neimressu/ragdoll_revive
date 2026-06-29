package com.neimressu.ragdollRevive.Client;

import com.neimressu.ragdollRevive.Network.GiveUpPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.neimressu.ragdollRevive.RagdollRevive.MODID;

@EventBusSubscriber(
        modid = MODID,
        value = Dist.CLIENT
)
public class ClientManager {
    private static boolean oldKey = false;

    @SubscribeEvent
    public static void onCrouchKey(ClientTickEvent.Pre event){
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }
        boolean currentKey = mc.options.keyShift.isDown();
        if (currentKey != oldKey) {
            PacketDistributor.sendToServer(new GiveUpPayload(currentKey));
            oldKey = currentKey;
        }
    }
}