package com.afsu.mod.cable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AbsoluteCableBlock extends Block implements EntityBlock {

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public AbsoluteCableBlock() {
        super(Properties.of(Material.METAL).strength(0.2F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false).setValue(EAST, false)
                .setValue(SOUTH, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    private VoxelShape makeShape(BlockState state) {
        float min = 6.0f;
        float max = 10.0f;
        VoxelShape shape = Block.box(min, min, min, max, max, max);

        if (state.getValue(UP)) shape = Shapes.or(shape, Block.box(min, max, min, max, 16, max));
        if (state.getValue(DOWN)) shape = Shapes.or(shape, Block.box(min, 0, min, max, min, max));
        if (state.getValue(NORTH)) shape = Shapes.or(shape, Block.box(min, min, 0, max, max, min));
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, Block.box(min, min, max, max, max, 16));
        if (state.getValue(EAST)) shape = Shapes.or(shape, Block.box(max, min, min, 16, max, max));
        if (state.getValue(WEST)) shape = Shapes.or(shape, Block.box(0, min, min, min, max, max));

        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return makeShape(state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbsoluteCableTileEntity(pos, state);
    }

    public boolean canConnectTo(LevelAccessor level, BlockPos pos, Direction dir) {
        BlockEntity te = level.getBlockEntity(pos);
        return te instanceof ic2.api.energy.tile.IEnergyTile;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(NORTH, canConnectTo(level, pos.north(), Direction.SOUTH))
                .setValue(SOUTH, canConnectTo(level, pos.south(), Direction.NORTH))
                .setValue(WEST, canConnectTo(level, pos.west(), Direction.EAST))
                .setValue(EAST, canConnectTo(level, pos.east(), Direction.WEST))
                .setValue(UP, canConnectTo(level, pos.above(), Direction.DOWN))
                .setValue(DOWN, canConnectTo(level, pos.below(), Direction.UP));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        boolean connect = canConnectTo(level, neighborPos, dir.getOpposite());
        switch (dir) {
            case NORTH: return state.setValue(NORTH, connect);
            case SOUTH: return state.setValue(SOUTH, connect);
            case WEST: return state.setValue(WEST, connect);
            case EAST: return state.setValue(EAST, connect);
            case UP: return state.setValue(UP, connect);
            case DOWN: return state.setValue(DOWN, connect);
        }
        return state;
    }
}
