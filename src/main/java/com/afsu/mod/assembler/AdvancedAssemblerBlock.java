package com.afsu.mod.assembler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;

import ic2.core.block.base.IC2ContainerBlock;
import ic2.core.block.base.drops.IBlockDropProvider;
import ic2.core.block.base.tiles.BaseTileEntity;
import ic2.core.utils.helpers.Tool;

public class AdvancedAssemblerBlock extends IC2ContainerBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public AdvancedAssemblerBlock() {
        super("advanced_assembler", BlockBehaviour.Properties.of(Material.METAL).strength(5.0f, 6.0f).requiresCorrectToolForDrops());
        this.setHarvestTool(Tool.PICKAXE.withLevel(1));
        this.setDropProvider(IBlockDropProvider.SELF_OR_ADV_MACHINE);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH).setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getHorizontalDirection().getOpposite());
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

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedAssemblerTileEntity(pos, state);
    }

    @Override
    public net.minecraft.world.item.BlockItem createItem() {
        return null;
    }
    
    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(net.minecraft.world.level.Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return type == com.afsu.mod.registry.AFSUBlockEntities.ADVANCED_ASSEMBLER_ENTITY.get() ?
                (lvl, pos, blockState, t) -> ((AdvancedAssemblerTileEntity) t).tick() : null;
    }
}
