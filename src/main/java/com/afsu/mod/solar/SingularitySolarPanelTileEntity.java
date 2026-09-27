package com.afsu.mod.solar;

import com.afsu.mod.AFSUMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SingularitySolarPanelTileEntity extends BaseSuperSolarTileEntity {
    public SingularitySolarPanelTileEntity(BlockPos pos, BlockState state) {
        super(pos, state, 16384, 480_000_000, 7); 
    }
    
    @Override
    public BlockEntityType<?> createType() {
        return AFSUMod.SINGULARITY_SOLAR_PANEL_ENTITY.get();
    }
}

