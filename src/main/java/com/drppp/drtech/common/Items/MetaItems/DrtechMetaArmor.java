package com.drppp.drtech.common.Items.MetaItems;


import com.drppp.drtech.common.Items.armor.*;

import static net.minecraft.inventory.EntityEquipmentSlot.*;
import static net.minecraft.inventory.EntityEquipmentSlot.FEET;
public class DrtechMetaArmor extends DrtechArmorItem {
    @Override
    public void registerSubItems() {
        MyMetaItems.SIMPLE_GAS_MASK = addItem(1, "simple_gas_mask").setArmorLogic(new SimpleGasMask());
        MyMetaItems.GAS_MASK = addItem(2, "gas_mask").setArmorLogic(new BreathingApparatus(HEAD));
        MyMetaItems.GAS_TANK = addItem(3, "gas_tank").setArmorLogic(new BreathingApparatus(CHEST));
        MyMetaItems.ASBESTOS_MASK = addItem(4, "asbestos_mask").setArmorLogic(new AdvancedBreathingApparatus(HEAD, 1, "asbestos", 0, 0.3));
        MyMetaItems.ASBESTOS_CHESTPLATE = addItem(5, "asbestos_chestplate").setArmorLogic(new AdvancedBreathingApparatus(CHEST, 1, "asbestos", 0, 0.3));
        MyMetaItems.ASBESTOS_LEGGINGS = addItem(6, "asbestos_leggings").setArmorLogic(new AdvancedBreathingApparatus(LEGS, 1, "asbestos", 0, 0.3));
        MyMetaItems.ASBESTOS_BOOTS = addItem(7, "asbestos_boots").setArmorLogic(new AdvancedBreathingApparatus(FEET, 1, "asbestos", 0, 0.3));
        MyMetaItems.REBREATHER_TANK = addItem(8, "rebreather_tank").setArmorLogic(new AdvancedBreathingApparatus(CHEST, 1, "rebreather", 0, 0.3));
        MyMetaItems.REFLECTIVE_MASK = addItem(9, "reflective_mask").setArmorLogic(new AdvancedBreathingApparatus(HEAD, 5, "reflective", 0, 0.4));
        MyMetaItems.REFLECTIVE_CHESTPLATE = addItem(10, "reflective_chestplate").setArmorLogic(new AdvancedBreathingApparatus(CHEST, 5, "reflective", 0, 0.4));
        MyMetaItems.REFLECTIVE_LEGGINGS = addItem(11, "reflective_leggings").setArmorLogic(new AdvancedBreathingApparatus(LEGS, 5, "reflective", 0, 0.4));
        MyMetaItems.REFLECTIVE_BOOTS = addItem(12, "reflective_boots").setArmorLogic(new AdvancedBreathingApparatus(FEET, 5, "reflective", 0, 0.4));
        MyMetaItems.FILTERED_TANK = addItem(13, "filtered_tank").setArmorLogic(new AdvancedBreathingApparatus(CHEST, 5, "filtered", 0, 0.4));
        MyMetaItems.NOMEX_MASK = addItem(14, "nomex_mask").setArmorLogic(new AdvancedBreathingApparatus(HEAD, 0, "nomex", 1, 0.6));
        MyMetaItems.NOMEX_CHESTPLATE = addItem(15, "nomex_chestplate").setArmorLogic(new AdvancedBreathingApparatus(CHEST, 0, "nomex", 1, 0.6));
        MyMetaItems.NOMEX_LEGGINGS = addItem(16, "nomex_leggings").setArmorLogic(new AdvancedBreathingApparatus(LEGS, 0, "nomex", 1, 0.6));
        MyMetaItems.NOMEX_BOOTS = addItem(17, "nomex_boots").setArmorLogic(new AdvancedBreathingApparatus(FEET, 0, "nomex", 1, 0.6));

    }
}