package com.neimressu.ragdollRevive.Client;

import com.neimressu.ragdollRevive.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import static com.neimressu.ragdollRevive.RagdollRevive.MODID;

@EventBusSubscriber(
        modid = MODID,
        value = Dist.CLIENT
)
public class TimerHUD {
    public static int timerValue = 0;
    public static int giveUpTimerValue = 0;
    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        if (timerValue!=0) {
            timerValue--;
        }
        if (giveUpTimerValue !=0) {
            giveUpTimerValue--;
        }
    }
    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();
        if (timerValue!=0) {
            event.getGuiGraphics().drawCenteredString(
                    mc.font,
                    Component.translatable("text.gui.client.revive_time_left",Math.round((float) timerValue /20)),
                    event.getGuiGraphics().guiWidth()/2 + ClientConfig.REVIVE_TIMER_X_OFFSET.getAsInt(),
                    (event.getGuiGraphics().guiWidth()/2)-120 + ClientConfig.REVIVE_TIMER_Y_OFFSET.getAsInt(),
                    0xFFFF5555
            );
        }
        if (giveUpTimerValue!=0) {
            event.getGuiGraphics().drawCenteredString(
                    mc.font,
                    Component.translatable("text.gui.client.give_up",Math.round((float) giveUpTimerValue /20)),
                    event.getGuiGraphics().guiWidth()/2 + ClientConfig.GIVE_UP_TIMER_X_OFFSET.getAsInt(),
                    (event.getGuiGraphics().guiWidth()/2)-100 + ClientConfig.GIVE_UP_TIMER_Y_OFFSET.getAsInt(),
                    0xFFFF5555
            );
        }
    }
}
