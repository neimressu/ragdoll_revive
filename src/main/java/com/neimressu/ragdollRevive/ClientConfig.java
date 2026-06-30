package com.neimressu.ragdollRevive;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue REVIVE_TIMER_X_OFFSET;
    public static final ModConfigSpec.IntValue REVIVE_TIMER_Y_OFFSET;

    public static final ModConfigSpec.IntValue GIVE_UP_TIMER_X_OFFSET;
    public static final ModConfigSpec.IntValue GIVE_UP_TIMER_Y_OFFSET;

    static {
        BUILDER.push("Client revive settings");
        REVIVE_TIMER_X_OFFSET = BUILDER
                .comment("Revive timer X offset")
                .defineInRange("reviveOffsetX",0,Integer.MIN_VALUE,Integer.MAX_VALUE);
        REVIVE_TIMER_Y_OFFSET = BUILDER
                .comment("Revive timer Y offset")
                .defineInRange("reviveOffsetY",0,Integer.MIN_VALUE,Integer.MAX_VALUE);
        GIVE_UP_TIMER_X_OFFSET = BUILDER
                .comment("Give up timer X offset")
                .defineInRange("giveUpOffsetX",0,Integer.MIN_VALUE,Integer.MAX_VALUE);
        GIVE_UP_TIMER_Y_OFFSET = BUILDER
                .comment("Give up timer Y offset")
                .defineInRange("giveUpOffsetY",0,Integer.MIN_VALUE,Integer.MAX_VALUE);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
