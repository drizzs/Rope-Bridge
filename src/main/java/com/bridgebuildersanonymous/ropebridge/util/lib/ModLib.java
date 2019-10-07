package com.bridgebuildersanonymous.ropebridge.util.lib;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ModLib {
    public static final String MOD_ID = "ropebridge";
    public static final String MOD_NAME = "Rope Bridge Mod";
    public static final String VERSION_NUMBER = "0.0.1";
    public static final String CLIENT_PROXY_CLASS = "com.bridgebuildersanonymous.ropebridge.client.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "com.bridgebuildersanonymous.ropebridge.common.CommonProxy";
    public static final Random RANDOM = new Random();
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    
    public final class Messages {
        public static final String WARNING_BREAKING = "chat.ropebridge.warning.breaking";
        public static final String NOT_ON_GROUND = "chat.ropebridge.info.notonground";
        public static final String NOT_CARDINAL = "chat.ropebridge.info.notcardinal";
        public static final String SLOPE_GREAT = "chat.ropebridge.info.greatslope";
        public static final String OBSTRUCTED = "chat.ropebridge.info.obstruction";
        public static final String UNDERFUNDED_BRIDGE = "chat.ropebridge.info.underfunded_bridge";
        public static final String UNDERFUNDED_LADDER = "chat.ropebridge.info.underfunded_ladder";
        public static final String BAD_SIDE = "chat.ropebridge.info.bad_side";
        public static final String TOP = "chat.ropebridge.params.top";
        public static final String BOTTOM = "chat.ropebridge.params.bottom";
        public static final String NOT_SOLID = "chat.ropebridge.info.not_solid";
    }

    // Blocks
    public static Block blockBridgeSlab1, blockBridgeSlab2, blockBridgeSlab3, blockBridgeSlab4;
    public static Block blockRopeLadder;

    // Items
    public static Item bridgeicon;
    public static Item itemBridgeBuilder, itemLadderBuilder;
    public static Item itemRope, itemBridgeHook, itemBarrel, itemHandle, itemLadderHook;


}
