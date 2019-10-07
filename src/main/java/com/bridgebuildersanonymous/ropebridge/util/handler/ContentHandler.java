package com.bridgebuildersanonymous.ropebridge.old.handler;

import com.bridgebuildersanonymous.ropebridge.RopeBridge;
import com.bridgebuildersanonymous.ropebridge.old.network.item.ItemBridgeBuilder;
import com.bridgebuildersanonymous.ropebridge.old.network.item.ItemLadderBuilder;
import com.bridgebuildersanonymous.ropebridge.util.lib.ModLib;
import com.bridgebuildersanonymous.ropebridge.old.network.lib.ModUtils;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RopeBridge.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ContentHandler {


    @SubscribeEvent
    public static void onItemRegistry(final RegistryEvent.Register<Item> event)
    {
        ModLib.itemBridgeBuilder = ModUtils.registerItem(new ItemLadderBuilder(new Item.Properties()), "ladder_builder");
        ModLib.itemBridgeHook = ModUtils.registerItem(new Item(new Item.Properties()), "bridge_builder_material.hook");
        ModLib.itemBarrel = ModUtils.registerItem(new Item(new Item.Properties()), "bridge_builder_material.barrel");
        ModLib.itemHandle = ModUtils.registerItem(new Item(new Item.Properties()), "bridge_builder_material.handle");
        ModLib.itemLadderHook = ModUtils.registerItem(new Item(new Item.Properties()), "ladder_hook");
        ModLib.itemBridgeBuilder = ModUtils.registerItem(new ItemBridgeBuilder(new Item.Properties()), "bridge_builder");
        ModLib.itemRope = ModUtils.registerItem(new Item(new Item.Properties()), "rope");
        ModLib.bridgeicon = ModUtils.registerItem(new Item(new Item.Properties()), "bridgeicon");
    }


}
