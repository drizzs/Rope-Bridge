package com.bridgebuildersanonymous.ropebridge.util.handler.builders;

import java.util.*;

import com.bridgebuildersanonymous.ropebridge.util.handler.ConfigHandler;
import com.bridgebuildersanonymous.ropebridge.util.handler.SlabPosHandler;
import com.bridgebuildersanonymous.ropebridge.util.lib.ModLib;
import com.bridgebuildersanonymous.ropebridge.util.ModUtils;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.block.SlabBlock.TYPE;


public class BridgeBuildingHandler {

    public static List<BlockState> slabsfrominv;

    public static void newBridge(PlayerEntity player, ItemStack stack, int inputType, BlockPos pos1, BlockPos pos2) {
        final LinkedList<SlabPosHandler> bridge = new LinkedList<>();
        boolean allClear = true;
        int x1;
        int x2;
        int y1;
        int y2;
        int z1;
        int z2;
        boolean rotate = getRotate(pos1, pos2);
        if (!rotate) {
            x1 = pos1.getX();
            y1 = pos1.getY();
            z1 = pos1.getZ();
            x2 = pos2.getX();
            y2 = pos2.getY();
            z2 = pos2.getZ();
        } else {
            x1 = pos1.getZ();
            y1 = pos1.getY();
            z1 = pos1.getX();
            x2 = pos2.getZ();
            y2 = pos2.getY();
            z2 = pos2.getX();
        }
        if (Math.abs(z2 - z1) > 3) {
            ModUtils.tellPlayer(player, ModLib.Messages.NOT_CARDINAL);
            return;
        }
        double m;
        double b;
        double distance;
        int distInt;

        m = (double) (y2 - y1) / (double) (x2 - x1);
        if (!ConfigHandler.COMMON.ignoreSlopeWarnings.get() && Math.abs(m) > 0.2) {
            ModUtils.tellPlayer(player, ModLib.Messages.SLOPE_GREAT);
            return;
        }
        b = y1 - m * x1;
        distance = Math.abs(x2 - x1);
        distInt = Math.abs(x2 - x1);
        if (distInt < 2) {
            return;
        }
        if (!hasMaterials(player, distInt - 1)) {
            return;
        }
        for (int x = Math.min(x1, x2) + 1; x <= Math.max(x1, x2) - 1; x++) {
            for (int y = Math.max(y1, y2); y >= Math.min(y1, y2) - distInt / 8 - 1; y--) {
                final double funcVal = m * x + b - distance / 1000 * Math.sin((x - Math.min(x1, x2)) * (Math.PI / distance)) * ConfigHandler.COMMON.bridgeDroopFactor.get() + ConfigHandler.COMMON.bridgeYOffset.get();
                if (y + 0.5 > funcVal && y - 0.5 <= funcVal) {
                    int level;
                    if (funcVal >= y) {
                        if (funcVal >= y + 0.25) {
                            level = 2;
                        } else {
                            level = 1;
                        }
                    } else {
                        if (funcVal >= y - 0.25) {
                            level = 2;
                        } else {
                            level = 1;
                        }
                    }
                    allClear = addSlab(player.world, bridge, x, y + 1, z1, level, rotate) && allClear;
                }
            }
        }

        if (allClear) {
            Hand hand = player.getActiveHand();
            takeMaterials(player, distInt - 1);
            stack.damageItem(ConfigHandler.COMMON.bridgeDamage.get(), player, (p) -> {
                p.sendBreakAnimation(hand);
            });
            if (slabsfrominv.size() > 0) {
                buildBridge(player.world, bridge);
            }
        } else {
            ModUtils.tellPlayer(player, ModLib.Messages.OBSTRUCTED);
        }
    }

    private static boolean getRotate(BlockPos p1, BlockPos p2) {
        return Math.abs(p1.getX() - p2.getX()) <= Math.abs(p1.getZ() - p2.getZ());
    }

