package com.neimressu.ragdollRevive;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public class Config {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue REVIVE_TIME;
    public static final ModConfigSpec.DoubleValue HEALTH_AFTER_REVIVE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> REVIVE_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_DAMAGE;
    public static final ModConfigSpec.ConfigValue<List<?extends String>> EXTEND_ITEMS;
    public static final ModConfigSpec.BooleanValue INVULNERABLE_IN_CRIT_STATE;
    public static final ModConfigSpec.ConfigValue<String> REVIVE_SOUND;
    public static final ModConfigSpec.IntValue GIVE_UP_TIMER;

    static {
        BUILDER.push("Revive Settings");

        REVIVE_TIME = BUILDER
                .comment("Time to revive a player in ticks")
                .defineInRange("reviveTime", 600, 1, Integer.MAX_VALUE);
        HEALTH_AFTER_REVIVE = BUILDER
                .comment("How much health player has after revive")
                .defineInRange("healthAfterRevive", 6.0, 0.0,Float.MAX_VALUE);
        REVIVE_ITEMS = BUILDER
                .comment("Items that revive player")
                .defineList("reviveItems",
                        List.of("minecraft:heart_of_the_sea", "minecraft:honey_bottle"),
                        obj -> obj instanceof String);
        EXCLUDED_DAMAGE = BUILDER
                .comment("Damage that will always kill")
                .defineList("excludedDamage",
                        List.of("minecraft:fall", "minecraft:generic_kill"),
                        obj -> obj instanceof String);
        EXTEND_ITEMS = BUILDER
                .comment("Items that extends revive time in ticks")
                .comment("Example: 'minecraft:golden_apple:1600'. Will increase revive time by 1600 ticks")
                .defineList("extendItems",
                        List.of("minecraft:paper:100"),
                        obj -> obj instanceof String);
        INVULNERABLE_IN_CRIT_STATE = BUILDER
                .comment("Odes player invulnerable while  in crit state")
                        .define("isInvulnerableInCritState",true);
        REVIVE_SOUND = BUILDER
                .comment("Sound that revived player hears upon reviving. Set to DISABLE if you dont want it")
                        .define("reviveSound","minecraft:block.conduit.deactivate");
        GIVE_UP_TIMER = BUILDER
                .comment("Time in ticks that takes for player to give up")
                        .defineInRange("giveUpTimer",100,1,Integer.MAX_VALUE);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}