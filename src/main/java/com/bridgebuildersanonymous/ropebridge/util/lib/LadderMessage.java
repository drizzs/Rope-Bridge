package com.bridgebuildersanonymous.ropebridge.old.network;

import com.bridgebuildersanonymous.ropebridge.old.handler.BridgeBuildingHandler;
import com.bridgebuildersanonymous.ropebridge.old.handler.LadderBuildingHandler;


import com.bridgebuildersanonymous.ropebridge.util.lib.BridgeMessage;
import com.sun.java.util.jar.pack.ConstantPool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class LadderMessage {


    private BlockPos from;
    private Direction side;

    private boolean failed;

    
    public LadderMessage(BlockPos from, Direction side) {
        this.from = from;
        this.side = side;
        this.failed = false;
    }

    public LadderMessage(boolean failed) {
        this.failed = failed;
    }

    public LadderMessage read(ByteBuf buf) {
        try {
            this.from = BlockPos.fromLong(buf.readLong());
            this.side = Direction.byIndex(buf.readByte());
            return new LadderMessage(new BlockPos(from), new Direction(side));
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return new LadderMessage(false);
        }
    }

    public static void write(LadderMessage msg, ByteBuf buf) {
        buf.writeLong(msg.from.toLong());
        buf.writeByte(msg.side.getIndex());
    }


    public static void handle(LadderMessage msg, Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleClient(msg, ctx.get()));
    }


    @OnlyIn(Dist.CLIENT)
    private static void handleClient(LadderMessage msg, NetworkEvent.Context ctx) {
        if (!msg.failed) {

            World world = Minecraft.getInstance().world;
            if (world != null) {
                final PlayerEntity player = ctx.getSender();
                Minecraft.getInstance().deferTask(() -> LadderBuildingHandler.newLadder(msg.from, player, world, msg.side, player.getHeldItemMainhand()));

            }
        }
        ctx.setPacketHandled(true);
    }

}
