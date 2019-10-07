package com.bridgebuildersanonymous.ropebridge.util;

import com.bridgebuildersanonymous.ropebridge.old.handler.ContentHandler;
import com.bridgebuildersanonymous.ropebridge.util.lib.ModLib;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class RopeBridgeGroup extends ItemGroup {
    public static final RopeBridgeGroup instance = new RopeBridgeGroup(ItemGroup.GROUPS.length, "occult");

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
