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
import java.util.function.BiFunction;

public class CableBlock extends Block implements EntityBlock {

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    private final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory;

    public CableBlock(BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory) {
        super(Properties.of(Material.METAL).strength(0.2F).noOcclusion());
        this.blockEntityFactory = blockEntityFactory;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false).setValue(EAST, false)
                .setValue(SOUTH, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    private static final VoxelShape[] SHAPES = makeShapes();
    
    private static VoxelShape[] makeShapes() {
        VoxelShape[] shapes = new VoxelShape[64];
        float min = 6.0f;
        float max = 10.0f;
        VoxelShape center = Block.box(min, min, min, max, max, max);
        VoxelShape up = Block.box(min, max, min, max, 16, max);
        VoxelShape down = Block.box(min, 0, min, max, min, max);
        VoxelShape north = Block.box(min, min, 0, max, max, min);
        VoxelShape south = Block.box(min, min, max, max, max, 16);
        VoxelShape east = Block.box(max, min, min, 16, max, max);
        VoxelShape west = Block.box(0, min, min, min, max, max);
        
        for (int i = 0; i < 64; i++) {
            VoxelShape shape = center;
            if ((i & 1) != 0) shape = Shapes.or(shape, up);
            if ((i & 2) != 0) shape = Shapes.or(shape, down);
            if ((i & 4) != 0) shape = Shapes.or(shape, north);
            if ((i & 8) != 0) shape = Shapes.or(shape, south);
            if ((i & 16) != 0) shape = Shapes.or(shape, east);
            if ((i & 32) != 0) shape = Shapes.or(shape, west);
            shapes[i] = shape;
        }
        return shapes;
    }

    private int getShapeIndex(BlockState state) {
        int index = 0;
        if (state.getValue(UP)) index |= 1;
        if (state.getValue(DOWN)) index |= 2;
        if (state.getValue(NORTH)) index |= 4;
        if (state.getValue(SOUTH)) index |= 8;
        if (state.getValue(EAST)) index |= 16;
        if (state.getValue(WEST)) index |= 32;
        return index;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[getShapeIndex(state)];
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityFactory.apply(pos, state);
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
