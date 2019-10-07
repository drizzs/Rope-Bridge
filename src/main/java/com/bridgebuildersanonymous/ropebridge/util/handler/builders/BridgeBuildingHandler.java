package com.bridgebuildersanonymous.ropebridge.builders;

import java.util.*;

import com.bridgebuildersanonymous.ropebridge.util.handler.ConfigHandler;
import com.bridgebuildersanonymous.ropebridge.util.handler.IMaterials;
import com.bridgebuildersanonymous.ropebridge.util.handler.SlabPosHandler;
import com.bridgebuildersanonymous.ropebridge.util.lib.ModLib;
import com.bridgebuildersanonymous.ropebridge.util.ModUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BridgeBuildingHandler implements IMaterials {

    public List<BlockState> slabsfrominv;

    public void newBridge(PlayerEntity player, ItemStack stack, int inputType, BlockPos pos1, BlockPos pos2) {
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
        if (!player.isCreative() && !hasMaterials(player, distInt - 1)) {
            return;
        }
        for (int x = Math.min(x1, x2) + 1; x <= Math.max(x1, x2) - 1; x++) {
            for (int y = Math.max(y1, y2); y >= Math.min(y1, y2) - distInt / 8 - 1; y--) {
                final double funcVal = m * x + b - distance / 1000 * Math.sin((x - Math.min(x1, x2)) * (Math.PI / distance)) * ConfigHandler.COMMON.bridgeDroopFactor.get() + ConfigHandler.COMMON.bridgeYOffset.get();
                if (y + 0.5 > funcVal && y - 0.5 <= funcVal) {
                    int level;
                    if (funcVal >= y) {
                        if (funcVal >= y + 0.25) {
                            level = 4;
                        } else {
                            level = 3;
                        }
                    } else {
                        if (funcVal >= y - 0.25) {
                            level = 2;
                        } else {
                            level = 1;
                        }
                    }
                    allClear = !addSlab(player.world, bridge, x, y + 1, z1, level, rotate) ? false : allClear;
                }
            }
        }

        if (allClear) {
            final int type = inputType == -1 ? getWoodType(player) : inputType;
            if (inputType == -1 && !player.isCreative()) {
                takeMaterials(player, distInt - 1);
                stack.damageItem(ConfigHandler.COMMON.bridgeDamage.get(), stack, stack);
            }
            buildBridge(player.world, bridge, type);
        } else {
            ModUtils.tellPlayer(player, ModLib.Messages.OBSTRUCTED);
            return;
        }
    }

    private static boolean getRotate(BlockPos p1, BlockPos p2) {
        return Math.abs(p1.getX() - p2.getX()) <= Math.abs(p1.getZ() - p2.getZ());
    }

    private static boolean hasMaterials(PlayerEntity player, int dist) {
        boolean noCost = ConfigHandler.COMMON.slabsPerBlock.get() == 0 && ConfigHandler.COMMON.stringPerBlock.get() == 0;
        if (player.isCreative()|| noCost)
            return true;
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

    private void takeMaterials(PlayerEntity player, int dist) {
        boolean noCost = ConfigHandler.COMMON.slabsPerBlock.get() == 0 && ConfigHandler.COMMON.stringPerBlock.get() == 0;
        if (player.isCreative() || noCost) {
            return;
        }
        int slabsNeeded = dist;
        int stringNeeded = 1 + dist / 2;
        int i = 0;

        for (; i < 36; i++) {
            final ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            final BlockItem item = (BlockItem) stack.getItem();
            if (item == ModLib.itemRope) {
                if (stack.getCount() > stringNeeded) {
                	stack.shrink(stringNeeded);
                    stringNeeded = 0;
                }
            } else if (item.isIn(ItemTags.SLABS)) {
                if (stack.getCount() > slabsNeeded) {
                    this.slabsfrominv = new ArrayList<>(slabsNeeded);
                    for (int n = 0; n < slabsNeeded; n++) {
                        slabsfrominv.add(item.getBlock().getDefaultState());
                    }
                    stack.shrink(stringNeeded);
                    slabsNeeded = 0;
                }
            }
        }
    }

    private static boolean addSlab(World world, LinkedList<SlabPosHandler> list, int x, int y, int z, int level, boolean rotate) {
        boolean isClear;
        BlockPos pos;
        if (rotate) {
            pos = new BlockPos(z, y, x);
        }
        else {
            pos = new BlockPos(x, y, z);
        }
        isClear = ConfigHandler.COMMON.breakThroughBlocks.get() || world.isAirBlock(pos) || world.getBlockState(pos).canBeReplacedByLogs(world, pos);
        list.add(new SlabPosHandler(pos, level, rotate));
        if (!isClear) {
            spawnSmoke(world, pos, 15);
        }
        return isClear;
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


	private void buildBridge(final World world, final LinkedList<SlabPosHandler> bridge, final int type) {

        SlabPosHandler slab;
        if (!bridge.isEmpty()) {
            slab = bridge.pop();
            Block block;


            world.setBlockState(slab.getBlockPos(), getSlabsfrominv(), 3);
            spawnSmoke(world, new BlockPos(slab.getBlockPos().getX(), slab.getBlockPos().getY(), slab.getBlockPos().getZ()), 1);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    buildBridge(world, bridge, type);
                }
            }, 100);
        }
    }

    private static int getWoodType(PlayerEntity player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack.isEmpty()) {
                continue;
            }
            Item item = stack.getItem();
            if (item.isIn(ItemTags.SLABS))
                return stack.getDamage();
        }
        return 0;
    }

    @Override
    public List<BlockState> getSlabsfrominv() {
        return this.slabsfrominv;
    }
}
