package com.drppp.drtech.common.Items.armor;

import com.drppp.drtech.Client.render.ITextureRegistrar;
import com.drppp.drtech.Client.render.SimpleBreathingApparatusModel;
import com.drppp.drtech.api.ItemHandler.IBreathingArmorLogic;
import com.drppp.drtech.common.enent.DimensionBreathabilityHandler;
import gregtech.api.items.metaitem.stats.IItemDurabilityManager;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.drppp.drtech.api.Utils.DrtechUtils.getRL;
import static com.drppp.drtech.common.enent.DimensionBreathabilityHandler.NETHER_TYPE_ID;
import static net.minecraft.inventory.EntityEquipmentSlot.CHEST;

public class BreathingApparatus implements IBreathingArmorLogic, IItemDurabilityManager, ITextureRegistrar {
    protected final EntityEquipmentSlot SLOT;
    @SideOnly(Side.CLIENT)
    protected ModelBiped model;

    public BreathingApparatus(EntityEquipmentSlot slot) {
        SLOT = slot;
    }

    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack itemStack) {
        return SLOT;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return switch (SLOT) {
            case HEAD -> "gregtech:textures/armor/gas_mask.png";
            case CHEST -> "gregtech:textures/armor/gas_tank.png";
            default -> null;
        };
    }

    @SideOnly(Side.CLIENT)
    @Override
    public @Nullable ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
        if (model == null)
            model = new SimpleBreathingApparatusModel("gas", armorSlot);
        return model;
    }

@Override
public boolean mayBreatheWith(ItemStack stack, EntityPlayer player) {
    // 检查维度是否在允许的范围内
    if (!NETHER_TYPE_ID.contains(player.dimension)) {
        return false;
    }

    // 获取玩家穿戴的胸甲
    ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
    if (chest.isEmpty()) {
        return false;
    }

    // 获取胸甲对应的物品实例
    Item chestItem = chest.getItem();

    if (chestItem instanceof DrtechArmorItem item) {
        if (item.getItem(chest).getArmorLogic() instanceof BreathingApparatus tank) {
            return tank.getOxygen(stack) > 0;
        }
    }

    return false;
}


    @Override
    public double tryTick(ItemStack stack, EntityPlayer player) {
        boolean isInHazardousEnvironment = DimensionBreathabilityHandler.isInHazardousEnvironment(player);
        if (!isInHazardousEnvironment) {
            return 0;
        }

        // 获取胸甲槽位的物品堆栈
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof DrtechArmorItem item)) {
            return 0.5;
        }

        if (item.getItem(chest).getArmorLogic() instanceof BreathingApparatus tank) {
            tank.changeOxygen(stack, -1);
            return 0;
        }
        return 0.5;
    }

    @Override
    public void addInformation(ItemStack stack, List<String> tooltips) {
        if (getEquipmentSlot(stack) == CHEST) {
            int oxygen = (int) getOxygen(stack);
            int maxOxygen = (int) getMaxOxygen(stack);
            tooltips.add(I18n.format("gregtech.oxygen", oxygen, maxOxygen));
            tooltips.add(I18n.format("drtech.dimension_applicable：" + NETHER_TYPE_ID));
        }
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        if (SLOT == EntityEquipmentSlot.CHEST) {
            return (getOxygen(itemStack) / getMaxOxygen(itemStack));
        } else {
            return 1;
        }
    }

    double getOxygen(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (!stack.getTagCompound().hasKey("oxygen")) {
            stack.getTagCompound().setDouble("oxygen", getMaxOxygen(stack));
        }
        return stack.getTagCompound().getDouble("oxygen");
    }

    double getMaxOxygen(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (!stack.getTagCompound().hasKey("maxOxygen")) {
            stack.getTagCompound().setDouble("maxOxygen", 1200);
        }
        return stack.getTagCompound().getDouble("maxOxygen");
    }

    void changeOxygen(ItemStack stack, double oxygenChange) {
        NBTTagCompound compound = stack.getTagCompound();
        compound.setDouble("oxygen", getOxygen(stack) + oxygenChange);
        stack.setTagCompound(compound);
    }

    @Override
    public List<ResourceLocation> getTextureLocations() {
        List<ResourceLocation> models = new ArrayList<>();
        switch (SLOT) {
            case HEAD -> models.add(getRL("armor/gas_mask"));
            case CHEST -> models.add(getRL("armor/gas_tank"));
        }
        return models;
    }
}