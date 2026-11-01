package com.afsu.mod.assembler;

import com.afsu.mod.AFSUMod;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.block.base.tiles.BaseInventoryTileEntity;
import ic2.core.inventory.base.ITileGui;
import ic2.core.inventory.container.IC2Container;
import ic2.core.inventory.handler.InventoryHandler;
import ic2.core.inventory.handler.AccessRule;
import ic2.core.inventory.handler.SlotType;
import ic2.core.inventory.filter.SpecialFilters;
import ic2.api.util.DirectionList;
import ic2.core.utils.helpers.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

import ic2.api.tiles.readers.IProgressMachine;
import ic2.api.tiles.readers.IEUStorage;
import ic2.api.network.buffer.NetworkInfo;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.fluid.IC2Tank;
import ic2.core.block.base.misc.BucketFiller;
import ic2.core.block.base.features.ITickListener;
import com.afsu.mod.tools.IDebuggableMachine;
import java.util.List;
import java.util.ArrayList;

public class AdvancedAssemblerTileEntity extends BaseInventoryTileEntity implements ITileGui, IEnergySink, net.minecraft.world.MenuProvider, IProgressMachine, IEUStorage, IDebuggableMachine, ITickListener, ic2.core.block.base.features.IWrenchableTile {

    public static final int[] INPUT_SLOTS = {0, 1, 2, 3, 4, 5};
    public static final int[] OUTPUT_SLOTS = {6, 7};
    public static final int[] UPGRADE_SLOTS = {10, 11, 12, 13};
    public static final int SLOT_FLUID_IN = 14;
    public static final int SLOT_FLUID_OUT = 15;
    public static final int SLOT_WASTE_IN = 16;
    public static final int SLOT_WASTE_OUT = 17;

    @NetworkInfo
    private int progress = 0;
    @NetworkInfo
    private int maxProgress = 100;

    private AdvancedAssemblerRecipe cachedRecipe = null;

    // --- TRACKING FOR TRACER ---
    private long lastTraceMaxEnergy = -1;
    private double lastTraceSpeed = -1;
    private double lastTraceEnergyMult = -1;
    
    private int lastNetworkEnergy = -1;
    private int lastNetworkProgress = -1;

    private void trace(String msg) {
        if (com.afsu.mod.tools.DebugLogger.isLogging()) {
            com.afsu.mod.tools.DebugLogger.log("[Assembler @ " + this.worldPosition.toShortString() + "] " + msg);
        }
    }
    // ---------------------------

    private final net.minecraft.world.item.ItemStack[] lastInputSnapshots = new net.minecraft.world.item.ItemStack[INPUT_SLOTS.length];
    private net.minecraft.world.SimpleContainer recipeContainer = null;

    private boolean addedToEnergyNet = false;
    @NetworkInfo
    private int energy = 0;
    @NetworkInfo
    private int maxEnergy = 40000;
    @NetworkInfo
    private int tier = 3;
    @NetworkInfo
    private double speedMultiplier = 1.0;
    @NetworkInfo
    private double energyDemandMultiplier = 1.0;
    
    public double getSpeedMultiplier() { return speedMultiplier; }
    public double getEnergyDemandMultiplier() { return energyDemandMultiplier; }

