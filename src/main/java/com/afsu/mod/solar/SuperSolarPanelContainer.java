package com.afsu.mod.solar;

import ic2.core.inventory.container.ContainerComponent;
import ic2.core.inventory.gui.components.simple.ChargeBarComponent;
import ic2.core.inventory.slot.FilterSlot;
import ic2.core.utils.math.geometry.Box2i;
import ic2.core.utils.math.geometry.Vec2i;
import net.minecraft.world.entity.player.Player;
import com.afsu.mod.AFSUMod;

public class SuperSolarPanelContainer extends ContainerComponent<BaseSuperSolarTileEntity> {

    public static final Box2i SOLAR_PANEL_LIGHT_BOX = new Box2i(147, 41, 12, 12);
    public static final Vec2i DAY_SOLAR_LIGHT_POS = new Vec2i(177, 17);
    public static final Vec2i NIGHT_SOLAR_LIGHT_POS = new Vec2i(193, 17);
    public static final Box2i CHARGE_BOX = new Box2i(141, 60, 24, 9);
    public static final Vec2i CHARGE_POS = new Vec2i(176, 0);

    public static final net.minecraft.resources.ResourceLocation GUI_TEXTURE = new net.minecraft.resources.ResourceLocation(AFSUMod.MODID, "textures/gui/gui_advanced_solar.png");

    public SuperSolarPanelContainer(BaseSuperSolarTileEntity tile, Player player, int id) {
        super(tile, player, id);
        this.addSlot(FilterSlot.createChargeSlot(tile, tile.getTier(), 0, 98, 39));
        this.addSlot(FilterSlot.createChargeSlot(tile, tile.getTier(), 1, 116, 39));
        this.addSlot(FilterSlot.createChargeSlot(tile, tile.getTier(), 2, 98, 57));
        this.addSlot(FilterSlot.createChargeSlot(tile, tile.getTier(), 3, 116, 57));
        
        this.addComponent(new SuperSolarPanelComp(tile, SOLAR_PANEL_LIGHT_BOX, DAY_SOLAR_LIGHT_POS, NIGHT_SOLAR_LIGHT_POS));
        this.addComponent(new SuperSolarEnergyStringComp(tile));
        this.addComponent(new ChargeBarComponent(CHARGE_BOX, tile, CHARGE_POS, false));
        this.addPlayerInventory(player.getInventory());
    }

    @Override
    public net.minecraft.resources.ResourceLocation getTexture() {
        return GUI_TEXTURE;
    }

    @Override
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    public void onGuiLoaded(ic2.core.inventory.gui.IC2Screen gui) {
        gui.clearFlag(ic2.core.inventory.gui.IC2Screen.SHOW_PLAYER_INVENTORY_NAME);
        gui.clearFlag(ic2.core.inventory.gui.IC2Screen.SHOW_CONTAINER_NAME);
    }
}
