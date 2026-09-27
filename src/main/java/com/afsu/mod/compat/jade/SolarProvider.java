package com.afsu.mod.compat.jade;

import com.afsu.mod.solar.BaseSuperSolarTileEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum SolarProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = new ResourceLocation("afsu", "solar_info");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof BaseSuperSolarTileEntity tile) {
            boolean sky = tile.skyBlockCheck();
            boolean sun = tile.isSunVisible();
            Component skyStatus = Component.translatable(sky ? "gui.afsu.jade.sky.clear" : "gui.afsu.jade.sky.blocked");
            Component sunStatus = Component.translatable(sun ? "gui.afsu.jade.sun.visible" : "gui.afsu.jade.sun.hidden");
            
            tooltip.add(Component.translatable("gui.afsu.jade.sky", skyStatus));
            tooltip.add(Component.translatable("gui.afsu.jade.sun", sunStatus));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
