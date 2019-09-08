package com.bridgebuildersanonymous.ropebridge.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityRopeLadder extends TileEntity {
    private RopeLadder.EnumType type;
    private static final String typeKey = "type";
    
    public TileEntityRopeLadder(RopeLadder.EnumType type) {
        setType(type);
    }

    public TileEntityRopeLadder() {
        this(RopeLadder.EnumType.OAK);
    }

    public RopeLadder.EnumType getType() {
        return type;
    }

    public void setType(RopeLadder.EnumType type) {
        this.type = type;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return newSate.getBlock() != oldState.getBlock();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (type != null) {
            compound.setInteger(typeKey, type.meta);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(typeKey)) {
            setType(RopeLadder.EnumType.fromMeta(compound.getInteger(typeKey)));
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
}
