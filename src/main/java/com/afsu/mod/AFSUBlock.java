package com.afsu.mod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import ic2.core.block.base.drops.IBlockDropProvider;
import ic2.core.utils.helpers.Tool;

public class AFSUBlock extends DirectionalEnergyStorageBlock {

    public AFSUBlock() {
        super("afsu_block", BlockBehaviour.Properties.of(Material.METAL).strength(5.0f, 6.0f).requiresCorrectToolForDrops());
        this.setHarvestTool(Tool.PICKAXE.withLevel(2));
        this.setDropProvider(IBlockDropProvider.SELF_OR_ADV_MACHINE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AFSUBlockEntity(pos, state);
    }

    @Override
    public net.minecraft.world.item.BlockItem createItem() {
        return null;
    }
}
