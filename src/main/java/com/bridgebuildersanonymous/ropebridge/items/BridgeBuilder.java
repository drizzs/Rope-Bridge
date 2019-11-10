package com.bridgebuildersanonymous.ropebridge.items;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import com.bridgebuildersanonymous.ropebridge.RopeBridge;
import com.bridgebuildersanonymous.ropebridge.items.ItemBuilder;
import com.bridgebuildersanonymous.ropebridge.util.handler.builders.BridgeBuildingHandler;
import com.bridgebuildersanonymous.ropebridge.util.network.RopeBridgePacketHandler;
import com.bridgebuildersanonymous.ropebridge.util.lib.ModLib;
import com.bridgebuildersanonymous.ropebridge.util.ModUtils;
import com.bridgebuildersanonymous.ropebridge.util.lib.BridgeMessage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

public class BridgeBuilder extends ItemBuilder {

    public BridgeBuilder(Properties properties) {
        super(properties);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        super.onUsingTick(stack, player, count);
        if (player.world.isRemote && player instanceof PlayerEntity) {
            final PlayerEntity p = (PlayerEntity) player;
            rotatePlayerTowards(p, getNearestYaw(p));
        }
    }

    private static void rotatePlayerTowards(PlayerEntity player, float target) {
        float yaw = player.rotationYaw % 360;
        if (yaw < 0) {
            yaw += 360;
        }
        rotatePlayerTo(player, yaw + (target - yaw) / 4);
    }

    private static void rotatePlayerTo(PlayerEntity player, float yaw) {
        final float original = player.rotationYaw;
        player.rotationYaw = yaw;
        player.prevRotationYaw += player.rotationYaw - original;
    }

    private static float getNearestYaw(PlayerEntity player) {
        float yaw = player.rotationYaw % 360;
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw < 45) {
            return 0F;
        }
        if (yaw > 45 && yaw <= 135) {
            return 90F;
        } else if (yaw > 135 && yaw <= 225) {
            return 180F;
        } else if (yaw > 225 && yaw <= 315) {
            return 270F;
        } else {
            return 360F;
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity && world.isRemote) {
            final PlayerEntity player = (PlayerEntity) entityLiving;
            if (this.getUseDuration(stack) - timeLeft > 10) {
                if (!player.onGround) {
                    ModUtils.tellPlayer(player, ModLib.Messages.NOT_ON_GROUND);
                } else {
                    final BlockRayTraceResult hit = (BlockRayTraceResult) trace(player);
                    if (hit.getType() == Type.BLOCK) {
                        final BlockPos floored = new BlockPos(Math.floor(player.posX), Math.floor(player.posY) - 1, Math.floor(player.posZ)).down();
                        BlockPos target = hit.getPos();
                        PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(
                                floored.getX(), floored.getY(), floored.getZ(), 500, world.dimension.getType());
                        RopeBridge.LOGGER.info(hit + "areaPoint");
                        RopeBridgePacketHandler.INSTANCE.send((PacketDistributor.NEAR.with(() -> point)),
                                new BridgeMessage(floored, target));
                        BridgeBuildingHandler.newBridge(player, player.getHeldItemMainhand(), -1, target, floored);
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
        World world = player.world;
        BlockState state = world.getBlockState(pos);
        if (!world.isRemote && player.isSneaking() && isBridgeBlock(state.getBlock())) {
            ModUtils.tellPlayer(player, ModLib.Messages.WARNING_BREAKING);
            breakBridge(player, player.world, pos);
        }
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        World world = Minecraft.getInstance().world;
        PlayerEntity player = Minecraft.getInstance().player;
        if (!world.isRemote) {
            if (player.isSneaking() && isBridgeBlock(state.getBlock())) {
                return 0.3F;
            }
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("- Hold right-click to build"));
        tooltip.add(new TranslationTextComponent("- Sneak to break whole bridge"));
    }

    private static boolean isBridgeBlock(Block blockIn) {
        return blockIn.isIn(BlockTags.SLABS);
    }

    /**
     * Breaks block at position posIn and recursively spreads to in-line
     * neighbors
     *
     * @param posIn the position of the block to start breaking bridge from
     */
    private static void breakBridge(final PlayerEntity player, final World worldIn, final BlockPos posIn) {
        Minecraft.getInstance().deferTask(() -> {
            int xRange = 0;
            int zRange = 0;

            Queue<BlockPos> newQueue = new LinkedList<>();
            newQueue.add(posIn);
            Queue<BlockPos> queue = new LinkedList<>();
            queue.add(posIn);

            while (!newQueue.isEmpty()) {
                BlockPos pos = newQueue.remove();
                for (int x = pos.getX() - xRange; x <= pos.getX() + xRange; x++) {
                    for (int y = pos.getY() - 1; y <= pos.getY() + 1; y++) {
                        for (int z = pos.getZ() - zRange; z <= pos.getZ() + zRange; z++) {
                            final BlockPos currentPos = new BlockPos(x, y, z);
                            if ((x - pos.getX() == 0 && z - pos.getZ() == 0) || queue.contains(currentPos)) {
                            } else {
                                final BlockState currentBlockState = worldIn.getBlockState(currentPos);
                                if (isBridgeBlock(currentBlockState.getBlock())) {
                                    newQueue.add(currentPos);
                                }
                            }
                        }
                    }
                }

                queue.add(pos);
            }
            Timer timer = new Timer();
            TimerTask task = new BreakTask(queue, worldIn, timer, !player.isCreative());
            timer.schedule(task, 100, 100);
        });
    }

    private static class BreakTask extends TimerTask {
        private final Queue<BlockPos> queue;
        private final World world;
        private final Timer timer;
        private final boolean drop;

        public BreakTask(Queue<BlockPos> queue, World world, Timer timer, boolean drop) {
            super();
            this.queue = queue;
            this.world = world;
            this.timer = timer;
            this.drop = drop;
        }

        @Override
        public void run() {
            BlockPos pos = queue.remove();
            if (world.getBlockState(pos).getBlock().isIn(BlockTags.SLABS)) {
                Minecraft.getInstance().deferTask(() -> world.destroyBlock(pos, drop));
            }
            if (queue.isEmpty()) {
                timer.cancel();
            }
        }
    }
}
