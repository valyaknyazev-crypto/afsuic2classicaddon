package com.afsu.mod.mixin;

import ic2.core.fluid.LayeredFluidTank;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LayeredFluidTank.class)
public abstract class LayeredFluidTankMixin {

    @Shadow(remap = false)
    Object2ObjectMap<Fluid, FluidStack> fluidMap;

    @Inject(method = "drain(Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraftforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/minecraftforge/fluids/FluidStack;", at = @At("RETURN"), remap = false)
    private void afsu$onDrainReturn(FluidStack resource, IFluidHandler.FluidAction action, CallbackInfoReturnable<FluidStack> cir) {
        if (action.execute() && cir.getReturnValue() != null && !cir.getReturnValue().isEmpty()) {
            Fluid fluid = cir.getReturnValue().getFluid();
            FluidStack stack = this.fluidMap.get(fluid);
            if (stack != null && stack.isEmpty()) {
                this.fluidMap.remove(fluid);
            }
        }
    }

    @Inject(method = "drain(ILnet/minecraftforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/minecraftforge/fluids/FluidStack;", at = @At("RETURN"), remap = false)
    private void afsu$onDrainIntReturn(int maxDrain, IFluidHandler.FluidAction action, CallbackInfoReturnable<FluidStack> cir) {
        if (action.execute() && cir.getReturnValue() != null && !cir.getReturnValue().isEmpty()) {
            Fluid fluid = cir.getReturnValue().getFluid();
            FluidStack stack = this.fluidMap.get(fluid);
            if (stack != null && stack.isEmpty()) {
                this.fluidMap.remove(fluid);
            }
        }
    }
}
