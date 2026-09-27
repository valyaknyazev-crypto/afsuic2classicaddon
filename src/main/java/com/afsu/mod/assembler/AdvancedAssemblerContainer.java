package com.afsu.mod.assembler;

import ic2.core.inventory.container.ContainerComponent;
import ic2.core.inventory.gui.components.simple.ProgressComponent;
import ic2.core.inventory.gui.components.simple.ChargeBarComponent;
import ic2.core.inventory.gui.components.simple.TankComponent;
import ic2.core.utils.math.geometry.Box2i;
import ic2.core.utils.math.geometry.Vec2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class AdvancedAssemblerContainer extends ContainerComponent<AdvancedAssemblerTileEntity> {

    public AdvancedAssemblerContainer(int id, net.minecraft.world.entity.player.Inventory inv, net.minecraft.network.FriendlyByteBuf data) {
        this((AdvancedAssemblerTileEntity) inv.player.level.getBlockEntity(data.readBlockPos()), inv.player, id);
    }

    public AdvancedAssemblerContainer(AdvancedAssemblerTileEntity tile, Player player, int id) {
        super(tile, player, id);
        
        // Fluid Slots
        this.addSlot(new ic2.core.inventory.slot.FilterSlot(tile, AdvancedAssemblerTileEntity.SLOT_FLUID_IN, 7, 15, T -> net.minecraftforge.fluids.FluidUtil.getFluidHandler(T).isPresent()));
        this.addSlot(new ic2.core.inventory.slot.FilterSlot(tile, AdvancedAssemblerTileEntity.SLOT_FLUID_OUT, 7, 57, T -> false));

        // Machine Slots
        for (int i = 0; i < 6; i++) {
            this.addSlot(new ic2.core.inventory.slot.FilterSlot(tile, AdvancedAssemblerTileEntity.INPUT_SLOTS[i], 49 + (i % 2) * 20, 15 + (i / 2) * 20, T -> true));
        }
        for (int i = 0; i < 4; i++) {
            this.addSlot(new ic2.core.inventory.slot.FilterSlot(tile, AdvancedAssemblerTileEntity.OUTPUT_SLOTS[i], 113 + (i % 2) * 20, 26 + (i / 2) * 20, T -> false));
        }
        
        // Upgrades
        for (int i = 0; i < 4; i++) {
            this.addSlot(new ic2.core.inventory.slot.FilterSlot(tile, AdvancedAssemblerTileEntity.UPGRADE_SLOTS[i], 154, 8 + i * 18, T -> T.getItem() instanceof ic2.api.items.IUpgradeItem));
        }
        
        this.addPlayerInventory(player.getInventory());

        this.addComponent(new TankComponent(new Box2i(28, 14, 16, 58), new Vec2i(176, 31), tile.getFluidTank()));
        this.addComponent(new ProgressComponent(new Box2i(88, 35, 24, 17), tile, new Vec2i(176, 14), false));
        this.addComponent(new ChargeBarComponent(new Box2i(90, 57, 14, 14), tile, new Vec2i(176, 0), true));
        
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("afsu", "textures/gui/advanced_assembler.png");
    }

}
