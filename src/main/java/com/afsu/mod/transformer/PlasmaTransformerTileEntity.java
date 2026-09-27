package com.afsu.mod.transformer;

import com.afsu.mod.AFSUMod;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.energy.tile.IMultiEnergySource;
import ic2.api.network.buffer.NetworkInfo;
import ic2.api.network.tile.INetworkClientEventListener;
import ic2.api.tiles.readers.IEUStorage;
import ic2.core.block.base.tiles.BaseTileEntity;
import ic2.core.inventory.base.ITileGui;
import ic2.core.inventory.container.IC2Container;
import ic2.core.utils.helpers.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.afsu.mod.tools.IDebuggableMachine;

public class PlasmaTransformerTileEntity extends BaseTileEntity implements IMultiEnergySource, IEnergySink, IEUStorage, ITileGui, INetworkClientEventListener, IDebuggableMachine {

    @NetworkInfo
    public int energy;
    @NetworkInfo(value = NetworkInfo.BitLevel.BIT_8)
    public int packetCount = 1;
    @NetworkInfo
    public int energyPacket = 32768; // Start at 32k
    @NetworkInfo
    public int maxEnergy = 32768 * 32;
    public boolean addedToEnergyNet = false;

    public PlasmaTransformerTileEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.addGuiFields(new String[]{"energy", "maxEnergy", "packetCount", "energyPacket"});
    }

    @Override
    public BlockEntityType<?> createType() {
        return com.afsu.mod.registry.AFSUBlockEntities.PLASMA_TRANSFORMER_ENTITY.get();
    }

    @Override
    public boolean isAllowingUI() {
        return false;
    }

    @Override
    public IC2Container createContainer(Player player, InteractionHand hand, Direction side, int windowID) {
        return new PlasmaTransformerContainer(this, player, windowID);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        NBTUtils.putInt(compound, "energy", this.energy, 0);
        NBTUtils.putInt(compound, "max_energy", this.maxEnergy, 256);
        NBTUtils.putInt(compound, "packets", this.packetCount, 1);
        NBTUtils.putInt(compound, "packet_energy", this.energyPacket, 32768);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.energy = NBTUtils.getInt(compound, "energy", 0);
        this.maxEnergy = NBTUtils.getInt(compound, "max_energy", 256);
        this.packetCount = NBTUtils.getInt(compound, "packets", 1);
        this.energyPacket = NBTUtils.getInt(compound, "packet_energy", 32768);
    }

    @Override
    public void onClientDataReceived(Player player, int key, int value) {
        if (key == 0) {
            this.packetCount = Mth.clamp(value, 1, 32);
            this.updateGuiField("packetCount");
        }
        if (key == 1) {
            this.energyPacket = Mth.clamp(value, 32768, 2097152); // clamped from 32k to 2M
            this.maxEnergy = this.energyPacket * 32;
            this.updateGuiFields(new String[]{"energyPacket", "maxEnergy"});
        }
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(BlockStateProperties.FACING);
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        if (this.isSimulating() && !this.addedToEnergyNet) {
            this.addedToEnergyNet = true;
            EnergyNet.INSTANCE.addTile(this);
        }
    }

    @Override
    public void onUnloaded(boolean chunk) {
        if (this.isSimulating() && this.addedToEnergyNet) {
            this.addedToEnergyNet = false;
            EnergyNet.INSTANCE.removeTile(this);
        }
        super.onUnloaded(chunk);
    }

    @Override
    public int getSourceTier() {
        return 10; // Extreme output tier
    }

    @Override
    public int getSinkTier() {
        return 10; // Accept anything
    }

    @Override
    public int getMaxEnergyOutput() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getProvidedEnergy() {
        return this.energy < this.energyPacket ? 0 : this.energyPacket;
    }

    @Override
    public void consumeEnergy(int consumed) {
        this.energy -= consumed;
        syncAndSaveIfNeeded();
    }

    @Override
    public boolean canEmitEnergy(IEnergyAcceptor acceptor, Direction side) {
        return this.getFacing() != side; // Emit to all sides except input
    }

    @Override
    public boolean canAcceptEnergy(IEnergyEmitter emitter, Direction side) {
        return this.getFacing() == side; // Accept only from facing
    }

    @Override
    public int getStoredEU() {
        return this.energy;
    }

    @Override
    public int getMaxEU() {
        return this.maxEnergy;
    }

    @Override
    public int getTier() {
        return 10;
    }

    @Override
    public int getRequestedEnergy() {
        return Math.max(0, this.maxEnergy - this.energy);
    }

    @Override
    public int acceptEnergy(Direction side, int amount, int voltage) {
        if (amount <= 0) return 0;
        int capacity = Math.max(0, this.maxEnergy - this.energy);
        int added = Math.min(amount, capacity);
        if (added > 0) {
            this.energy += added;
            syncAndSaveIfNeeded();
        }
        return amount - added;
    }

    private long lastGuiSyncTick = -1;
    private long lastSaveTick = -1;
    private int lastNetworkEnergy = -1;

    private void syncAndSaveIfNeeded() {
        if (level == null || level.isClientSide) return;
        long currentTick = level.getGameTime();
        
        // GUI update every 4 ticks
        if (currentTick - lastGuiSyncTick >= 4 || this.energy == 0 || this.energy == this.maxEnergy) {
            if (this.lastNetworkEnergy != this.energy) {
                this.updateGuiField("energy");
                this.lastNetworkEnergy = this.energy;
                this.lastGuiSyncTick = currentTick;
            }
        }
        
        // Chunk NBT save every 100 ticks (5 seconds) to prevent disk I/O spam
        if (currentTick - lastSaveTick >= 100 || this.energy == 0 || this.energy == this.maxEnergy) {
            this.setChanged();
            this.lastSaveTick = currentTick;
        }
    }

    @Override
    public boolean hasMultiplePackets() {
        return this.packetCount > 1;
    }

    @Override
    public int getPacketCount() {
        return this.packetCount;
    }

    @Override
    public java.util.List<net.minecraft.network.chat.Component> getDiagnostics() {
        java.util.List<net.minecraft.network.chat.Component> diag = new java.util.ArrayList<>();
        diag.add(net.minecraft.network.chat.Component.literal("[Energy]: " + this.energy + " / " + this.maxEnergy + " EU").withStyle(net.minecraft.ChatFormatting.GREEN));
        diag.add(net.minecraft.network.chat.Component.literal("[Tier]: " + this.getTier()).withStyle(net.minecraft.ChatFormatting.GREEN));
        return diag;
    }
}
