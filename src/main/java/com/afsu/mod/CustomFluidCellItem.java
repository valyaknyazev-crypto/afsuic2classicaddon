package com.afsu.mod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CustomFluidCellItem extends Item {
    private final Supplier<? extends Fluid> fluidSupplier;

    public CustomFluidCellItem(Supplier<? extends Fluid> fluidSupplier, Properties properties) {
        super(properties);
        this.fluidSupplier = fluidSupplier;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        Item emptyCell = ForgeRegistries.ITEMS.getValue(new ResourceLocation("ic2", "cell_empty"));
        if (emptyCell != null && emptyCell != net.minecraft.world.item.Items.AIR) {
            return new ItemStack(emptyCell);
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidCellHandler(stack, fluidSupplier.get());
    }

    public static class FluidCellHandler extends FluidHandlerItemStackSimple.Consumable {
        private final Fluid fluid;

        public FluidCellHandler(@Nonnull ItemStack container, Fluid fluid) {
            super(container, 1000);
            this.fluid = fluid;
        }

        @Nonnull
        @Override
        public FluidStack getFluid() {
            return new FluidStack(fluid, 1000);
        }

        @Override
        protected void setContainerToEmpty() {
            Item emptyCell = ForgeRegistries.ITEMS.getValue(new ResourceLocation("ic2", "cell_empty"));
            if (emptyCell != null && emptyCell != net.minecraft.world.item.Items.AIR) {
                this.container = new ItemStack(emptyCell);
            } else {
                this.container = ItemStack.EMPTY;
            }
        }
    }
}