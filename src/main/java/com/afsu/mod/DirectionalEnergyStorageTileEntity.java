package com.afsu.mod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import ic2.core.block.base.tiles.impls.BaseEnergyStorageTileEntity;

public abstract class DirectionalEnergyStorageTileEntity extends BaseEnergyStorageTileEntity {

    public DirectionalEnergyStorageTileEntity(BlockPos pos, BlockState state, int tier, int output, int capacity) {
        super(pos, state, tier, output, capacity);
    }

    @Override
    public void setFacing(Direction direction) {
        super.setFacing(direction);
        if (this.level != null && !this.level.isClientSide) {
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(BlockStateProperties.FACING, direction));
        }
    }
}
