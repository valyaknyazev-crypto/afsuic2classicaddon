package com.afsu.mod.cable;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyConductor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SuperConductorCableTileEntity extends BlockEntity implements IEnergyConductor {
    
    private boolean addedToEnet = false;
    
    public SuperConductorCableTileEntity(BlockPos pos, BlockState state) {
        super(com.afsu.mod.registry.AFSUBlockEntities.SUPER_CONDUCTOR_CABLE_ENTITY.get(), pos, state);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide && !addedToEnet) {
            EnergyNet.INSTANCE.addTile(this);
            addedToEnet = true;
        }
    }
    
    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!level.isClientSide && addedToEnet) {
            EnergyNet.INSTANCE.removeTile(this);
            addedToEnet = false;
        }
    }
    
    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (!level.isClientSide && addedToEnet) {
            EnergyNet.INSTANCE.removeTile(this);
            addedToEnet = false;
        }
    }

    @Override
    public double getConductionLoss() {
        return 0.5;
    }

    @Override
    public int getInsulationEnergyAbsorption() {
        return 9001; 
    }

    @Override
    public int getInsulationBreakdownEnergy() {
        return 9001;
    }

    @Override
    public int getConductorBreakdownEnergy() {
        return 16384; 
    }

    @Override
    public void removeInsulation() {
    }

    @Override
    public void removeConductor() {
        if (!level.isClientSide) {
            level.destroyBlock(worldPosition, true);
        }
    }

    @Override
    public boolean canEmitEnergy(ic2.api.energy.tile.IEnergyAcceptor receiver, Direction direction) {
        return true;
    }

    @Override
    public boolean canAcceptEnergy(ic2.api.energy.tile.IEnergyEmitter emitter, Direction direction) {
        return true;
    }

    @Override
    public boolean isLavaLogged() {
        return false;
    }
}
