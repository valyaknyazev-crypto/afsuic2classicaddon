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
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.SwordItem;

import ic2.api.items.electric.ElectricItem;
import ic2.api.items.electric.IElectricItem;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;

public class ElectricSaberItem extends SwordItem implements IElectricItem {
    
    private final int maxCapacity;
    private final int maxTransfer;
    private final int tier;
    private final int energyPerTick;
    private final int energyPerHit;
    
    private final Multimap<Attribute, AttributeModifier> activeModifiers;
    private final Multimap<Attribute, AttributeModifier> inactiveModifiers;

    public ElectricSaberItem(Properties properties, int maxCapacity, int maxTransfer, int tier, int energyPerTick, int energyPerHit, double activeDamage, double inactiveDamage) {
        super(Tiers.DIAMOND, 3, -2.4F, properties);
        this.maxCapacity = maxCapacity;
        this.maxTransfer = maxTransfer;
        this.tier = tier;
        this.energyPerTick = energyPerTick;
        this.energyPerHit = energyPerHit;
        
        ImmutableMultimap.Builder<Attribute, AttributeModifier> active = ImmutableMultimap.builder();
        active.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", activeDamage, AttributeModifier.Operation.ADDITION));
        active.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4, AttributeModifier.Operation.ADDITION));
        this.activeModifiers = active.build();
        
        ImmutableMultimap.Builder<Attribute, AttributeModifier> inactive = ImmutableMultimap.builder();
        inactive.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", inactiveDamage, AttributeModifier.Operation.ADDITION));
        inactive.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4, AttributeModifier.Operation.ADDITION));
        this.inactiveModifiers = inactive.build();
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return false;
    }

    @Override
    public int getCapacity(ItemStack itemStack) {
        return this.maxCapacity;
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return this.tier;
    }

    @Override
    public int getTransferLimit(ItemStack itemStack) {
        return this.maxTransfer;
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
        if (!nbt.getBoolean("active") && ElectricItem.MANAGER.canUse(stack, this.energyPerTick)) {
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
                if (!ElectricItem.MANAGER.use(stack, this.energyPerTick, (LivingEntity)entityIn)) {
                    stack.getOrCreateTag().putBoolean("active", false);
                }
            }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (stack.getOrCreateTag().getBoolean("active")) {
            if (!ElectricItem.MANAGER.use(stack, this.energyPerHit, attacker)) {
                stack.getOrCreateTag().putBoolean("active", false);
            }
        }
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack) {
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            return stack.getOrCreateTag().getBoolean("active") ? this.activeModifiers : this.inactiveModifiers;
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
            pItems.add(new ItemStack(this));
            ItemStack full = new ItemStack(this);
            ElectricItem.MANAGER.charge(full, this.maxCapacity, this.tier, true, false);
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
