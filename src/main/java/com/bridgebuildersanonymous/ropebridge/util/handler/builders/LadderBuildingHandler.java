package com.bridgebuildersanonymous.ropebridge.util.handler.builders;


import com.bridgebuildersanonymous.ropebridge.util.ModUtils;
import com.bridgebuildersanonymous.ropebridge.util.handler.ConfigHandler;
import com.bridgebuildersanonymous.ropebridge.util.handler.TagHandler;
import com.bridgebuildersanonymous.ropebridge.util.lib.ModLib;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LadderBuildingHandler {

    public static List<BlockState> laddersfrominv;

    public static void newLadder(BlockPos start, PlayerEntity player, World world, Direction hitSide,
                                 ItemStack builder) {

        if (!hitSide.getAxis().isHorizontal()) {
            ModUtils.tellPlayer(player, ModLib.Messages.BAD_SIDE,
                    hitSide == Direction.UP ? I18n.format(ModLib.Messages.TOP) : I18n.format(ModLib.Messages.BOTTOM));
            return;
        }
        if (!world.getBlockState(start.offset(hitSide.getOpposite())).isSolid()) {
            ModUtils.tellPlayer(player, ModLib.Messages.NOT_SOLID);
            return;
        }

        int count = 0;
        BlockState state = world.getBlockState(start.down(count));
        while (state.isOpaqueCube(world, start.down(count))) {
            count++;
        }
        if (count <= 0) {
            ModUtils.tellPlayer(player, ModLib.Messages.OBSTRUCTED);
            return;
        }

        int laddersNeeded = count;
        int ropeNeeded = count * ConfigHandler.COMMON.ropePerBlock.get();
        if (!hasMaterials(player, laddersNeeded, ropeNeeded)) {
            ModUtils.tellPlayer(player, ModLib.Messages.UNDERFUNDED_LADDER, laddersNeeded, ropeNeeded);
            return;
        }
        Hand hand = player.getActiveHand();
        if(!player.isCreative()) {
            builder.damageItem(ConfigHandler.COMMON.ladderDamage.get(), player, (p) -> {
                p.sendBreakAnimation(hand);
            });
        }
        takeMaterials(player, laddersNeeded);
        build(world, start, count, hitSide);
    }

    private static void build(World world, BlockPos start, int count, final Direction facing) {
        buildLadder(world, start, count, 0, facing);
    }

    private static void buildLadder(final World world, final BlockPos start, final int count, final int it,
                                    final Direction facing) {

        world.setBlockState(start.down(it), laddersfrominv.get(1));
        laddersfrominv.remove(1);
        if (it + 1 < count)
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    buildLadder(world, start, count, it + 1, facing);
                }
            }, 100);
    }

    private static void takeMaterials(PlayerEntity player, int dist) {
        int laddersNeeded = dist;
        int ropeNeeded = 1 + dist / 2;
        int i = 0;

        for (; i < 36; i++) {
            final ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            final Item item = stack.getItem();
            if (item == ModLib.itemRope) {
                if (stack.getCount() > ropeNeeded) {
                    stack.shrink(ropeNeeded);
                    ropeNeeded = 0;
                }
            } else if (item.isIn(TagHandler.LADDERS)) {
                if (stack.getCount() > laddersNeeded) {
                    BlockItem itemblock = (BlockItem) item;
                    setLaddersFromInv(laddersNeeded, itemblock.getBlock().getDefaultState());
                    stack.shrink(laddersNeeded);
                    laddersNeeded = 0;
                }
            }
        }
    }

    private static boolean hasMaterials(PlayerEntity player, int laddersNeeded, int ropeNeeded) {
        int laddersOwned = 0;
        int ropeOwned = 0;
        for (ItemStack i : player.inventory.mainInventory) {
            if (i == null)
                continue;
            Item it = i.getItem();
            if (it == ModLib.itemRope) {
                ropeOwned += i.getCount();
            } else if (it.isIn(TagHandler.LADDERS)) {
                laddersOwned += i.getCount();
            }
        }
        return laddersNeeded <= laddersOwned && ropeNeeded <= ropeOwned;

    }

    public static List<BlockState> setLaddersFromInv(int i, BlockState state) {
        laddersfrominv = new ArrayList<>(i);
        for (int n = 0; n < i; n++) {
            laddersfrominv.add(state);
        }
        return laddersfrominv;
    }
}
