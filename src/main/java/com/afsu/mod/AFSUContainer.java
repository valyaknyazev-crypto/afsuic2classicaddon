package com.afsu.mod;

import ic2.core.block.storage.container.ContainerEnergyStorage;
import ic2.core.block.base.tiles.impls.BaseEnergyStorageTileEntity;
import ic2.core.block.storage.components.EnergyStorageComponent;
import net.minecraft.world.entity.player.Player;

public class AFSUContainer extends ContainerEnergyStorage {
    public AFSUContainer(BaseEnergyStorageTileEntity tile, Player player, int id) {
        super(tile, player, id);
        
        // Remove the original EnergyStorageComponent to replace it with ours
        this.getComponents().removeIf(c -> c instanceof EnergyStorageComponent);
        
        // Add our custom component for rendering
        this.addComponent(new AFSUEnergyStorageComponent(tile));
    }
}
