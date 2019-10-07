package com.bridgebuildersanonymous.ropebridge.items;

import java.math.BigDecimal;

import com.bridgebuildersanonymous.ropebridge.util.handler.ConfigHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.security.auth.login.Configuration;

public abstract class ItemBuilder extends Item {

	public ItemBuilder(Properties properties) {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            playerIn.setActiveHand(hand);
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    @Override
    public abstract void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entityLiving, int timeLeft);

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public static boolean equalsZero(double d) {
        return BigDecimal.valueOf(d).equals(BigDecimal.ZERO);
    }



    public static RayTraceResult trace(PlayerEntity player) {
        return player.func_213324_a(ConfigHandler.COMMON.maxBridgeDistance.get(), 1.0f, true);
    }
}
