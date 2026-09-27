package com.afsu.mod.transformer;

import com.mojang.blaze3d.vertex.PoseStack;
import ic2.core.inventory.gui.IC2Screen;
import ic2.core.inventory.gui.components.GuiWidget;
import ic2.core.inventory.gui.components.base.ToolTipButton;
import ic2.core.utils.helpers.Formatters;
import ic2.core.utils.math.geometry.Box2i;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.Set;

public class PlasmaTransformerComponent extends GuiWidget {
    
    public static final String[] TIERS = new String[]{"LuV", "ZPM", "UV", "UHV", "UEV", "UIV", "MAX"};
    PlasmaTransformerTileEntity tile;
    int lastPacket;
    int lastEnergy;

    public PlasmaTransformerComponent(PlasmaTransformerTileEntity tile) {
        super(Box2i.EMPTY_BOX);
        this.tile = tile;
    }

    @Override
    protected void addRequests(Set<GuiWidget.ActionRequest> requests) {
        requests.add(GuiWidget.ActionRequest.GUI_INIT);
        requests.add(GuiWidget.ActionRequest.GUI_TICK);
        requests.add(GuiWidget.ActionRequest.DRAW_FOREGROUND);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void init(IC2Screen gui) {
        this.lastEnergy = this.tile.energyPacket;
        this.lastPacket = this.tile.packetCount;
        int x = gui.getGuiLeft();
        int y = gui.getGuiTop();
        
        MutableComponent ctrl = this.translate("tooltip.ic2.press_key_description", this.translate("tooltip.ic2.ctrl.name"), "10x");
        MutableComponent shift = this.translate("tooltip.ic2.press_key_description", this.translate("tooltip.ic2.shift.name"), "100x");
        MutableComponent alt = this.translate("tooltip.ic2.press_key_description", this.translate("tooltip.ic2.alt.name"), "1000x");
        MutableComponent all = this.string("").append(ctrl).append("\n").append(shift).append("\n").append(alt);
        
        gui.addRenderableWidget(0, new ToolTipButton(x + 157, y + 18, 12, 12, this.string("+"), T -> this.onPacket(1)).setToolTip(ctrl));
        gui.addRenderableWidget(1, new ToolTipButton(x + 157, y + 35, 12, 12, this.string("+"), T -> this.onEnergy(1)).setToolTip(all));
        gui.addRenderableWidget(2, new ToolTipButton(x + 7, y + 18, 12, 12, this.string("-"), T -> this.onPacket(-1)).setToolTip(ctrl));
        gui.addRenderableWidget(3, new ToolTipButton(x + 7, y + 35, 12, 12, this.string("-"), T -> this.onEnergy(-1)).setToolTip(all));
        
        ToolTipButton confirmBtn = new ToolTipButton(x + 119, y + 70, 50, 12, this.translate("gui.ic2.creative_source.confirm"), T -> this.confirmChange());
        confirmBtn.setToolTip(all);
        confirmBtn.active = false;
        gui.addRenderableWidget(4, confirmBtn);
        
        int xOffset = 13;
        for (int i = 0; i < TIERS.length; ++i) {
            int index = i;
            int extra = gui.getFont().width(TIERS[i]) + 6;
            gui.addRenderableWidget(new ExtendedButton(x + xOffset, y + 55, extra, 12, this.string(TIERS[i]), T -> this.sendTier(index)));
            xOffset += extra + 3;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void confirmChange() {
        this.tile.sendToServer(0, this.tile.packetCount);
        this.tile.sendToServer(1, this.tile.energyPacket);
        this.lastPacket = this.tile.packetCount;
        this.lastEnergy = this.tile.energyPacket;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void tick(IC2Screen gui) {
        gui.getButton(0).active = this.tile.packetCount < 32;
        gui.getButton(1).active = this.tile.energyPacket < 2097152;
        gui.getButton(2).active = this.tile.packetCount > 1;
        gui.getButton(3).active = this.tile.energyPacket > 32768;
        gui.getButton(4).active = this.tile.energyPacket != this.lastEnergy || this.tile.packetCount != this.lastPacket;
    }

    @OnlyIn(Dist.CLIENT)
    public void onPacket(int effect) {
        if (Screen.hasControlDown()) effect *= 10;
        this.tile.packetCount = Mth.clamp(this.tile.packetCount + effect, 1, 32);
    }

    public void sendTier(int tierIndex) {
        // Tiers: 32768 * (2 ^ tierIndex)
        int power = 32768 * (int)Math.pow(2, tierIndex);
        this.tile.energyPacket = power;
    }

    @OnlyIn(Dist.CLIENT)
    public void onEnergy(int effect) {
        if (Screen.hasControlDown()) effect *= 10;
        if (Screen.hasShiftDown()) effect *= 100;
        if (Screen.hasAltDown()) effect *= 1000;
        
        this.tile.energyPacket = Mth.clamp(this.tile.energyPacket + effect, 32768, 2097152);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawForeground(PoseStack matrix, int mouseX, int mouseY) {
        this.gui.drawCenterString(matrix, this.translate("gui.ic2.creative_source.eu", Formatters.EU_FORMAT.format(this.tile.energyPacket)), 83, 37, 0x404040);
        this.gui.drawCenterString(matrix, this.translate("gui.ic2.creative_source.packets", Formatters.EU_FORMAT.format(this.tile.packetCount)), 83, 20, 0x404040);
    }
}
