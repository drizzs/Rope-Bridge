package com.bridgebuildersanonymous.ropebridge.util.handler;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import static com.bridgebuildersanonymous.ropebridge.RopeBridge.MOD_ID;

public class TagHandler
{
    public static final Tag<Item> LADDERS = tag("ladderblocks");


    private static Tag<Item> tag(String name) {
        return new ItemTags.Wrapper(new ResourceLocation(MOD_ID, name));
    }

}
