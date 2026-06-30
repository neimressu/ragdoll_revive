package com.neimressu.ragdollRevive.Network;

import com.neimressu.ragdollRevive.Client.TimerHUD;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayLoadHandler {
    public static void handle(TimerPayLoad payload, IPayloadContext context) {
        if (payload.timerType()==1) {
            TimerHUD.timerValue = payload.timerLeft();
        } else if (payload.timerType()==2) {
            TimerHUD.giveUpTimerValue = payload.timerLeft();
        } else if (payload.timerType()==3) {
            TimerHUD.timerValue=TimerHUD.timerValue+(payload.timerLeft()/2); //idk why, but do not remove divide
        }
    }
}
