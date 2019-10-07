package com.bridgebuildersanonymous.ropebridge.util.lib;

import com.bridgebuildersanonymous.ropebridge.util.handler.builders.BridgeBuildingHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BridgeMessage {

    private BlockPos from;
    private BlockPos to;

    private boolean failed;

    public BridgeMessage(BlockPos to,BlockPos from) {
        this.from = from;
        this.to = to;
        this.failed = false;
    }

    public BridgeMessage(boolean failed) {
        this.failed = failed;
    }
    public static BridgeMessage read (ByteBuf buf) {
        try {
            BlockPos from = BlockPos.fromLong(buf.readLong());
            BlockPos to = BlockPos.fromLong(buf.readLong());
            return new BridgeMessage(to, from);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return new BridgeMessage(false);
        }
    }


    public static void write(BridgeMessage msg, ByteBuf buf) {
        buf.writeLong(msg.from.toLong());
        buf.writeLong(msg.to.toLong());
    }

    public static void handle(BridgeMessage msg, Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleClient(msg, ctx.get()));
    }


    @OnlyIn(Dist.CLIENT)
    private static void handleClient(BridgeMessage msg, NetworkEvent.Context ctx) {
        if (!msg.failed) {

            World world = Minecraft.getInstance().world;
            if (world != null) {
                final PlayerEntity player = ctx.getSender();
                Minecraft.getInstance().deferTask(() -> BridgeBuildingHandler.newBridge(player, player.getHeldItemMainhand(), -1, msg.from, msg.to));

            }
        }
        ctx.setPacketHandled(true);
    }

}
