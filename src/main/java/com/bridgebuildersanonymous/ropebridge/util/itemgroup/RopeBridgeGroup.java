package com.bridgebuildersanonymous.ropebridge.util.itemgroup;

import com.bridgebuildersanonymous.ropebridge.util.lib.ModLib;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class RopeBridgeGroup extends ItemGroup {

    public static final RopeBridgeGroup instance = new RopeBridgeGroup(ItemGroup.GROUPS.length, "ropebridge");

    private RopeBridgeGroup(int index, String label)
    {
        super(index, label);
    }

    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(ModLib.bridgeicon);
    }

}
