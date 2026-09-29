package com.afsu.mod.solar;

import com.afsu.mod.AFSUMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AbsoluteSolarPanelTileEntity extends BaseSuperSolarTileEntity {
    public AbsoluteSolarPanelTileEntity(BlockPos pos, BlockState state) {
        super(pos, state, 65536, 32768000, 8); 
    }
    
    @Override
    public BlockEntityType<?> createType() {
        return com.afsu.mod.registry.AFSUBlockEntities.ABSOLUTE_SOLAR_PANEL_ENTITY.get();
    }
}

