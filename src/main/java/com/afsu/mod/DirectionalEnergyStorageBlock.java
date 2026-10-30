package com.afsu.mod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import ic2.core.block.base.IC2ContainerBlock;
import ic2.core.block.base.tiles.BaseTileEntity;
import ic2.core.utils.helpers.Tool;

public abstract class DirectionalEnergyStorageBlock extends IC2ContainerBlock {

    public DirectionalEnergyStorageBlock(String name, Properties properties) {
        super(name, properties);
        this.setHarvestTool(Tool.PICKAXE.withLevel(1));
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // In IC2, the output face points TOWARDS the player on placement.
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof BaseTileEntity base) {
            base.setFacingSilent(state.getValue(BlockStateProperties.FACING));
        }
    }

    @Override
    public Direction getFacing(BlockState state, Level world, BlockPos pos) {
        if (state.hasProperty(BlockStateProperties.FACING)) {
            return state.getValue(BlockStateProperties.FACING);
        }
        return super.getFacing(state, world, pos);
    }
}
