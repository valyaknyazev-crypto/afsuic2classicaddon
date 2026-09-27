package com.afsu.mod.solar;

import com.mojang.blaze3d.vertex.PoseStack;
import ic2.core.inventory.gui.components.GuiWidget;
import ic2.core.utils.math.geometry.Box2i;
import net.minecraft.network.chat.Component;

import java.util.Set;

public class SuperSolarEnergyStringComp extends GuiWidget {
    BaseSuperSolarTileEntity block;
    int textColor = 13487565;

    public SuperSolarEnergyStringComp(BaseSuperSolarTileEntity tile) {
        super(Box2i.EMPTY_BOX);
        this.block = tile;
    }

    @Override
    protected void addRequests(Set<ActionRequest> set) {
        set.add(ActionRequest.DRAW_FOREGROUND);
    }

    @Override
    public void drawForeground(PoseStack matrix, int mouseX, int mouseY) {
        gui.drawCenterString(matrix, block.getName(), 176 / 2, 5, 0);
        int eu = this.block.getStorage();
        int max = this.block.getMaxStorage();
        if (eu > max) {
            eu = max;
        }

        matrix.pushPose();
        float scale = 0.8f;
        matrix.scale(scale, scale, scale);
        int x = (int)(9 / scale);
        int y = (int)(21 / scale);
        
        gui.drawString(matrix, Component.translatable("gui.afsu.solar.storage", eu), x, y, textColor);
        gui.drawString(matrix, Component.literal("/" + max), x, y + 10, textColor);
        gui.drawString(matrix, Component.translatable("gui.afsu.solar.maxOutput"), x, y + 20, textColor);
        gui.drawString(matrix, Component.literal(this.block.getMaxEnergyOutput() + " EU/t"), x, y + 30, textColor);
        gui.drawString(matrix, Component.translatable("gui.afsu.solar.generating"), x, y + 40, textColor);
        
        
        int currentOutput = this.block.isActive() ? (this.block.day ? this.block.getProduction() : this.block.getLowerProduction()) : 0;
        gui.drawString(matrix, Component.literal(currentOutput + " EU/t"), x, y + 50, textColor);
        
        matrix.popPose();
    }
}