    private static boolean hasMaterials(PlayerEntity player, int dist) {
        final int slabsNeeded = dist;
        final int stringNeeded = 1 + dist / 2;
        int slabsHad = 0;
        int stringHad = 0;

        for (int i = 0; i < 36; i++) {
            final ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            final Item item = stack.getItem();
            if (item == ModLib.itemRope) {
                stringHad += stack.getCount();
            }
            if (item.isIn(ItemTags.SLABS)) {
                slabsHad += stack.getCount();
            }
        }
        if (slabsHad >= slabsNeeded && stringHad >= stringNeeded) {
            return true;
        } else {
            ModUtils.tellPlayer(player, ModLib.Messages.UNDERFUNDED_BRIDGE, slabsNeeded, stringNeeded);
            return false;
        }
    }

    private static void takeMaterials(PlayerEntity player, int dist) {
        int slabsNeeded = dist;
        int stringNeeded = 1 + dist / 2;
        int i = 0;

        for (; i < 36; i++) {
            final ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            final Item item = stack.getItem();
            if (item == ModLib.itemRope) {
                if (stack.getCount() > stringNeeded) {
                    stack.shrink(stringNeeded);
                    stringNeeded = 0;
                }
                else if(hasMaterials(player, dist)){
                    int x = stack.getCount();
                    stack.shrink(x);
                    stringNeeded = -x;
                }
            }
            if (item.isIn(ItemTags.SLABS)) {
                if (stack.getCount() > slabsNeeded) {
                    BlockItem itemblock = (BlockItem) item;
                    setSlabsfrominv(slabsNeeded, itemblock.getBlock().getDefaultState());
                    stack.shrink(slabsNeeded);
                    slabsNeeded = 0;
                }
                else if(hasMaterials(player, dist)){
                    int x = stack.getCount();
                    stack.shrink(x);
                    slabsNeeded = -x;
                }
            }
        }
    }

    private static boolean addSlab(World world, LinkedList<SlabPosHandler> list, int x, int y, int z, int level, boolean rotate) {
        boolean isClear;
        BlockPos pos;
        if (rotate) {
            pos = new BlockPos(z, y, x);
        } else {
            pos = new BlockPos(x, y, z);
        }
        isClear = ConfigHandler.COMMON.breakThroughBlocks.get() || world.isAirBlock(pos) || world.getBlockState(pos).canBeReplacedByLogs(world, pos);
        list.add(new SlabPosHandler(pos, level, rotate));
        if (!isClear) {
            spawnSmoke(world, pos, 15);
        }
        return true;
    }

    // Controls if blocks are in the way
    private static void spawnSmoke(World world, BlockPos pos, int times) {

        if (times > 0) {
            world.addParticle(ParticleTypes.EXPLOSION, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
            final World finworld = world;
            final BlockPos finPos = pos;
            final int finTimes = times - 1;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    spawnSmoke(finworld, finPos, finTimes);
                }
            }, 1000);
        }
    }

    private static void buildBridge(final World world, final LinkedList<SlabPosHandler> bridge) {
        SlabPosHandler slab;
        if (!bridge.isEmpty()) {
            slab = bridge.pop();
            BlockState slabstate;
            switch (slab.getLevel()) {
                case 1:
                    slabstate = slabsfrominv.get(1).getBlockState().with(TYPE, SlabType.BOTTOM);
                    break;
                case 2:
                    slabstate = slabsfrominv.get(1).getBlockState().with(TYPE, SlabType.TOP);
                    break;
                default:
                    slabstate = Blocks.AIR.getDefaultState();
                    break;
            }


            if (!world.getBlockState(slab.getBlockPos()).isSolid()) {
                world.setBlockState(slab.getBlockPos(), slabstate, 3);
                spawnSmoke(world, new BlockPos(slab.getBlockPos().getX(), slab.getBlockPos().getY(), slab.getBlockPos().getZ()), 1);
                slabsfrominv.remove(1);
                if(!slabsfrominv.isEmpty()) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            buildBridge(world, bridge);
                        }
                    }, 100);
                }
            }
        }
    }

    public static List<BlockState> setSlabsfrominv(int i, BlockState state) {
        slabsfrominv = new ArrayList<>(i);
        for (int n = 0; n < i; n++) {
            slabsfrominv.add(state);
        }
        return slabsfrominv;
    }

}
