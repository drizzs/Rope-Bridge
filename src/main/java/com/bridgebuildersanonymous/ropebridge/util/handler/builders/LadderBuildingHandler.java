package com.bridgebuildersanonymous.ropebridge.builders;


import com.bridgebuildersanonymous.ropebridge.util.ModUtils;
import com.bridgebuildersanonymous.ropebridge.util.handler.ConfigHandler;
import com.bridgebuildersanonymous.ropebridge.util.handler.ContentHandler;
import com.bridgebuildersanonymous.ropebridge.util.lib.ModLib;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LadderBuildingHandler {


	public static void newLadder(BlockPos start, PlayerEntity player, World world, Direction hitSide,
								 ItemStack builder) {

		if (!hitSide.getAxis().isHorizontal()) {
			ModUtils.tellPlayer(player, ModLib.Messages.BAD_SIDE,
					hitSide == Direction.UP ? I18n.format(ModLib.Messages.TOP) : I18n.format(ModLib.Messages.BOTTOM));
			return;
		}
		if (!world.isSideSolid(start.offset(hitSide.getOpposite()), hitSide)) {
			ModUtils.tellPlayer(player, ModLib.Messages.NOT_SOLID);
			return;
		}

		int count = 0;
		BlockPos lower = start;
		BlockState state = world.getBlockState(lower);
		while (state.getBlock().isReplaceable(world, lower)) {
			count++;
			lower = lower.down();
			state = world.getBlockState(lower);
		}
		if (count <= 0) {
			ModUtils.tellPlayer(player, ModLib.Messages.OBSTRUCTED);
			return;
		}

		int woodNeeded = count * ConfigHandler.COMMON.woodPerBlock.get();
		int ropeNeeded = count * ConfigHandler.COMMON.ropePerBlock.get();
		BlockPlanks.Enum woodType = findType(player);
		Enum type = convertType(woodType);

		if (!player.isCreative()) {
			if (type == null || !hasMaterials(player, woodNeeded, ropeNeeded, woodType)) {
				ModUtils.tellPlayer(player, ModLib.Messages.UNDERFUNDED_LADDER, woodNeeded, ropeNeeded);
				return;
			}
		}

		if (type == null)
			type = Enum.OAK;

		if (!player.isCreative())
			builder.damageItem(ConfigurationHandler.getLadderDamage(), player);

		consume(player, woodNeeded, ropeNeeded, woodType);
		build(world, start, count, hitSide, type);
	}

	private static void build(World world, BlockPos start, int count, final Direction facing, final Enum type) {
		build(world, start, count, 0, facing, type);
	}

	private static void build(final World world, final BlockPos start, final int count, final int it,
							  final Direction facing, final Enum type) {
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
			BlockState state = ContentHandler.blockRopeLadder.getDefaultState()
					.withProperty(RopeLadder.FACING, facing).withProperty(RopeLadder.TYPE, type);
			world.setBlockState(start.down(it), state);
			world.setTileEntity(start.down(it), new TileEntityRopeLadder(type));
		});
		if (it + 1 < count)
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					build(world, start, count, it + 1, facing, type);
				}
			}, 100);
	}

	private static void consume(PlayerEntity player, int woodNeeded, int ropeNeeded,
								net.minecraft.block.BlockPlanks.Enum woodType) {
		boolean noCost = ConfigurationHandler.getRopePerBlock() == 0 && ConfigurationHandler.getWoodPerBlock() == 0;
		if (player.isCreative() || noCost)
			return;
		player.inventory.clearMatchingItems(ContentHandler.itemRope, -1, ropeNeeded, null);
		player.inventory.clearMatchingItems(Item.getItemFromBlock(Blocks.WOODEN_SLAB), woodType.getMetadata(),
				woodNeeded, null);
	}

	private static Enum convertType(BlockPlanks.Enum type) {
		if (type == null)
			return null;
		switch (type) {
			case ACACIA:
				return Enum.ACACIA;
			case BIRCH:
				return Enum.BIRCH;
			case DARK_OAK:
				return Enum.DARK_OAK;
			case JUNGLE:
				return Enum.JUNGLE;
			case OAK:
				return Enum.OAK;
			case SPRUCE:
				return Enum.SPRUCE;
			default:
				return null;
		}
	}

	private static BlockPlanks.Enum findType(PlayerEntity player) {
		boolean noCost = ConfigurationHandler.getRopePerBlock() == 0 && ConfigurationHandler.getWoodPerBlock() == 0;
		if (noCost || player.isCreative())
			return BlockPlanks.Enum.OAK;
		for (ItemStack i : player.inventory.mainInventory) {
			if (i != null && i.getItem() == Item.getItemFromBlock(Blocks.WOODEN_SLAB))
				return (BlockPlanks.Enum) Blocks.WOODEN_SLAB.getTypeForItem(i);
		}
		return null;
	}

	private static boolean hasMaterials(PlayerEntity player, int woodNeeded, int ropeNeeded,
										BlockPlanks.Enum toFind) {
		boolean noCost = ConfigurationHandler.getRopePerBlock() == 0 && ConfigurationHandler.getWoodPerBlock() == 0;
		if (noCost || player.isCreative())
			return true;
		for (ItemStack i : player.inventory.mainInventory) {
			if (i == null)
				continue;
			Item it = i.getItem();
			if (it == ContentHandler.itemRope) {
				ropeNeeded -= i.getCount();
			} else if (it == Item.getItemFromBlock(Blocks.WOODEN_SLAB)
					&& toFind == Blocks.WOODEN_SLAB.getTypeForItem(i)) {
				woodNeeded -= i.getCount();
			}
		}
		return woodNeeded <= 0 && ropeNeeded <= 0;

	}
}
