package com.afsu.mod.cable;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyConductor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CableTileEntity extends BlockEntity implements IEnergyConductor, com.afsu.mod.tools.IDebuggableMachine {
    
    private boolean addedToEnet = false;
    private final double conductionLoss;
    private final int conductorBreakdownEnergy;
    
    @Override
    public java.util.List<net.minecraft.network.chat.Component> getDiagnostics() {
        java.util.List<net.minecraft.network.chat.Component> diag = new java.util.ArrayList<>();
        diag.add(net.minecraft.network.chat.Component.literal("[Conduction Loss]: " + this.conductionLoss + " EU/block").withStyle(net.minecraft.ChatFormatting.GREEN));
        diag.add(net.minecraft.network.chat.Component.literal("[Breakdown Energy]: " + this.conductorBreakdownEnergy + " EU").withStyle(net.minecraft.ChatFormatting.GREEN));
        return diag;
    }
    
    public static BlockEntityType<?> getAbsoluteType() { return com.afsu.mod.registry.AFSUBlockEntities.ABSOLUTE_CABLE_ENTITY.get(); }
    public static BlockEntityType<?> getSuperType() { return com.afsu.mod.registry.AFSUBlockEntities.SUPER_CONDUCTOR_CABLE_ENTITY.get(); }
    public static BlockEntityType<?> getUltraType() { return com.afsu.mod.registry.AFSUBlockEntities.ULTRA_CONDUCTOR_CABLE_ENTITY.get(); }
    
    public CableTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, double conductionLoss, int conductorBreakdownEnergy) {
        super(type, pos, state);
        this.conductionLoss = conductionLoss;
        this.conductorBreakdownEnergy = conductorBreakdownEnergy;
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
        return this.conductionLoss;
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
        return this.conductorBreakdownEnergy; 
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
