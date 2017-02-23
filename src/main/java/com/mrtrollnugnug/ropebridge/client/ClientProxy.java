package com.mrtrollnugnug.ropebridge.client;

import com.mrtrollnugnug.ropebridge.common.CommonProxy;
import com.mrtrollnugnug.ropebridge.handler.ContentHandler;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		ContentHandler.onClientPreInit();
    }
    
    
}