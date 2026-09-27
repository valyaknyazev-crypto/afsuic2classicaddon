package com.afsu.mod;

import com.mojang.blaze3d.vertex.PoseStack;
import ic2.core.block.storage.components.EnergyStorageComponent;
import ic2.core.block.base.tiles.impls.BaseEnergyStorageTileEntity;
import net.minecraft.network.chat.Component;

public class AFSUEnergyStorageComponent extends EnergyStorageComponent {

    public AFSUEnergyStorageComponent(BaseEnergyStorageTileEntity tile) {
        super(tile);
    }

    @Override
    public void drawForeground(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        int guiOffset = this.tile.getGuiOffset();
        
        Component powerLevelTitle = Component.translatable("gui.ic2.storage.power");
        this.gui.drawString(pPoseStack, powerLevelTitle, 85 + guiOffset, 23, 4210752);
        
        String powerText = "\u221E EU";
        String maxPowerText = "/ \u221E EU";
        
        pPoseStack.pushPose();
        pPoseStack.scale(0.85F, 0.85F, 1.0F);
        
        float scale = 0.85F;
        float xBase = (109.0F + guiOffset) / scale;
        
        this.gui.drawString(pPoseStack, Component.literal(powerText), (int) xBase, (int) (35.0F / scale), 4210752);
        this.gui.drawString(pPoseStack, Component.literal(maxPowerText), (int) xBase, (int) (45.0F / scale), 4210752);
        
        pPoseStack.popPose();
        
        Component outputTitle = Component.translatable("gui.ic2.storage.output", new Object[]{
            ic2.core.utils.helpers.Formatters.EU_FORMAT.format((long) this.tile.getMaxEnergyOutput())
        });
        this.gui.drawString(pPoseStack, outputTitle, 85 + guiOffset, 60, 4210752);
    }
}
