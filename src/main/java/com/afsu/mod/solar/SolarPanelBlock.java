package com.afsu.mod.solar;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;

import ic2.core.block.base.IC2ContainerBlock;

import java.util.function.BiFunction;

public class SolarPanelBlock extends IC2ContainerBlock {

    private final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory;

    public SolarPanelBlock(String name, BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory) {
        super(name, BlockBehaviour.Properties.of(Material.METAL).strength(5.0f, 6.0f).requiresCorrectToolForDrops());
        this.blockEntityFactory = blockEntityFactory;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityFactory.apply(pos, state);
    }
    @Override
    public net.minecraft.world.item.BlockItem createItem() {
        return null;
    }
}