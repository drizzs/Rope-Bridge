package com.bridgebuildersanonymous.ropebridge.util.network;

import com.bridgebuildersanonymous.ropebridge.util.lib.BridgeMessage;
import com.bridgebuildersanonymous.ropebridge.util.lib.LadderMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import static com.bridgebuildersanonymous.ropebridge.RopeBridge.MOD_ID;

public class RopeBridgePacketHandler
{
    public static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MOD_ID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();


    public static void init() {
        INSTANCE.registerMessage(0, BridgeMessage.class, BridgeMessage::write,
                BridgeMessage::read, BridgeMessage::handle);
        INSTANCE.registerMessage(0, LadderMessage.class, LadderMessage::write,
                LadderMessage::read, LadderMessage::handle);
    }

}
