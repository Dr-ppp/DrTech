package com.drppp.drtech.common.Items.armor;

import com.drppp.drtech.Client.render.BreathingApparatusModel;
import com.drppp.drtech.Client.render.ITextureRegistrar;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.drppp.drtech.api.Utils.DrtechUtils.getRL;
import static com.drppp.drtech.common.enent.DimensionBreathabilityHandler.BENEATH_TYPE_ID;
import static com.drppp.drtech.common.enent.DimensionBreathabilityHandler.NETHER_TYPE_ID;
import static net.minecraft.inventory.EntityEquipmentSlot.*;

public class AdvancedBreathingApparatus extends BreathingApparatus implements ITextureRegistrar {
    private final double hoursOfLife;
    private final String name;
    private final int tier;
    private final double relativeAbsorption;

    public AdvancedBreathingApparatus(EntityEquipmentSlot slot, double hoursOfLife, String name, int tier, double relativeAbsorption) {
        super(slot);
        this.hoursOfLife = hoursOfLife;
        this.name = name;
        this.tier = tier;
        this.relativeAbsorption = relativeAbsorption;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "gregtech:textures/armor/" + name + "_" + slot.getName() + ".png";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public @Nullable ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
        if (model == null)
            model = new BreathingApparatusModel(name, armorSlot);
        return model;
    }

    @Override
    public boolean mayBreatheWith(ItemStack stack, EntityPlayer player) {
        return BENEATH_TYPE_ID.contains(player.dimension) || NETHER_TYPE_ID.contains(player.dimension);
    }

    @Override
    public double tryTick(ItemStack stack, EntityPlayer player) {
        this.handleDamage(stack, player);

        ItemStack chest = player.getItemStackFromSlot(CHEST);
        if (chest.getItem() instanceof DrtechArmorItem item) {
            if (item.getItem(chest).getArmorLogic() instanceof AdvancedBreathingApparatus tank && tank.tier == tier) {
                tank.handleDamage(chest, player);

                int piecesCount = processArmorPiece(player, LEGS, tank) + processArmorPiece(player, FEET, tank);

                if (tank.getOxygen(chest) <= 0) {
                    return 1.5;
                } else {
                    tank.changeOxygen(chest, Math.max(-1., tank.getOxygen(chest) - 1.));
                }
                return switch (piecesCount) {
                    case 1 -> 0.5;
                    case 2 -> 0;
                    default -> 1;
                };
            }
        }
        return 2;
    }

    private int processArmorPiece(EntityPlayer player, EntityEquipmentSlot slot, AdvancedBreathingApparatus tank) {
        ItemStack piece = player.getItemStackFromSlot(slot);
        if (piece.getItem() instanceof DrtechArmorItem item) {
            if (item.getItem(piece).getArmorLogic() instanceof AdvancedBreathingApparatus logic && logic.tier == tank.tier) {
                logic.handleDamage(piece, player);
                return 1;
            }
        }
        return 0;
    }


    private double getDamage(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (!stack.getTagCompound().hasKey("damage")) {
            stack.getTagCompound().setDouble("damage", 0);
        }
        return stack.getTagCompound().getDouble("damage");
    }

    private void changeDamage(ItemStack stack, double damageChange) {
        NBTTagCompound compound = stack.getTagCompound();
        compound.setDouble("damage", getDamage(stack) + damageChange);
        stack.setTagCompound(compound);
    }


    private void handleDamage(ItemStack stack, EntityPlayer player) {
        if (hoursOfLife == 0) return;
        if (BENEATH_TYPE_ID.contains(player.dimension) || NETHER_TYPE_ID.contains(player.dimension)) {
            double amount = (1. / (60. * 60. * hoursOfLife));
            changeDamage(stack, amount);
        }

        if (getDamage(stack) == 1) {
            player.renderBrokenItemStack(stack);
        }
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        if (SLOT == CHEST && getMaxOxygen(itemStack) != -1) {
            return getOxygen(itemStack) / getMaxOxygen(itemStack);
        } else {
            if (hoursOfLife > 0) {
                return 1 - getDamage(itemStack);
            }
        }
        return 1;
    }

    @Override
    public float getHeatResistance() {
        return 0.25F;
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, @NotNull ItemStack armor, DamageSource source,
                                                       double damage, EntityEquipmentSlot equipmentSlot) {
        int damageLimit = Integer.MAX_VALUE;
        if (source.isUnblockable()) return new ISpecialArmor.ArmorProperties(0, 0.0, 0);
        return new ISpecialArmor.ArmorProperties(0, getAbsorption(armor) * relativeAbsorption, damageLimit);
    }

    protected float getAbsorption(ItemStack itemStack) {
        return getAbsorption(getEquipmentSlot(itemStack));
    }

    protected float getAbsorption(EntityEquipmentSlot slot) {
        return switch (slot) {
            case HEAD, FEET -> 0.15F;
            case CHEST -> 0.4F;
            case LEGS -> 0.3F;
            default -> 0.0F;
        };
    }


public void addInformation(ItemStack stack, List<String> strings) {
    if (getEquipmentSlot(stack) == CHEST) {
        int maxOxygen = (int) getMaxOxygen(stack);
        int oxygen = (int) getOxygen(stack);

        if (maxOxygen == -1) {
            strings.add(I18n.format("drtech.unlimited_oxygen"));
        } else {
            strings.add(I18n.format("drtech.oxygen", oxygen, maxOxygen));
        }

        // 使用占位符和国际化资源文件
        strings.add(I18n.format("drtech.dimension_applicable", NETHER_TYPE_ID));
        strings.add(I18n.format("drtech.dimension_applicable", BENEATH_TYPE_ID));
    }

    if (hoursOfLife > 0) {
        final double SECONDS_IN_HOUR = 3600; // 定义常量
        double lifetime = SECONDS_IN_HOUR * hoursOfLife;
        int secondsRemaining = (int) (lifetime - getDamage(stack) * lifetime);
        strings.add(I18n.format("drtech.seconds_left", secondsRemaining));
    } else {
        strings.add(I18n.format("drtech.unlimited"));
    }

    int armor = (int) Math.round(20.0F * this.getAbsorption(this.SLOT) * this.relativeAbsorption);
    if (armor > 0) {
        strings.add(I18n.format("attribute.modifier.plus.0", armor, I18n.format("attribute.name.generic.armor")));
    }
}


    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return (int) Math.round(20.0F * this.getAbsorption(armor) * relativeAbsorption);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<ResourceLocation> getTextureLocations() {
        List<ResourceLocation> models = new ArrayList<>();
        models.add(getRL("armor/" + name + "_" + this.SLOT.toString()));
        return models;
    }
}