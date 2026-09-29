package com.afsu.mod.solar;

import ic2.api.energy.tile.IEnergySource;
import ic2.api.tiles.readers.IEUProducer;
import ic2.api.util.DirectionList;
import ic2.core.block.base.features.ITickListener;
import ic2.core.block.base.features.ITileActivityProvider;
import ic2.core.block.base.features.IWrenchableTile;
import ic2.core.block.base.tiles.impls.BaseGeneratorTileEntity;
import ic2.core.inventory.base.ITileGui;
import ic2.core.inventory.container.IC2Container;
import ic2.core.inventory.filter.special.ElectricItemFilter;
import ic2.core.inventory.handler.AccessRule;
import ic2.core.inventory.handler.InventoryHandler;
import ic2.core.inventory.handler.SlotType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import ic2.api.items.electric.ElectricItem;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;

import com.afsu.mod.tools.IDebuggableMachine;

public abstract class BaseSuperSolarTileEntity extends BaseGeneratorTileEntity implements ITickListener, IEnergySource, ITileGui, IWrenchableTile, IEUProducer, ITileActivityProvider, IDebuggableMachine {
    @ic2.api.network.buffer.NetworkInfo
    public boolean day = false;
    protected final int lowerProduction;
    protected final int maxOutput;
    protected int emittedThisTick = 0;
    private long lastEmitResetTick = -1;
    
    
    private void resetEmitIfNewTick() {
        if (this.level != null && this.level.getGameTime() != lastEmitResetTick) {
            lastEmitResetTick = this.level.getGameTime();
            emittedThisTick = 0;
        }
    }

    public BaseSuperSolarTileEntity(BlockPos pos, BlockState state, int generation, int maxStorage, int tier) {
        super(pos, state, tier);
        this.maxOutput = generation;
        this.production = generation / 2;
        this.lowerProduction = this.production / 8;
        this.maxStorage = maxStorage;
        this.tier = tier;
        this.addComparator(ic2.core.block.base.misc.comparator.types.base.FlagComparator.createTile("active", ic2.core.block.base.misc.comparator.ComparatorNames.ACTIVE, this));
        this.addGuiFields("day");
    }

    public BaseSuperSolarTileEntity(BlockPos pos, BlockState state, int maxOutput, int dayProduction, int nightProduction, int maxStorage, int tier) {
        super(pos, state, tier);
        this.maxOutput = maxOutput;
        this.production = dayProduction;
        this.lowerProduction = nightProduction;
        this.maxStorage = maxStorage;
        this.tier = tier;
        this.addComparator(ic2.core.block.base.misc.comparator.types.base.FlagComparator.createTile("active", ic2.core.block.base.misc.comparator.ComparatorNames.ACTIVE, this));
        this.addGuiFields("day");
    }
    
    @Override
    protected void addSlotInfo(InventoryHandler handler) {
        handler.registerBlockSides(DirectionList.UP.invert());
        handler.registerBlockAccess(DirectionList.UP.invert(), AccessRule.BOTH);
        handler.registerSlotAccess(AccessRule.BOTH, 0, 1, 2, 3);
        for (int i = 0; i < 4; i++) {
            handler.setSlotAccess(i, Direction.UP, AccessRule.DISABLED);
        }
        handler.registerSlotsForSide(DirectionList.UP.invert(), 0, 1, 2, 3);
        handler.registerInputFilter(ElectricItemFilter.CHARGE_FILTER, 0, 1, 2, 3);
        handler.registerOutputFilter(ElectricItemFilter.NOT_CHARGE_FILTER, 0, 1, 2, 3);
        handler.registerNamedSlot(SlotType.CHARGE, 0, 1, 2, 3);
    }
    
    @Override
    public IC2Container createContainer(Player player, InteractionHand interactionHand, Direction direction, int i) {
        return new SuperSolarPanelContainer(this, player, i);
    }
    
    @Override
    public int getMaxEnergyOutput() {
        return this.maxOutput;
    }
    
    @Override
    public int getProvidedEnergy() {
        resetEmitIfNewTick();
        return Math.max(0, Math.min(this.storage, this.maxOutput - this.emittedThisTick));
    }
    
    @Override
    public int getMaxEU() {
        return this.maxStorage;
    }
    
    @Override
    public int getStoredEU() {
        return this.storage;
    }
    
    @Override
    public void consumeEnergy(int amount) {
        super.consumeEnergy(amount);
        this.emittedThisTick += amount;
    }
    
    @Override
    public int getFuel() { return 0; }
    
    @Override
    public int getMaxFuel() { return 0; }

    @Override
    public boolean gainFuel() {
        return false;
    }


