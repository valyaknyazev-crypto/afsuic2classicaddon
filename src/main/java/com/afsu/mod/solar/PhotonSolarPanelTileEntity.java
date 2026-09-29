package com.afsu.mod.solar;

import com.afsu.mod.AFSUMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PhotonSolarPanelTileEntity extends BaseSuperSolarTileEntity {
    public PhotonSolarPanelTileEntity(BlockPos pos, BlockState state) {
        super(pos, state, 8192, 4_096_000, 6); 
    }
    
    @Override
    public BlockEntityType<?> createType() {
        return com.afsu.mod.registry.AFSUBlockEntities.PHOTON_SOLAR_PANEL_ENTITY.get();
    }
}

