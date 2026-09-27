package com.afsu.mod.tools;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.SwordItem;

import ic2.api.items.electric.ElectricItem;
import ic2.api.items.electric.IElectricItem;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class AdvancedSaberItem extends SwordItem implements IElectricItem {
    
    public static final int MAX_CAPACITY = 360_000;
    public static final int MAX_TRANSFER = 8192;
    public static final int TIER = 1;
    public static final int ENERGY_PER_TICK = 10; // Drain while active
    public static final int ENERGY_PER_HIT = 400; // Extra drain when hitting

    public AdvancedSaberItem(Properties properties) {
        super(Tiers.DIAMOND, 3, -2.4F, properties);
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return false;
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
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.getBoolean("active")) {
            if (!worldIn.isClientSide) {
                nbt.putBoolean("active", false);
            }
            return InteractionResultHolder.success(stack);
        }
        if (!nbt.getBoolean("active") && ElectricItem.MANAGER.canUse(stack, ENERGY_PER_TICK)) {
            if (!worldIn.isClientSide) {
                nbt.putBoolean("active", true);
                nbt.putLong("activation_time", worldIn.getGameTime());
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide && stack.getOrCreateTag().getBoolean("active")) {
            if (entityIn instanceof LivingEntity) {
                if (!ElectricItem.MANAGER.use(stack, ENERGY_PER_TICK, (LivingEntity)entityIn)) {
                    stack.getOrCreateTag().putBoolean("active", false);
                }
            }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (stack.getOrCreateTag().getBoolean("active")) {
            if (!ElectricItem.MANAGER.use(stack, ENERGY_PER_HIT, attacker)) {
                stack.getOrCreateTag().putBoolean("active", false);
            }
        }
        return true;
    }

    private static final Multimap<Attribute, AttributeModifier> ACTIVE_MODIFIERS;
    private static final Multimap<Attribute, AttributeModifier> INACTIVE_MODIFIERS;
    static {
        Multimap<Attribute, AttributeModifier> active = HashMultimap.create();
        active.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 30.0, AttributeModifier.Operation.ADDITION));
        active.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4, AttributeModifier.Operation.ADDITION));
        ACTIVE_MODIFIERS = active;
        Multimap<Attribute, AttributeModifier> inactive = HashMultimap.create();
        inactive.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 4.0, AttributeModifier.Operation.ADDITION));
        inactive.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4, AttributeModifier.Operation.ADDITION));
        INACTIVE_MODIFIERS = inactive;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            return stack.getOrCreateTag().getBoolean("active") ? ACTIVE_MODIFIERS : INACTIVE_MODIFIERS;
        }
        return HashMultimap.create();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getOrCreateTag().getBoolean("active") != newStack.getOrCreateTag().getBoolean("active") || slotChanged;
    }

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (this.allowedIn(pCategory)) {
            // Empty
            pItems.add(new ItemStack(this));
            // Full
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
}




