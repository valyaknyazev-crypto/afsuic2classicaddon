package com.afsu.mod.solar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import ic2.core.block.base.IC2ContainerBlock;
import ic2.core.utils.helpers.Tool;

import java.util.function.BiFunction;

public class SolarPanelBlock extends IC2ContainerBlock {

    private final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory;

    public SolarPanelBlock(String name, BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory) {
        super(name, BlockBehaviour.Properties.of(Material.METAL).strength(5.0f, 6.0f).requiresCorrectToolForDrops());
        this.blockEntityFactory = blockEntityFactory;
        this.setHarvestTool(Tool.PICKAXE.withLevel(1));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityFactory.apply(pos, state);
    }

    @Override
    public net.minecraft.world.item.BlockItem createItem() {
        return null;
    }

    // Solar panels do not have a facing and should not be rotated by wrench
    @Override
    public Direction getFacing(BlockState state, Level world, BlockPos pos) {
        return null;
    }

    @Override
    public boolean canSetFacing(BlockState state, Level world, BlockPos pos, Player player, Direction side) {
        return false;
    }

    @Override
    public boolean setFacing(BlockState state, Level world, BlockPos pos, Player player, Direction side) {
        return false;
    }
}
