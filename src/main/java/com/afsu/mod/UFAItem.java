package com.afsu.mod;

import ic2.api.items.electric.ElectricItem;
import ic2.api.items.electric.IElectricItem;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UFAItem extends Item implements IElectricItem {

    public static final int MAX_CAPACITY = 100_000_000;
    public static final int MAX_TRANSFER = 8192;
    public static final int TIER = 1;

    public UFAItem() {
        super(new Item.Properties().stacksTo(1).tab(AFSUMod.AFSU_TAB));
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return true;
    }

    @Override
    public int getCapacity(ItemStack itemStack) {
        return MAX_CAPACITY;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return TIER;
    }

    @Override
    public int getTransferLimit(ItemStack itemStack) {
        return MAX_TRANSFER;
    }

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (this.allowedIn(pCategory)) {
            // Empty battery
            pItems.add(new ItemStack(this));
            
            // Full battery
            ItemStack full = new ItemStack(this);
            ElectricItem.MANAGER.charge(full, MAX_CAPACITY, TIER, true, false);
            pItems.add(full);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        int capacity = getCapacity(pStack);
        int charge = ElectricItem.MANAGER.getCharge(pStack);
        return Math.round(13.0F * charge / capacity);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        int capacity = getCapacity(pStack);
        int charge = ElectricItem.MANAGER.getCharge(pStack);
        float f = Math.max(0.0F, (float)charge / (float)capacity);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        Component ic2Tooltip = ElectricItem.MANAGER.getToolTip(pStack);
        if (ic2Tooltip != null) {
            pTooltipComponents.add(ic2Tooltip);
        }
    }
}
