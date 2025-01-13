package com.drppp.drtech.common.Items.armor;

import com.drppp.drtech.Client.render.ITextureRegistrar;
import com.drppp.drtech.api.ItemHandler.IBreathingArmorLogic;
import com.drppp.drtech.api.ItemHandler.IBreathingItem;
import gregtech.api.items.armor.ArmorMetaItem;
import gregtech.api.items.metaitem.stats.IItemComponent;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DrtechArmorItem extends ArmorMetaItem<DrtechArmorItem.SuSyArmorMetaValueItem> implements IBreathingItem {


    @Override
    public boolean isValid(ItemStack stack, EntityPlayer player) {
        return getItem(stack).armorLogic.mayBreatheWith(stack, player);
    }

    @Override
    public double tryTick(ItemStack stack, EntityPlayer player) {
        return getItem(stack).armorLogic.tryTick(stack, player);
    }

    public class SuSyArmorMetaValueItem extends ArmorMetaItem<SuSyArmorMetaValueItem>.ArmorMetaValueItem {
        private IBreathingArmorLogic armorLogic = null;
        protected SuSyArmorMetaValueItem(int metaValue, String unlocalizedName) {
            super(metaValue, unlocalizedName);
            this.setMaxStackSize(1);
        }

        public SuSyArmorMetaValueItem setArmorLogic(IBreathingArmorLogic armorLogic) {
            super.setArmorLogic(armorLogic);
            this.armorLogic = armorLogic;
            if (armorLogic instanceof IItemComponent)
                this.addComponents((IItemComponent) armorLogic);
            return this;
        }

    }

    protected SuSyArmorMetaValueItem constructMetaValueItem(short metaValue, String unlocalizedName) {
        return new SuSyArmorMetaValueItem(metaValue, unlocalizedName);
    }

    public void registerIngameModels(TextureMap map) {
        for (SuSyArmorMetaValueItem item : this.getAllItems()) {
            if (item.getArmorLogic() instanceof ITextureRegistrar registrar) {
                for (ResourceLocation model : registrar.getTextureLocations()) {
                    map.registerSprite(model);
                }
            }
        }
    }
}