package com.afsu.mod.transformer;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IMultiEnergySource;
import ic2.api.network.buffer.NetworkInfo;
import ic2.api.network.tile.INetworkClientEventListener;
import ic2.api.tiles.readers.IEUStorage;
import ic2.core.block.base.features.ITickListener;
import ic2.core.block.base.features.IWrenchableTile;
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

public class PlasmaTransformerTileEntity extends BaseTileEntity implements IMultiEnergySource, IEnergySink, IEUStorage, ITileGui, INetworkClientEventListener, IWrenchableTile, ITickListener, IDebuggableMachine {

    public final int highOutput = 65536;

    @NetworkInfo
    public int energy;
    @NetworkInfo(value = NetworkInfo.BitLevel.BIT_8)
    public int packetCount = 32;
    @NetworkInfo
    public int energyPacket = 2048; // Default 2048 EU (EV), safe for standard EV machinery!
    @NetworkInfo
    public int maxEnergy = 65536 * 4; // 262,144 EU capacity
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
        NBTUtils.putInt(compound, "max_energy", this.maxEnergy, 65536 * 4);
        NBTUtils.putInt(compound, "packets", this.packetCount, 32);
        NBTUtils.putInt(compound, "packet_energy", this.energyPacket, 2048);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.energy = NBTUtils.getInt(compound, "energy", 0);
        this.maxEnergy = NBTUtils.getInt(compound, "max_energy", 65536 * 4);
        this.packetCount = NBTUtils.getInt(compound, "packets", 32);
        this.energyPacket = NBTUtils.getInt(compound, "packet_energy", 2048);
    }

    @Override
    public void onClientDataReceived(Player player, int key, int value) {
        if (key == 0) {
            this.packetCount = Mth.clamp(value, 1, 32);
            this.updateGuiField("packetCount");
        }
        if (key == 1) {
            this.energyPacket = Mth.clamp(value, 128, 65536);
            this.maxEnergy = Math.max(this.highOutput * 4, this.energyPacket * 32);
            this.updateGuiFields(new String[]{"energyPacket", "maxEnergy"});
            if (this.addedToEnergyNet) {
                EnergyNet.INSTANCE.updateTile(this);
            }
        }
    }

    @Override
    public Direction getFacing() {
        if (this.hasLevel() && this.getBlockState().hasProperty(BlockStateProperties.FACING)) {
            return this.getBlockState().getValue(BlockStateProperties.FACING);
        }
        return super.getFacing();
    }

    @Override
    public void onLoaded() {
        if (this.hasLevel() && this.getBlockState().hasProperty(BlockStateProperties.FACING)) {
            this.setFacingSilent(this.getBlockState().getValue(BlockStateProperties.FACING));
        }
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
    public void onTick() {
        this.handleRedstone();
        this.setActive(this.isRedstonePowered());
    }

    @Override
    public boolean setActive(boolean active) {
        if (super.setActive(active)) {
            if (this.addedToEnergyNet) {
                EnergyNet.INSTANCE.updateTile(this);
            }
            if (this.level != null && !this.level.isClientSide) {
                this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(PlasmaTransformerBlock.ACTIVE, active));
            }
            return true;
        }
        return false;
    }

    // --- IWrenchableTile implementation ---

    @Override
    public boolean canSetFacing(Direction dir) {
        return dir != this.getFacing();
    }

    @Override
    public void setFacing(Direction facing) {
        super.setFacing(facing);
        if (this.level != null && !this.level.isClientSide) {
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(BlockStateProperties.FACING, facing));
        }
        if (this.addedToEnergyNet) {
            EnergyNet.INSTANCE.updateTile(this);
        }
    }

    @Override
    public boolean canRemoveBlock(Player player) {
        return true;
    }

    @Override
    public double getDropRate(Player player) {
        return 1.0D;
    }

    // --- EnergyNet methods ---

    @Override
    public boolean canAcceptEnergy(IEnergyEmitter emitter, Direction side) {
        // Step-down (inactive): High voltage in on facing (5-dot face)
        // Step-up (active): Low voltage in on other 5 faces
        return this.isActive() ? (this.getFacing() != side) : (this.getFacing() == side);
    }

    @Override
    public boolean canEmitEnergy(IEnergyAcceptor acceptor, Direction side) {
        // Step-down (inactive): Low voltage out on other 5 faces
        // Step-up (active): High voltage out on facing (5-dot face)
        return this.isActive() ? (this.getFacing() == side) : (this.getFacing() != side);
    }

    @Override
    public int getSinkTier() {
        return this.isActive() ? EnergyNet.INSTANCE.getTierFromPower(this.energyPacket) : EnergyNet.INSTANCE.getTierFromPower(this.highOutput);
    }

    @Override
    public int getSourceTier() {
        return this.isActive() ? EnergyNet.INSTANCE.getTierFromPower(this.highOutput) : EnergyNet.INSTANCE.getTierFromPower(this.energyPacket);
    }

    @Override
    public int getMaxEnergyOutput() {
        return this.isActive() ? this.highOutput : this.energyPacket;
    }

    @Override
    public int getProvidedEnergy() {
        int provided = this.isActive() ? this.highOutput : this.energyPacket;
        return this.energy < provided ? 0 : provided;
    }

    @Override
    public void consumeEnergy(int consumed) {
        this.energy = Math.max(0, this.energy - consumed);
        syncAndSaveIfNeeded();
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
        return this.getSinkTier();
    }

    @Override
    public int getRequestedEnergy() {
        return Math.max(0, this.maxEnergy - this.energy);
    }

    @Override
    public int acceptEnergy(Direction side, int amount, int voltage) {
        if (amount <= 0) return 0;
        int maxAllowed = this.isActive() ? this.energyPacket : this.highOutput;
        if (amount > maxAllowed) {
            return amount;
        }
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
        return !this.isActive() && this.packetCount > 1;
    }

    @Override
    public int getPacketCount() {
        return !this.isActive() ? this.packetCount : 1;
    }

    @Override
    public java.util.List<net.minecraft.network.chat.Component> getDiagnostics() {
        java.util.List<net.minecraft.network.chat.Component> diag = new java.util.ArrayList<>();
        diag.add(net.minecraft.network.chat.Component.literal("[Mode]: " + (this.isActive() ? "Step-Up" : "Step-Down")).withStyle(net.minecraft.ChatFormatting.GOLD));
        diag.add(net.minecraft.network.chat.Component.literal("[Energy]: " + this.energy + " / " + this.maxEnergy + " EU").withStyle(net.minecraft.ChatFormatting.GREEN));
        diag.add(net.minecraft.network.chat.Component.literal("[Source Tier]: " + this.getSourceTier() + " (" + this.getMaxEnergyOutput() + " EU/p)").withStyle(net.minecraft.ChatFormatting.GREEN));
        diag.add(net.minecraft.network.chat.Component.literal("[Sink Tier]: " + this.getSinkTier()).withStyle(net.minecraft.ChatFormatting.GREEN));
        diag.add(net.minecraft.network.chat.Component.literal("[Packets]: " + this.getPacketCount()).withStyle(net.minecraft.ChatFormatting.GREEN));
        return diag;
    }
}
