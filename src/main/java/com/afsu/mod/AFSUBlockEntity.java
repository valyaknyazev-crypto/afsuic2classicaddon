package com.afsu.mod;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;

import com.afsu.mod.tools.IDebuggableMachine;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.network.chat.Component;

public class AFSUBlockEntity extends DirectionalEnergyStorageTileEntity implements net.minecraft.world.MenuProvider, IDebuggableMachine {

    public static final ResourceLocation TEXTURE = new ResourceLocation("ic2", "textures/gui_sprites/blocks/storage/gui_mfsu.png");

    public AFSUBlockEntity(BlockPos pos, BlockState state) {
        // tier 8, capacity 2,110,000,000 EU
        super(pos, state, 8, 65536, 2110000000);
    }

    @Override
    public BlockEntityType<?> createType() {
        return com.afsu.mod.registry.AFSUBlockEntities.AFSU_BLOCK_ENTITY.get();
    }

    @Override
    public ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    public double getDropRate(Player player) {
        return 0.7d;
    }

    @Override
    public int getGuiOffset() {
        return -15;
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("block.afsu.afsu_block");
    }

    @Override
    public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv, Player player) {
        return this.createContainer(player, net.minecraft.world.InteractionHand.MAIN_HAND, null, id);
    }
    
    @Override
    public ic2.core.inventory.container.IC2Container createContainer(net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand, net.minecraft.core.Direction side, int id) {
        return new ic2.core.block.storage.container.ContainerEnergyStorage(this, player, id);
    }

    @Override
    public List<Component> getDiagnostics() {
        List<Component> diag = new ArrayList<>();
        diag.add(Component.literal("[Energy]: " + this.getStoredEU() + " / 2110000000 EU").withStyle(net.minecraft.ChatFormatting.GREEN));
        diag.add(Component.literal("[Output Rate]: " + this.output + " EU/tick").withStyle(net.minecraft.ChatFormatting.GREEN));
        return diag;
    }
}
