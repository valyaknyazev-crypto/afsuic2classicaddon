package com.afsu.mod.compat.jade;

import ic2.api.energy.tile.IEnergyConductor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CableProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = new ResourceLocation("afsu", "cable_info");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof IEnergyConductor tile) {
            tooltip.add(Component.literal("Conductor Breakdown: " + tile.getConductorBreakdownEnergy() + " EU/t"));
            tooltip.add(Component.literal("Insulation Breakdown: " + tile.getInsulationBreakdownEnergy() + " EU/t"));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
