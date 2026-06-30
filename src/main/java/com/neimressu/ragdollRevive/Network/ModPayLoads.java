package com.neimressu.ragdollRevive.Network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.neimressu.ragdollRevive.RagdollRevive.MODID;

@EventBusSubscriber(modid = MODID)
public class ModPayLoads {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event){
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                GiveUpPayload.TYPE,
                GiveUpPayload.STREAM_CODEC,
                ServerPayLoadHandler::handle
        );

        registrar.playToClient(
                TimerPayLoad.TYPE,
                TimerPayLoad.STREAM_CODEC,
                ClientPayLoadHandler::handle
        );
    }
}