    @NetworkInfo
    public final IC2Tank fluidTank = new IC2Tank(16000);
    
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> new IFluidHandler() {
        @Override public int getTanks() { return 2; }
        @Override public net.minecraftforge.fluids.FluidStack getFluidInTank(int tank) { return tank == 0 ? fluidTank.getFluid() : wasteTank.getFluid(); }
        @Override public int getTankCapacity(int tank) { return tank == 0 ? fluidTank.getCapacity() : wasteTank.getCapacity(); }
        @Override public boolean isFluidValid(int tank, net.minecraftforge.fluids.FluidStack stack) { return tank == 0 && fluidTank.isFluidValid(stack); }
        @Override public int fill(net.minecraftforge.fluids.FluidStack resource, FluidAction action) { return fluidTank.fill(resource, action); }
        @Override public net.minecraftforge.fluids.FluidStack drain(net.minecraftforge.fluids.FluidStack resource, FluidAction action) { 
            return wasteTank.drain(resource, action);
        }
        @Override public net.minecraftforge.fluids.FluidStack drain(int maxDrain, FluidAction action) {
            return wasteTank.drain(maxDrain, action);
        }
    });

    private final BucketFiller bucketFiller = new BucketFiller(this, fluidTank, SLOT_FLUID_IN, SLOT_FLUID_OUT);

    @NetworkInfo
    public final IC2Tank wasteTank = new IC2Tank(16000);
    private final BucketFiller wasteBucketFiller = new BucketFiller(this, wasteTank, SLOT_WASTE_IN, SLOT_WASTE_OUT);


    public AdvancedAssemblerTileEntity(BlockPos pos, BlockState state) {
        super(pos, state, 18);
        this.addGuiFields("fluidTank", "wasteTank", "energy", "progress", "maxProgress", "tier", "maxEnergy");
        this.fluidTank.addListener(tank -> {
            this.updateGuiField("fluidTank");
            this.setChanged();
        });
        this.wasteTank.addListener(tank -> {
            this.updateGuiField("wasteTank");
            this.setChanged();
        });
    }
    
    @Override
    protected void addSlotInfo(InventoryHandler handler) {
        handler.registerBlockSides(DirectionList.ALL);
        handler.registerBlockAccess(DirectionList.ALL, AccessRule.BOTH);
        
        for (int i : INPUT_SLOTS) {
            handler.registerSlotAccess(AccessRule.IMPORT, i);
            handler.registerNamedSlot(SlotType.INPUT, i);
        }
        for (int i : OUTPUT_SLOTS) {
            handler.registerSlotAccess(AccessRule.EXPORT, i);
            handler.registerNamedSlot(SlotType.OUTPUT, i);
            handler.registerInputFilter(SpecialFilters.ALWAYS_FALSE, i);
        }
        for (int i : UPGRADE_SLOTS) {
            handler.registerNamedSlot(SlotType.UPGRADES, i);
            handler.registerInputFilter(T -> T.getItem() instanceof ic2.api.items.IUpgradeItem, i);
        }
        
        handler.registerSlotAccess(AccessRule.IMPORT, SLOT_FLUID_IN);
        handler.registerNamedSlot(SlotType.INPUT, SLOT_FLUID_IN);
        
        
        handler.registerSlotAccess(AccessRule.EXPORT, SLOT_FLUID_OUT);
        handler.registerNamedSlot(SlotType.OUTPUT, SLOT_FLUID_OUT);
        handler.registerInputFilter(SpecialFilters.ALWAYS_FALSE, SLOT_FLUID_OUT);

        handler.registerSlotAccess(AccessRule.IMPORT, SLOT_WASTE_IN);
        handler.registerNamedSlot(SlotType.INPUT, SLOT_WASTE_IN);
        handler.registerSlotAccess(AccessRule.EXPORT, SLOT_WASTE_OUT);
        handler.registerNamedSlot(SlotType.OUTPUT, SLOT_WASTE_OUT);
        handler.registerInputFilter(SpecialFilters.ALWAYS_FALSE, SLOT_WASTE_OUT);

    }
    
    public net.minecraftforge.fluids.capability.templates.FluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        NBTUtils.putInt(compound, "energy", this.energy, 0);
        NBTUtils.putInt(compound, "progress", this.progress, 0);
        NBTUtils.putInt(compound, "maxProgress", this.maxProgress, 0);
        compound.put("Fluid", fluidTank.writeToNBT(new CompoundTag()));
        compound.put("WasteFluid", wasteTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.energy = NBTUtils.getInt(compound, "energy", 0);
        this.progress = NBTUtils.getInt(compound, "progress", 0);
        this.maxProgress = NBTUtils.getInt(compound, "maxProgress", 0);
        if (compound.contains("Fluid")) {
            fluidTank.readFromNBT(compound.getCompound("Fluid"));
        }
        if (compound.contains("WasteFluid")) {
            wasteTank.readFromNBT(compound.getCompound("WasteFluid"));
        }
        applyUpgrades();
        this.energy = Math.min(this.energy, this.maxEnergy);
    }

    @Override
    public void onLoaded() {
        if (this.hasLevel() && this.getBlockState().hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)) {
            this.setFacingSilent(this.getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING));
        }
        super.onLoaded();
        if (this.isSimulating() && !this.addedToEnergyNet) {
            this.addedToEnergyNet = true;
            EnergyNet.INSTANCE.addTile(this);
            trace("РЎР‚РЎСџРІР‚СњР Р‰ ON_LOADED: Added to EnergyNet");
        }
    }

    @Override
    public void onUnloaded(boolean chunk) {
        if (this.isSimulating() && this.addedToEnergyNet) {
            this.addedToEnergyNet = false;
            EnergyNet.INSTANCE.removeTile(this);
            trace("РЎР‚РЎСџРІР‚СњР Р‰ ON_UNLOADED: Removed from EnergyNet");
        }
        super.onUnloaded(chunk);
    }
    
    @Override
    public int getSinkTier() {
        return this.tier;
    }

    @Override
    public int getRequestedEnergy() {
        return Math.max(0, this.maxEnergy - this.energy);
    }

    @Override
    public int acceptEnergy(Direction side, int amount, int voltage) {
        if (amount <= 0) return 0;
        
        long capLong = (long) this.maxEnergy - (long) this.energy;
        int capacity = (int) Math.max(0, Math.min(Integer.MAX_VALUE, capLong));
        int added = Math.min(amount, capacity);
        
        if (added > 0) {
            this.energy += added;

        }
        
        return amount - added;
    }
    
    @Override
    public float getProgress() {
        return this.progress;
    }
    
    @Override
    public float getMaxProgress() {
        return this.maxProgress;
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
        return this.tier;
    }
    
    @Override
    public boolean canAcceptEnergy(IEnergyEmitter emitter, Direction side) {
        return true;
    }
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHandler.invalidate();
    }

    @Override
    public void onTick() {
        if (level == null || level.isClientSide) return;
        applyUpgrades();
        processRecipes();
        
        // Throttle GUI updates to prevent network flood & rubber-banding
        if (this.clock(4) || this.energy == 0 || this.energy == this.maxEnergy || this.progress == 0) {
            if (this.lastNetworkEnergy != this.energy) {
                this.updateGuiField("energy");
                this.lastNetworkEnergy = this.energy;
            }
            if (this.lastNetworkProgress != this.progress) {
                this.updateGuiField("progress");
                this.lastNetworkProgress = this.progress;
            }
        }
    }
    
    @Override
    public boolean checkTicking() {
        return this.isRemoved();
    }

    private void processRecipes() {
        bucketFiller.fillTank();
        wasteBucketFiller.fillItem();
        
        boolean inputsChanged = false;
        for (int i = 0; i < INPUT_SLOTS.length; i++) {
            int slot = INPUT_SLOTS[i];
            net.minecraft.world.item.ItemStack currentStack = this.inventory.get(slot);
            net.minecraft.world.item.ItemStack lastStack = lastInputSnapshots[i];
            if (lastStack == null || !net.minecraft.world.item.ItemStack.matches(lastStack, currentStack)) {
                inputsChanged = true;
                lastInputSnapshots[i] = currentStack.copy();
            }
        }
        
        if (recipeContainer == null) {
            recipeContainer = new net.minecraft.world.SimpleContainer(16);
            inputsChanged = true;
        }

        if (inputsChanged) {
            this.setChanged();
            AdvancedAssemblerRecipe oldRecipe = cachedRecipe;
            net.minecraft.world.item.ItemStack[] invArray = this.inventory.toArray(new net.minecraft.world.item.ItemStack[0]);
            for (int i = 0; i < invArray.length && i < recipeContainer.getContainerSize(); i++) {
                recipeContainer.setItem(i, invArray[i]);
            }
            net.minecraft.world.item.crafting.RecipeManager recipeManager = level.getRecipeManager();
            cachedRecipe = recipeManager.getRecipeFor(com.afsu.mod.assembler.AdvancedAssemblerRecipeType.INSTANCE, recipeContainer, level).orElse(null);
            
            if (oldRecipe != cachedRecipe && progress > 0) {
                progress = 0;

                this.setChanged();
            }
        }
        
        if (cachedRecipe != null) {
            AdvancedAssemblerRecipe rec = cachedRecipe;
            int euPerTick = rec.getEuCost();
            double speedMultiplier = this.speedMultiplier;
            double energyMultiplier = this.energyDemandMultiplier;
            int actualEuPerTick = calculateActualEuPerTick(rec);
            int baseDuration = rec.getDuration();
            this.maxProgress = (int) Math.max(1, Math.round(baseDuration / speedMultiplier));
            this.updateGuiField("maxProgress");
            
                        if (this.energy >= actualEuPerTick) {
                net.minecraft.world.item.ItemStack resultPreview = rec.getResultItem();
                net.minecraft.world.item.ItemStack secResultPreview = rec.getSecondaryResult() != null ? rec.getSecondaryResult() : net.minecraft.world.item.ItemStack.EMPTY;
                
                java.util.List<net.minecraft.world.item.ItemStack> neededOutputs = new java.util.ArrayList<>();
                if (!resultPreview.isEmpty()) neededOutputs.add(resultPreview);
                if (!secResultPreview.isEmpty()) neededOutputs.add(secResultPreview);
                
                net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> remainingPreview = rec.getRemainingItems(recipeContainer);
                for (int i = 0; i < 6; i++) {
                    net.minecraft.world.item.ItemStack inSlot = this.inventory.get(INPUT_SLOTS[i]);
                    net.minecraft.world.item.ItemStack rem = remainingPreview.get(INPUT_SLOTS[i]);
                    if (rem != null && !rem.isEmpty() && inSlot.getCount() > 1) {
                        neededOutputs.add(rem.copy());
                    }
                }

                if (canInsertAll(neededOutputs)) {
                    boolean hasFluid = true;
                    if (rec.getFluidRequirement() != null && !rec.getFluidRequirement().isEmpty()) {
                        net.minecraftforge.fluids.FluidStack drained = fluidTank.drain(rec.getFluidRequirement(), net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
                        if (drained.isEmpty() || drained.getAmount() < rec.getFluidRequirement().getAmount()) {
                            hasFluid = false;
                        } else {
                            String inFluid = net.minecraftforge.registries.ForgeRegistries.FLUIDS.getKey(rec.getFluidRequirement().getFluid()).getPath();
                            net.minecraft.world.level.material.Fluid outFluid = null;
                            if (inFluid.equals("sulfuric_acid")) {
                                outFluid = com.afsu.mod.registry.AFSUFluids.WASTE_ACID.get();
                            } else if (inFluid.equals("hydrogen_peroxide")) {
                                outFluid = com.afsu.mod.registry.AFSUFluids.TOXIC_SLUDGE.get();
                            }
                            if (outFluid != null) {
                                int fillable = wasteTank.fill(new net.minecraftforge.fluids.FluidStack(outFluid, rec.getFluidRequirement().getAmount()), net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
                                if (fillable < rec.getFluidRequirement().getAmount()) {
                                    hasFluid = false;
                                }
                            }
                        }
                    }
                    
                    if (hasFluid) {
                        this.energy -= actualEuPerTick;
                        progress++;

                        if (progress >= maxProgress) {
                            net.minecraft.world.item.ItemStack result = rec.assemble(recipeContainer);
                            net.minecraft.world.item.ItemStack secResult = rec.getSecondaryResult() != null ? rec.getSecondaryResult().copy() : net.minecraft.world.item.ItemStack.EMPTY;
                            if (rec.getFluidRequirement() != null && !rec.getFluidRequirement().isEmpty()) {
                                fluidTank.drain(rec.getFluidRequirement(), net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                                
                                String inFluid = net.minecraftforge.registries.ForgeRegistries.FLUIDS.getKey(rec.getFluidRequirement().getFluid()).getPath();
                                net.minecraft.world.level.material.Fluid outFluid = null;
                                if (inFluid.equals("sulfuric_acid")) {
                                    outFluid = com.afsu.mod.registry.AFSUFluids.WASTE_ACID.get();
                                } else if (inFluid.equals("hydrogen_peroxide")) {
                                    outFluid = com.afsu.mod.registry.AFSUFluids.TOXIC_SLUDGE.get();
                                }
                                if (outFluid != null) {
                                    wasteTank.fill(new net.minecraftforge.fluids.FluidStack(outFluid, rec.getFluidRequirement().getAmount()), net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                            
                            insertResult(result);
                            if (!secResult.isEmpty()) {
                                insertResult(secResult);
                            }
                            consumeIngredients(rec);
                            
                            progress = 0;
                            this.setChanged();
                        }
                    }
                }            }
        }
    }
    
    private void consumeIngredients(AdvancedAssemblerRecipe recipe) {
        int[] invCounts = new int[6];
        int[] initialCounts = new int[6];
        net.minecraft.world.item.ItemStack[] invStacks = new net.minecraft.world.item.ItemStack[6];
        for (int i = 0; i < 6; i++) {
            invStacks[i] = this.inventory.get(INPUT_SLOTS[i]);
            initialCounts[i] = invStacks[i].getCount();
            invCounts[i] = initialCounts[i];
        }

        for (ic2.api.recipes.ingridients.inputs.IInput ing : recipe.getInputs()) {
            int needed = ing.getInputSize();
            for (int j = 0; j < 6; j++) {
                if (invCounts[j] > 0 && ing.matches(invStacks[j])) {
                    int consume = Math.min(needed, invCounts[j]);
                    invCounts[j] -= consume;
                    needed -= consume;
                    if (needed <= 0) break;
                }
            }
        }

        net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> remaining = recipe.getRemainingItems(recipeContainer);

        for (int i = 0; i < 6; i++) {
            int consumed = initialCounts[i] - invCounts[i];
            if (consumed > 0) {
                net.minecraft.world.item.ItemStack stackInSlot = this.inventory.get(INPUT_SLOTS[i]);
                stackInSlot.shrink(consumed);
                if (stackInSlot.isEmpty()) {
                    this.inventory.set(INPUT_SLOTS[i], net.minecraft.world.item.ItemStack.EMPTY);
                }
                net.minecraft.world.item.ItemStack rem = remaining.get(INPUT_SLOTS[i]);
                if (rem != null && !rem.isEmpty()) {
                    net.minecraft.world.item.ItemStack returnStack = rem.copy();
                    returnStack.setCount(consumed);
                    if (this.inventory.get(INPUT_SLOTS[i]).isEmpty()) {
                        this.inventory.set(INPUT_SLOTS[i], returnStack);
                    } else {
                        insertResult(returnStack);
                    }
                }
            }
        }
        this.setChanged();
    }
    private void applyUpgrades() {
        this.tier = 3;
        long newMaxEnergy = 40000;
        this.speedMultiplier = 1.0;
        this.energyDemandMultiplier = 1.0;
        for (int i : UPGRADE_SLOTS) {
            net.minecraft.world.item.ItemStack stack = this.inventory.get(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ic2.api.items.IUpgradeItem) {
                ic2.api.items.IUpgradeItem upgrade = (ic2.api.items.IUpgradeItem) stack.getItem();
                this.tier += upgrade.getExtraTier(stack, null) * stack.getCount();
                newMaxEnergy += (long) upgrade.getExtraEnergyStorage(stack, null) * stack.getCount();
                this.speedMultiplier *= Math.pow(upgrade.getProcessingSpeedMultiplier(stack, null), stack.getCount());
                this.energyDemandMultiplier *= Math.pow(upgrade.getEnergyDemandMultiplier(stack, null), stack.getCount());
            }
        }
        if (newMaxEnergy > Integer.MAX_VALUE) {
            this.maxEnergy = Integer.MAX_VALUE;
        } else {
            this.maxEnergy = (int) newMaxEnergy;
        }
        
        if (this.maxEnergy != lastTraceMaxEnergy || this.speedMultiplier != lastTraceSpeed || this.energyDemandMultiplier != lastTraceEnergyMult) {
            this.setChanged();
            trace("РЎР‚РЎСџРІР‚С”Р’В Р С—РЎвЂР РЏ UPGRADES CHANGED! Recalculated: maxEnergy=" + this.maxEnergy + ", speedMult=" + String.format("%.2f", this.speedMultiplier) + ", energyMult=" + String.format("%.2f", this.energyDemandMultiplier));
            lastTraceMaxEnergy = this.maxEnergy;
            lastTraceSpeed = this.speedMultiplier;
            lastTraceEnergyMult = this.energyDemandMultiplier;
        }
    }
    private boolean canInsertAll(java.util.List<net.minecraft.world.item.ItemStack> toInsert) {
        if (toInsert == null || toInsert.isEmpty()) return true;
        
        net.minecraft.world.item.ItemStack[] simSlots = new net.minecraft.world.item.ItemStack[OUTPUT_SLOTS.length];
        for (int i = 0; i < OUTPUT_SLOTS.length; i++) {
            simSlots[i] = this.inventory.get(OUTPUT_SLOTS[i]).copy();
        }
        
        for (net.minecraft.world.item.ItemStack req : toInsert) {
            if (req.isEmpty()) continue;
            int remaining = req.getCount();
            
            for (int i = 0; i < simSlots.length; i++) {
                if (!simSlots[i].isEmpty() && net.minecraft.world.item.ItemStack.isSameItemSameTags(simSlots[i], req)) {
                    int space = simSlots[i].getMaxStackSize() - simSlots[i].getCount();
                    int toAdd = Math.min(space, remaining);
                    simSlots[i].grow(toAdd);
                    remaining -= toAdd;
                    if (remaining <= 0) break;
                }
            }
            
            if (remaining > 0) {
                for (int i = 0; i < simSlots.length; i++) {
                    if (simSlots[i].isEmpty()) {
                        int toAdd = Math.min(req.getMaxStackSize(), remaining);
                        simSlots[i] = req.copy();
                        simSlots[i].setCount(toAdd);
                        remaining -= toAdd;
                        if (remaining <= 0) break;
                    }
                }
            }
            
            if (remaining > 0) {
                return false;
            }
        }
        return true;
    }

    private void insertResult(net.minecraft.world.item.ItemStack stack) {
        if (stack.isEmpty()) return;
        net.minecraft.world.item.ItemStack remaining = stack.copy();

        // Pass 1: Try stacking into existing non-empty output slots
        for (int i : OUTPUT_SLOTS) {
            net.minecraft.world.item.ItemStack slot = this.inventory.get(i);
            if (!slot.isEmpty() && net.minecraft.world.item.ItemStack.isSameItemSameTags(slot, remaining)) {
                int space = slot.getMaxStackSize() - slot.getCount();
                if (space > 0) {
                    int toAdd = Math.min(space, remaining.getCount());
                    slot.grow(toAdd);
                    remaining.shrink(toAdd);
                    this.setChanged();
                    if (remaining.isEmpty()) return;
                }
            }
        }

        // Pass 2: Place remainder into empty output slots
        for (int i : OUTPUT_SLOTS) {
            net.minecraft.world.item.ItemStack slot = this.inventory.get(i);
            if (slot.isEmpty()) {
                this.inventory.set(i, remaining.copy());
                this.setChanged();
                return;
            }
        }
    }
    


    @Override
    public BlockEntityType<?> createType() {
        return com.afsu.mod.registry.AFSUBlockEntities.ADVANCED_ASSEMBLER_ENTITY.get();
    }

    @Override
    public boolean isAllowingUI() {
        return true;
    }

    @Override
    public IC2Container createContainer(Player player, InteractionHand hand, Direction side, int windowID) {
        return new AdvancedAssemblerContainer(this, player, windowID);
    }
    
    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("block.afsu.advanced_assembler");
    }

    @Override
    public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory playerInventory, Player player) {
        return new AdvancedAssemblerContainer(this, player, id);
    }

    private int calculateActualEuPerTick(AdvancedAssemblerRecipe rec) {
        double cost = rec.getEuCost() * this.energyDemandMultiplier;
        if (cost > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) Math.round(cost);
    }

    @Override
    public List<net.minecraft.network.chat.Component> getDiagnostics() {
        List<net.minecraft.network.chat.Component> diag = new ArrayList<>();
        diag.add(net.minecraft.network.chat.Component.literal("[Energy]: " + this.energy + " / " + this.maxEnergy + " EU").withStyle(net.minecraft.ChatFormatting.GREEN));
        diag.add(net.minecraft.network.chat.Component.literal("[Upgrade Multipliers]: Speed x" + String.format("%.2f", this.speedMultiplier) + ", Consumption x" + String.format("%.2f", this.energyDemandMultiplier)).withStyle(net.minecraft.ChatFormatting.GREEN));
        
        if (this.cachedRecipe != null) {
            int baseEuPerTick = this.cachedRecipe.getEuCost();
            int actualEuPerTick = calculateActualEuPerTick(this.cachedRecipe);
            
            diag.add(net.minecraft.network.chat.Component.literal("[Recipe]: " + this.cachedRecipe.getResultItem().getHoverName().getString()).withStyle(net.minecraft.ChatFormatting.GREEN));
            diag.add(net.minecraft.network.chat.Component.literal("[EU/tick]: " + actualEuPerTick + " (base: " + baseEuPerTick + ")").withStyle(net.minecraft.ChatFormatting.GREEN));
            
            if (actualEuPerTick > this.maxEnergy) {
                diag.add(net.minecraft.network.chat.Component.literal("[ERROR]: Recipe cost exceeds maximum machine buffer!").withStyle(net.minecraft.ChatFormatting.RED));
                diag.add(net.minecraft.network.chat.Component.literal("[SOLUTION]: 'Energy Storage Upgrades' required.").withStyle(net.minecraft.ChatFormatting.RED));
            } else if (this.energy < actualEuPerTick) {
                diag.add(net.minecraft.network.chat.Component.literal("[STATUS]: Waiting for energy.").withStyle(net.minecraft.ChatFormatting.YELLOW));
            } else {
                diag.add(net.minecraft.network.chat.Component.literal("[STATUS]: Crafting (" + this.progress + "/" + this.maxProgress + ")").withStyle(net.minecraft.ChatFormatting.GREEN));
            }
        } else {
            diag.add(net.minecraft.network.chat.Component.literal("[STATUS]: Recipe not found.").withStyle(net.minecraft.ChatFormatting.YELLOW));
        }
        return diag;
    }

    // --- IWrenchableTile implementation ---
    @Override
    public boolean canSetFacing(Direction dir) {
        return dir.getAxis().isHorizontal() && dir != this.getFacing();
    }

    @Override
    public void setFacing(Direction dir) {
        super.setFacing(dir);
        if (this.level != null && !this.level.isClientSide) {
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING, dir));
        }
    }

    @Override
    public boolean canRemoveBlock(Player player) {
        return true;
    }

    @Override
    public double getDropRate(Player player) {
        return 0.8D;
    }
}


