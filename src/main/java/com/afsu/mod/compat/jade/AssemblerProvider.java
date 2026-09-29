package com.afsu.mod.compat.jade;

import com.afsu.mod.assembler.AdvancedAssemblerTileEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.IServerDataProvider;
import net.minecraft.nbt.CompoundTag;
import snownee.jade.api.ui.IElementHelper;

public enum AssemblerProvider implements IBlockComponentProvider, IServerDataProvider<net.minecraft.world.level.block.entity.BlockEntity> {
    INSTANCE;

    private static final ResourceLocation UID = new ResourceLocation("afsu", "assembler_info");

    @Override
    public void appendServerData(CompoundTag data, net.minecraft.server.level.ServerPlayer player, net.minecraft.world.level.Level level, net.minecraft.world.level.block.entity.BlockEntity blockEntity, boolean showDetails) {
        if (blockEntity instanceof AdvancedAssemblerTileEntity tile) {
            data.putInt("afsu.progress", (int)tile.getProgress());
            data.putInt("afsu.maxProgress", (int)tile.getMaxProgress());
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof AdvancedAssemblerTileEntity tile) {
            
            double speed = tile.getSpeedMultiplier();
            double energyMulti = tile.getEnergyDemandMultiplier();
            
            tooltip.add(Component.translatable("gui.afsu.jade.speed", String.format("%.1f", speed)));
            tooltip.add(Component.translatable("gui.afsu.jade.energy_demand", String.format("%.1f", energyMulti)));
            
            CompoundTag data = accessor.getServerData();
            int progress = data.contains("afsu.progress") ? data.getInt("afsu.progress") : (int)tile.getProgress();
            int maxProgress = data.contains("afsu.maxProgress") ? data.getInt("afsu.maxProgress") : (int)tile.getMaxProgress();

            
            if (maxProgress > 0 && progress > 0) {
                tooltip.add(IElementHelper.get().progress((float)progress / maxProgress, Component.translatable("gui.afsu.jade.progress", progress, maxProgress).withStyle(net.minecraft.ChatFormatting.WHITE), IElementHelper.get().progressStyle().color(0xFF00AAFF).textColor(0xFFFFFFFF), IElementHelper.get().borderStyle()));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
