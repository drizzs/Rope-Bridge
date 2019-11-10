package com.bridgebuildersanonymous.ropebridge.util.lib;

import com.bridgebuildersanonymous.ropebridge.util.handler.builders.LadderBuildingHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public static LadderMessage read(ByteBuf buf) {
        try {
            BlockPos from = BlockPos.fromLong(buf.readLong());
            Direction side = Direction.byIndex(buf.readByte());
            return new LadderMessage(from, side);
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
