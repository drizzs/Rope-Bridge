package com.bridgebuildersanonymous.ropebridge.util;

import com.bridgebuildersanonymous.ropebridge.util.lib.ModLib;
import com.bridgebuildersanonymous.ropebridge.util.handler.ContentHandler;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSource;
import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.GameData;

public final class ModUtils {


    /**
     * Sends a message to a command sender. Can be used for easier message
     * sending.
     *
     * @param sender  The thing to send the message to. This should probably be a
     *                player.
     * @param message The message to send. This can be a normal message, however
     *                translation keys are HIGHLY encouraged!
     */
    public static void tellPlayer(ICommandSource sender, String message, Object... params) {
        sender.sendMessage(new TranslationTextComponent(message, params));
    }

}