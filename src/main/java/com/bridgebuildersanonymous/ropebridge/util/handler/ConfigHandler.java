package com.bridgebuildersanonymous.ropebridge.util.handler

import net.minecraftforge.common.ForgeConfigSpec
import org.apache.commons.lang3.tuple.Pair

object ConfigHandler {

    val COMMON: CommonConfig
    val COMMON_SPEC: ForgeConfigSpec

    init {
        val specPair = ForgeConfigSpec.Builder().configure<CommonConfig>(Function<Builder, CommonConfig> { CommonConfig(it) })
        COMMON_SPEC = specPair.right
        COMMON = specPair.left
    }

    class CommonConfig internal constructor(builder: ForgeConfigSpec.Builder) {
        private val maxBridgeDistance: ForgeConfigSpec.IntValue
        private val bridgeDroopFactor: ForgeConfigSpec.IntValue? = null
        private val slabsPerBlock: ForgeConfigSpec.IntValue? = null
        private val stringPerBlock: ForgeConfigSpec.IntValue? = null
        private val woodPerBlock: ForgeConfigSpec.IntValue? = null
        private val ropePerBlock: ForgeConfigSpec.IntValue? = null
        private val bridgeDamage: ForgeConfigSpec.IntValue? = null
        private val ladderDamage: ForgeConfigSpec.IntValue? = null

        init {
            builder.push("BridgeControl")
            maxBridgeDistance = builder
                    .comment("The minimum height to spawn Test Ore at.")
                    .defineInRange("maxBridgeDistance", 1, 0, 256)
            maxTestOreSpawnHeight = builder
                    .comment("The maximum height to spawn Test Ore at.")
                    .defineInRange("maxTestOreSpawnHeight", 73, 0, 256)
            chanceToSpawnTestOre = builder
                    .comment("Controls the chance to spawn Test Ore in world generation.")
                    .defineInRange("chanceToSpawnTestOre", 20, 1, 100)
            maxTestOreVeinSize = builder
                    .comment("The maximum number of ores per vein. Will Spawn half of number indicated. 10 = 5 ore.")
                    .defineInRange("maxTestOreVeinSize", 10, 1, 100)
            builder.pop()
        }

        companion object {
            private val bridgeYOffset: Float = 0.toFloat()
            private val breakThroughBlocks: Boolean = false
            private val ignoreSlopeWarnings: Boolean = false
        }
    }

}
