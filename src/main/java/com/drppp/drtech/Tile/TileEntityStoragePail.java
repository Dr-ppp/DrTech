package com.drppp.drtech.Tile;

import com.drppp.drtech.api.TileEntity.TileEntityWithUI;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.IUIHolder;
import gregtech.api.gui.ModularUI;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.client.renderer.texture.Textures;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class TileEntityStoragePail extends TileEntityWithUI {

    public  ItemStackHandler inventory = new ItemStackHandler(243);
    public TileEntityStoragePail(){

    }
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(compound.hasKey("StoragePail"))
            inventory.deserializeNBT(compound.getCompoundTag("StoragePail"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("StoragePail",inventory.serializeNBT());
        return compound;
    }

    @Override
    public ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder;
        builder = ModularUI.builder(GuiTextures.BACKGROUND, 198, 208);
        for (int i = 0; i < 20; i++) {
            builder.slot(inventory,i,i%10*9,i/10*9,true,true, GuiTextures.SLOT);
        }
        builder.bindPlayerInventory(entityPlayer.inventory, 180);
        return builder.build(this, entityPlayer);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability== CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {

    }

}