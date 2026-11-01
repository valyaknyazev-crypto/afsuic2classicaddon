package com.afsu.mod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import ic2.core.block.base.tiles.impls.BaseEnergyStorageTileEntity;

public abstract class DirectionalEnergyStorageTileEntity extends BaseEnergyStorageTileEntity {

    public DirectionalEnergyStorageTileEntity(BlockPos pos, BlockState state, int tier, int output, int capacity) {
        super(pos, state, tier, output, capacity);
    }

    @Override
    public Direction getFacing() {
        if (this.hasLevel() && this.getBlockState().hasProperty(BlockStateProperties.FACING)) {
            return this.getBlockState().getValue(BlockStateProperties.FACING);
        }
        return super.getFacing();
    }

    @Override
    public void onLoaded() {
        if (this.hasLevel() && this.getBlockState().hasProperty(BlockStateProperties.FACING)) {
            this.setFacingSilent(this.getBlockState().getValue(BlockStateProperties.FACING));
        }
        super.onLoaded();
    }

    @Override
    public void setFacing(Direction facing) {
        super.setFacing(facing);
        if (this.hasLevel() && !this.level.isClientSide) {
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(BlockStateProperties.FACING, facing));
        }
    }
}