    public int getCurrentOutput() {
        if (!isActive()) return 0;
        return (this.day && skyBlockCheck()) ? this.production : this.lowerProduction;
    }

    public boolean skyBlockCheck() {
        if (this.getLevel() == null) return false;
        return this.getLevel().canSeeSkyFromBelowWater(this.getBlockPos().above()) && this.getLevel().dimensionType().hasSkyLight();
    }
  
    public boolean isSunVisible() {
        if (this.level == null) return false;
        return isSunVisible(this.level, this.getBlockPos().above());
    }
  
    public static boolean isSunVisible(Level world, BlockPos pos) {
        if (!world.isDay()) return false;
        Holder<Biome> biome = world.getBiome(pos);
        return biome.get().getPrecipitation() == Biome.Precipitation.NONE || (!world.isRaining() && !world.isThundering());
    }

    public boolean isConverting() {
        if (this.skyBlockCheck()) {
            return this.storage < this.maxStorage;
        } else {
            return false;
        }
    }
    
    @Override
    public void onTick() {
        resetEmitIfNewTick();
        if (this.level != null && !this.level.isClientSide()) {
            if (this.clock(64)) {
                this.day = isSunVisible();
                this.updateGuiField("day");
            }
            
            int oldEnergy = this.storage;
            boolean converting = isConverting();
            this.setActive(converting);
            
            if (converting) {
                int toAdd = getCurrentOutput();
                if (this.maxStorage - this.storage < toAdd) {
                    this.storage = this.maxStorage;
                } else {
                    this.storage += toAdd;
                }
            }
            
            if (this.storage > 0) {
                for (ItemStack tStack : this.inventory) {
                    if (this.storage <= 0) break;
                    if (tStack.isEmpty()) continue;
                    int charged = ElectricItem.MANAGER.charge(tStack, this.storage, this.tier, false, false);
                    this.storage -= charged;
                }
            }
            
            if (oldEnergy != this.storage) {
                // Throttle GUI updates to prevent network flooding and UI energy bar rubber-banding
                if (this.clock(4) || this.storage == 0 || this.storage == this.maxStorage) {
                    this.updateGuiField("storage");
                }
            }
            
        }
    }
    
    @Override
    public float getEUProduction() {
        return Math.min((float)this.storage, (float)getCurrentOutput());
    }

    public int getStorage() { return this.storage; }
    public int getMaxStorage() { return this.maxStorage; }
    public int getProduction() { return this.production; }
    public int getLowerProduction() { return this.lowerProduction; }
    public int getTier() { return this.tier; }

    @Override
    public ic2.api.energy.tile.IEnergySource.SourceType getSourceType() {
        return ic2.api.energy.tile.IEnergySource.SourceType.PASSIVE_PRODUCING;
    }
    
    @Override
    public boolean canEmitEnergy(ic2.api.energy.tile.IEnergyAcceptor acceptor, Direction side) {
        return true; // Emit in all directions, including UP
    }

    @Override
    public java.util.List<net.minecraft.network.chat.Component> getDiagnostics() {
        java.util.List<net.minecraft.network.chat.Component> diag = new java.util.ArrayList<>();
        diag.add(net.minecraft.network.chat.Component.literal("[Energy]: " + this.getStorage() + " / " + this.getMaxStorage() + " EU").withStyle(net.minecraft.ChatFormatting.GREEN));
        diag.add(net.minecraft.network.chat.Component.literal("[Generation]: " + this.getCurrentOutput() + " EU/tick (" + this.getProduction() + " Day / " + this.getLowerProduction() + " Night)").withStyle(net.minecraft.ChatFormatting.GREEN));
        diag.add(net.minecraft.network.chat.Component.literal("[Sky Visible]: " + this.skyBlockCheck()).withStyle(net.minecraft.ChatFormatting.GREEN));
        return diag;
    }

    @Override
    public abstract BlockEntityType<?> createType();

    public static BlockEntityType<?> getQuantumType() { return com.afsu.mod.registry.AFSUBlockEntities.QUANTUM_SOLAR_PANEL_ENTITY.get(); }
    public static BlockEntityType<?> getPhotonType() { return com.afsu.mod.registry.AFSUBlockEntities.PHOTON_SOLAR_PANEL_ENTITY.get(); }
    public static BlockEntityType<?> getSingularityType() { return com.afsu.mod.registry.AFSUBlockEntities.SINGULARITY_SOLAR_PANEL_ENTITY.get(); }
    public static BlockEntityType<?> getAbsoluteType() { return com.afsu.mod.registry.AFSUBlockEntities.ABSOLUTE_SOLAR_PANEL_ENTITY.get(); }
}

