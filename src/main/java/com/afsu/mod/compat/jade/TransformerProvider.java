package com.afsu.mod.compat.jade;

import com.afsu.mod.transformer.PlasmaTransformerTileEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum TransformerProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = new ResourceLocation("afsu", "transformer_info");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof PlasmaTransformerTileEntity tile) {
            
            int packets = tile.packetCount;
            int packetSize = tile.energyPacket;
            
            tooltip.add(Component.translatable("gui.afsu.jade.packets", packets));
            tooltip.add(Component.translatable("gui.afsu.jade.packet_size", packetSize));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
