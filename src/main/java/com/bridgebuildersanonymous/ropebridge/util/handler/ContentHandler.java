package com.bridgebuildersanonymous.ropebridge.util.handler;

import com.bridgebuildersanonymous.ropebridge.RopeBridge;
import com.bridgebuildersanonymous.ropebridge.items.BridgeBuilder;
import com.bridgebuildersanonymous.ropebridge.items.LadderBuilder;
import com.bridgebuildersanonymous.ropebridge.util.itemgroup.RopeBridgeGroup;
import com.bridgebuildersanonymous.ropebridge.util.lib.ModLib;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = RopeBridge.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ContentHandler {


    @SubscribeEvent
    public static void onItemRegistry(final RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(ModLib.itemBridgeBuilder);;
        ModLib.itemBridgeHook = registerItem(event.getRegistry(), new Item(new Item.Properties().group(RopeBridgeGroup.instance)), "bridge_builder_material.hook");
        ModLib.itemBarrel = registerItem( event.getRegistry(), new Item(new Item.Properties().group(RopeBridgeGroup.instance)), "bridge_builder_material.barrel");
        ModLib.itemHandle = registerItem(event.getRegistry(), new Item(new Item.Properties().group(RopeBridgeGroup.instance)), "bridge_builder_material.handle");
        ModLib.itemLadderHook = registerItem(event.getRegistry(), new Item(new Item.Properties().group(RopeBridgeGroup.instance)), "ladder_hook");
        ModLib.itemBridgeBuilder = registerItem(event.getRegistry(), new BridgeBuilder(new Item.Properties().group(RopeBridgeGroup.instance)), "bridge_builder");
        ModLib.itemLadderBuilder = registerItem(event.getRegistry(), new LadderBuilder(new Item.Properties().group(RopeBridgeGroup.instance)), "bridge_builder");
        ModLib.itemRope = registerItem(event.getRegistry(), new Item(new Item.Properties().group(RopeBridgeGroup.instance)), "rope");
        ModLib.bridgeicon = registerItem(event.getRegistry(), new Item(new Item.Properties()), "bridgeicon");
    }

    public static Item registerItem(IForgeRegistry<Item> registry, Item item, String name) {
        item.setRegistryName(name);
        registry.register(item);
        return item;
    }
}
