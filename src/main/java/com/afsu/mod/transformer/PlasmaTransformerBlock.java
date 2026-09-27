package com.afsu.mod.transformer;

import com.afsu.mod.DirectionalEnergyStorageBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Block;

public class PlasmaTransformerBlock extends DirectionalEnergyStorageBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public PlasmaTransformerBlock() {
        super("plasma_transformer", BlockBehaviour.Properties.of(Material.METAL).strength(5.0f, 6.0f).requiresCorrectToolForDrops());
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlasmaTransformerTileEntity(pos, state);
    }

    @Override
    public net.minecraft.world.item.BlockItem createItem() {
        return null; // Will use manual item registration or allow default
    }
}
