package com.afsu.mod.compat.jade;

import com.afsu.mod.assembler.AdvancedAssemblerTileEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public enum AssemblerProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = new ResourceLocation("afsu", "assembler_info");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof AdvancedAssemblerTileEntity tile) {
            
            double speed = tile.getSpeedMultiplier();
            double energyMulti = tile.getEnergyDemandMultiplier();
            
            tooltip.add(Component.translatable("gui.afsu.jade.speed", String.format("%.1f", speed)));
            tooltip.add(Component.translatable("gui.afsu.jade.energy_demand", String.format("%.1f", energyMulti)));
            
            int progress = (int)tile.getProgress();
            int maxProgress = (int)tile.getMaxProgress();
            
            if (maxProgress > 0 && progress > 0) {
                tooltip.add(IElementHelper.get().progress((float)progress / maxProgress, Component.literal(progress + " / " + maxProgress), IElementHelper.get().progressStyle().color(0xFF00AAFF), IElementHelper.get().borderStyle()));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
