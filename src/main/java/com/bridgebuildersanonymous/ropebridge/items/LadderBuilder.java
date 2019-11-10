package com.bridgebuildersanonymous.ropebridge.items;

import com.bridgebuildersanonymous.ropebridge.RopeBridge;
import com.bridgebuildersanonymous.ropebridge.items.ItemBuilder;
import com.bridgebuildersanonymous.ropebridge.util.handler.builders.LadderBuildingHandler;
import com.bridgebuildersanonymous.ropebridge.util.network.RopeBridgePacketHandler;
import com.bridgebuildersanonymous.ropebridge.util.lib.LadderMessage;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class LadderBuilder extends ItemBuilder {
	
    public LadderBuilder(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity && world.isRemote) {
            final PlayerEntity player = (PlayerEntity) entityLiving;
            if (this.getUseDuration(stack) - timeLeft > 10) {
                final BlockRayTraceResult hit = (BlockRayTraceResult) trace(player);
                if (hit.getType() == Type.BLOCK) {
                    final BlockPos start = hit.getPos();
                    RopeBridge.LOGGER.info(start);
                    BlockPos pos = player.getPosition();
                    Direction direction = player.getHorizontalFacing();
                    RopeBridge.LOGGER.info(direction);
                    PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(
                            pos.getX(), pos.getY(), pos.getZ(), 500, world.dimension.getType());
                    RopeBridgePacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> point),
                            new LadderMessage(start.offset(direction), direction));
                    LadderBuildingHandler.newLadder(start.offset(direction), player, world, direction, stack);

                }
            }
        }
    }
}
