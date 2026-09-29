package com.afsu.mod.solar;

import com.afsu.mod.AFSUMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class QuantumSolarPanelTileEntity extends BaseSuperSolarTileEntity {
    public QuantumSolarPanelTileEntity(BlockPos pos, BlockState state) {
        super(pos, state, 4096, 2_048_000, 5); // tier 5 is max
    }
    
    @Override
    public BlockEntityType<?> createType() {
        return com.afsu.mod.registry.AFSUBlockEntities.QUANTUM_SOLAR_PANEL_ENTITY.get();
    }
}

