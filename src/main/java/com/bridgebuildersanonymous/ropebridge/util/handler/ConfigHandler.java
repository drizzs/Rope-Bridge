package com.bridgebuildersanonymous.ropebridge.util.handler;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHandler {

    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class CommonConfig {
        public final ForgeConfigSpec.IntValue maxBridgeDistance;
        public final ForgeConfigSpec.IntValue bridgeDroopFactor;
        public final ForgeConfigSpec.DoubleValue bridgeYOffset;
        public final ForgeConfigSpec.BooleanValue breakThroughBlocks;
        public final ForgeConfigSpec.BooleanValue ignoreSlopeWarnings;
        public final ForgeConfigSpec.IntValue slabsPerBlock;
        public final ForgeConfigSpec.IntValue stringPerBlock;
        public final ForgeConfigSpec.IntValue woodPerBlock;
        public final ForgeConfigSpec.IntValue ropePerBlock;
        public final ForgeConfigSpec.IntValue bridgeDamage;
        public final ForgeConfigSpec.IntValue ladderDamage;
        CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.push("BridgeControl");
            maxBridgeDistance = builder
                    .comment("Max length of bridges made be Grappling Gun.")
                    .defineInRange("maxBridgeDistance", 400, 1, 1000);
            bridgeDroopFactor = builder
                    .comment("Percent of slack the bridge will have, causing it to hang.")
                    .defineInRange("bridgeDroopFactor", 100, 0, 100);
            bridgeYOffset = builder
                    .comment( "Generated bridges will be raised or lowered by this ammount in blocks.\nDefault is just below user's feet.")
                    .defineInRange("bridgeYOffset", -0.3F, -1.00F, 1.00F);
            breakThroughBlocks = builder
                    .comment("If enabled, all blocks that dare stand in a bridge's way will be broken.\nVery useful in creative mode.")
                    .define("breakThroughBlocks", false);
            ignoreSlopeWarnings = builder
                    .comment("Set true to ignore all slope warnings and allow building of very steep bridges.")
                    .define("ignoreSlopeWarnings", false);
            builder.pop();

            builder.push("MaterialControl");
            slabsPerBlock = builder
                    .comment("Slabs consumed for each bridge block built.")
                    .defineInRange("slabsPerBlock", 1, 0, 10);
            stringPerBlock = builder
                    .comment("String consumed for each bridge block built.")
                    .defineInRange("stringPerBlock", 2, 0, 20);
            woodPerBlock = builder
                    .comment("Wood consumed for each ladder block built.")
                    .defineInRange("woodPerBlock", 1, 1, 10);
            ropePerBlock = builder
                    .comment("Rope consumed for each ladder block built.")
                    .defineInRange("woodPerBlock", 2, 1, 20);
            builder.pop();

            builder.push("ItemDamage");
            bridgeDamage = builder
                    .comment("How much the Ladder Gun is damaged after creating each ladder.")
                    .defineInRange("bridgeDamage", 1, 0, 64);
            ladderDamage = builder
                    .comment("How much the Bridge Gun is damaged after creating each ladder.")
                    .defineInRange("ladderDamage", 1, 0, 64);

        }
    }

}
