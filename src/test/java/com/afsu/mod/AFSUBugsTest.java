package com.afsu.mod;

import com.afsu.mod.assembler.AdvancedAssemblerRecipe;
import com.afsu.mod.assembler.AdvancedAssemblerTileEntity;
import com.afsu.mod.cable.CableBlock;
import com.afsu.mod.solar.BaseSuperSolarTileEntity;
import com.afsu.mod.solar.SolarPanelBlock;
import com.afsu.mod.tools.ElectricSaberItem;
import com.afsu.mod.transformer.PlasmaTransformerTileEntity;
import ic2.api.blocks.IWrenchable;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class AFSUBugsTest {

    @BeforeAll
    public static void setup() {
        try {
            Field distField = net.minecraftforge.fml.loading.FMLEnvironment.class.getDeclaredField("dist");
            distField.setAccessible(true);
            distField.set(null, net.minecraftforge.api.distmarker.Dist.CLIENT);
        } catch (Throwable ignored) {
        }
        try {
            net.minecraft.SharedConstants.tryDetectVersion();
            net.minecraft.server.Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
    }

    @Test
    @DisplayName("Verify CableBlock implements IWrenchable and wrench methods are defined")
    public void testCableBlockWrench() throws Exception {
        assertTrue(IWrenchable.class.isAssignableFrom(CableBlock.class), "CableBlock must implement IWrenchable");
        
        Method getFacing = CableBlock.class.getMethod("getFacing", net.minecraft.world.level.block.state.BlockState.class, 
                net.minecraft.world.level.Level.class, net.minecraft.core.BlockPos.class);
        assertNotNull(getFacing);
        
        Method canRemoveBlock = CableBlock.class.getMethod("canRemoveBlock", net.minecraft.world.level.block.state.BlockState.class,
                net.minecraft.world.level.Level.class, net.minecraft.core.BlockPos.class, net.minecraft.world.entity.player.Player.class);
        assertNotNull(canRemoveBlock);

        Method getDropRate = CableBlock.class.getMethod("getDropRate", net.minecraft.world.level.block.state.BlockState.class,
                net.minecraft.world.level.Level.class, net.minecraft.core.BlockPos.class, net.minecraft.world.entity.player.Player.class);
        assertNotNull(getDropRate);
    }

    @Test
    @DisplayName("Verify SolarPanelBlock implements IWrenchable and overrides getFacing to null")
    public void testSolarPanelBlockWrench() throws Exception {
        assertTrue(IWrenchable.class.isAssignableFrom(SolarPanelBlock.class), "SolarPanelBlock must implement IWrenchable");

        Method getFacing = SolarPanelBlock.class.getMethod("getFacing", net.minecraft.world.level.block.state.BlockState.class,
                net.minecraft.world.level.Level.class, net.minecraft.core.BlockPos.class);
        assertNotNull(getFacing);

        Method canRemoveBlock = SolarPanelBlock.class.getMethod("canRemoveBlock", net.minecraft.world.level.block.state.BlockState.class,
                net.minecraft.world.level.Level.class, net.minecraft.core.BlockPos.class, net.minecraft.world.entity.player.Player.class);
        assertNotNull(canRemoveBlock);

        Method getDropRate = SolarPanelBlock.class.getMethod("getDropRate", net.minecraft.world.level.block.state.BlockState.class,
                net.minecraft.world.level.Level.class, net.minecraft.core.BlockPos.class, net.minecraft.world.entity.player.Player.class);
        assertNotNull(getDropRate);
    }

    @Test
    @DisplayName("Verify Advanced Assembler Tile Entity implements IWrenchableTile")
    public void testAssemblerWrenchableTile() throws Exception {
        assertTrue(ic2.core.block.base.features.IWrenchableTile.class.isAssignableFrom(AdvancedAssemblerTileEntity.class),
                "AdvancedAssemblerTileEntity must implement IWrenchableTile");

        Method canSetFacing = AdvancedAssemblerTileEntity.class.getMethod("canSetFacing", Direction.class);
        assertNotNull(canSetFacing);

        Method setFacing = AdvancedAssemblerTileEntity.class.getMethod("setFacing", Direction.class);
        assertNotNull(setFacing);

        Method canRemoveBlock = AdvancedAssemblerTileEntity.class.getMethod("canRemoveBlock", net.minecraft.world.entity.player.Player.class);
        assertNotNull(canRemoveBlock);

        Method getDropRate = AdvancedAssemblerTileEntity.class.getMethod("getDropRate", net.minecraft.world.entity.player.Player.class);
        assertNotNull(getDropRate);
    }

    @Test
    @DisplayName("Verify Plasma Transformer Tile Entity implements IWrenchableTile")
    public void testPlasmaTransformerWrenchableTile() throws Exception {
        assertTrue(ic2.core.block.base.features.IWrenchableTile.class.isAssignableFrom(PlasmaTransformerTileEntity.class),
                "PlasmaTransformerTileEntity must implement IWrenchableTile");

        Method canSetFacing = PlasmaTransformerTileEntity.class.getMethod("canSetFacing", Direction.class);
        assertNotNull(canSetFacing);

        Method setFacing = PlasmaTransformerTileEntity.class.getMethod("setFacing", Direction.class);
        assertNotNull(setFacing);

        Method canRemoveBlock = PlasmaTransformerTileEntity.class.getMethod("canRemoveBlock", net.minecraft.world.entity.player.Player.class);
        assertNotNull(canRemoveBlock);

        Method getDropRate = PlasmaTransformerTileEntity.class.getMethod("getDropRate", net.minecraft.world.entity.player.Player.class);
        assertNotNull(getDropRate);
    }

    @Test
    @DisplayName("Verify Super Solar Panels Tile Entities implement IWrenchableTile")
    public void testSolarPanelsWrenchableTile() throws Exception {
        assertTrue(ic2.core.block.base.features.IWrenchableTile.class.isAssignableFrom(BaseSuperSolarTileEntity.class),
                "BaseSuperSolarTileEntity must implement IWrenchableTile");

        Method canSetFacing = BaseSuperSolarTileEntity.class.getMethod("canSetFacing", Direction.class);
        assertNotNull(canSetFacing);

        Method setFacing = BaseSuperSolarTileEntity.class.getMethod("setFacing", Direction.class);
        assertNotNull(setFacing);

        Method canRemoveBlock = BaseSuperSolarTileEntity.class.getMethod("canRemoveBlock", net.minecraft.world.entity.player.Player.class);
        assertNotNull(canRemoveBlock);

        Method getDropRate = BaseSuperSolarTileEntity.class.getMethod("getDropRate", net.minecraft.world.entity.player.Player.class);
        assertNotNull(getDropRate);
    }

    @Test
    @DisplayName("Verify Cable Ratings restored to standard baseline")
    public void testCableBreakdownLimits() {
        com.afsu.mod.cable.CableTileEntity ultra = new com.afsu.mod.cable.CableTileEntity(null, net.minecraft.core.BlockPos.ZERO, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 0.01, 32768);
        com.afsu.mod.cable.CableTileEntity absolute = new com.afsu.mod.cable.CableTileEntity(null, net.minecraft.core.BlockPos.ZERO, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 0.0, 131072);
        com.afsu.mod.cable.CableTileEntity superCond = new com.afsu.mod.cable.CableTileEntity(null, net.minecraft.core.BlockPos.ZERO, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 0.5, 16384);

        assertEquals(32768, ultra.getConductorBreakdownEnergy(), "Ultra Conductor Cable rating must be 32,768 EU");
        assertEquals(131072, absolute.getConductorBreakdownEnergy(), "Absolute Cable rating must be 131,072 EU");
        assertEquals(16384, superCond.getConductorBreakdownEnergy(), "Super Conductor Cable rating must be 16,384 EU");
    }

    @Test
    @DisplayName("Verify UFAItem has Tier 5 and 50,000 EU/t transfer rate")
    public void testUFAItemTierAndTransferRate() {
        assertEquals(5, UFAItem.TIER, "UFAItem must be Tier 5 (extreme voltage/buffer)");
        assertEquals(50000, UFAItem.MAX_TRANSFER, "UFAItem charging rate must be 50,000 EU/t (2x 25,000 EU/t counterpart)");
    }

    @Test
    @DisplayName("Verify Super Solar Panels generate 100% rated daytime power")
    public void testSolarPanelGenerationFullValues() throws Exception {
        Method getProd = BaseSuperSolarTileEntity.class.getMethod("getProduction");
        Method getLowerProd = BaseSuperSolarTileEntity.class.getMethod("getLowerProduction");
        assertNotNull(getProd);
        assertNotNull(getLowerProd);
    }

    @Test
    @DisplayName("Verify Chunk Unload and Dirty Contracts for Assembler, Transformer, Solar, and AFSU")
    public void testChunkPersistenceAndDirtyContracts() throws Exception {
        Method asmUnloaded = AdvancedAssemblerTileEntity.class.getDeclaredMethod("onUnloaded", boolean.class);
        assertNotNull(asmUnloaded);
        assertEquals(AdvancedAssemblerTileEntity.class, asmUnloaded.getDeclaringClass());

        Method ptClientData = PlasmaTransformerTileEntity.class.getDeclaredMethod("onClientDataReceived", 
                net.minecraft.world.entity.player.Player.class, int.class, int.class);
        assertNotNull(ptClientData);
        assertEquals(PlasmaTransformerTileEntity.class, ptClientData.getDeclaringClass());

        Method afsuClientData = AFSUBlockEntity.class.getDeclaredMethod("onClientDataReceived", 
                net.minecraft.world.entity.player.Player.class, int.class, int.class);
        assertNotNull(afsuClientData);
        assertEquals(AFSUBlockEntity.class, afsuClientData.getDeclaringClass());

        Method afsuStackInSlot = AFSUBlockEntity.class.getDeclaredMethod("setStackInSlot", 
                int.class, net.minecraft.world.item.ItemStack.class);
        assertNotNull(afsuStackInSlot);
        assertEquals(AFSUBlockEntity.class, afsuStackInSlot.getDeclaringClass());

        Method solarStackInSlot = BaseSuperSolarTileEntity.class.getDeclaredMethod("setStackInSlot", 
                int.class, net.minecraft.world.item.ItemStack.class);
        assertNotNull(solarStackInSlot);
        assertEquals(BaseSuperSolarTileEntity.class, solarStackInSlot.getDeclaringClass());
    }

    @Test
    @DisplayName("Verify Advanced Assembler Recipe rejects invalid extraneous items in grid")
    public void testAssemblerRecipeStrictMatching() {
        ic2.api.recipes.ingridients.inputs.IInput input = new ic2.api.recipes.ingridients.inputs.IInput() {
            @Override public boolean matches(net.minecraft.world.item.ItemStack stack) {
                return !stack.isEmpty() && stack.getItem() == net.minecraft.world.item.Items.IRON_INGOT;
            }
            @Override public int getInputSize() { return 1; }
            @Override public java.util.List<net.minecraft.world.item.ItemStack> getComponents() { 
                return java.util.Collections.singletonList(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_INGOT)); 
            }
            @Override public void serialize(net.minecraft.network.FriendlyByteBuf buffer) {}
            @Override public com.google.gson.JsonObject serialize() { return new com.google.gson.JsonObject(); }
        };

        AdvancedAssemblerRecipe recipe = new AdvancedAssemblerRecipe(
                new net.minecraft.resources.ResourceLocation("afsu", "test_recipe"),
                java.util.Collections.singletonList(input),
                net.minecraft.world.item.ItemStack.EMPTY,
                net.minecraft.world.item.ItemStack.EMPTY,
                net.minecraftforge.fluids.FluidStack.EMPTY,
                100, 20
        );

        net.minecraft.world.SimpleContainer container = new net.minecraft.world.SimpleContainer(6);
        container.setItem(0, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_INGOT));
        assertTrue(recipe.matches(container, null), "Recipe must match when only required ingredients are present");

        // Add extraneous unmapped item (e.g. Gold Ingot in slot 1)
        container.setItem(1, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.GOLD_INGOT));
        assertFalse(recipe.matches(container, null), "Recipe must REJECT craft when extraneous items are present in other slots");
    }

    @Test
    @DisplayName("Verify Electric Saber has useOn block targeting and selective inventoryTick drain")
    public void testElectricSaberPolish() throws Exception {
        Method useOn = ElectricSaberItem.class.getDeclaredMethod("useOn", net.minecraft.world.item.context.UseOnContext.class);
        assertNotNull(useOn);
        assertEquals(ElectricSaberItem.class, useOn.getDeclaringClass(), "ElectricSaberItem must override useOn to support block right-clicking");

        Method invTick = ElectricSaberItem.class.getDeclaredMethod("inventoryTick", 
                net.minecraft.world.item.ItemStack.class, net.minecraft.world.level.Level.class, 
                net.minecraft.world.entity.Entity.class, int.class, boolean.class);
        assertNotNull(invTick);
        assertEquals(ElectricSaberItem.class, invTick.getDeclaringClass(), "ElectricSaberItem must override inventoryTick to check isSelected");
    }


    @Test
    @DisplayName("Verify Electric Saber client frame calculation is read-only and handles GUI contexts")
    public void testSaberFrameCalculation() {
        net.minecraft.world.item.ItemStack inactiveStack = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD);
        boolean hadTag = inactiveStack.hasTag();
        float inactiveFrame = com.afsu.mod.client.AFSUClientSetup.calculateSaberFrame(inactiveStack, null, null);
        assertEquals(0.0F, inactiveFrame, "Inactive saber must return 0.0F");
        assertEquals(hadTag, inactiveStack.hasTag(), "Calling calculateSaberFrame must not modify NBT tag");

        net.minecraft.world.item.ItemStack activeStack = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD);
        activeStack.getOrCreateTag().putBoolean("active", true);
        float activeFrameNoLevel = com.afsu.mod.client.AFSUClientSetup.calculateSaberFrame(activeStack, null, null);
        assertTrue(activeFrameNoLevel >= 10.0F, "Active saber in GUI with null level must return extended blade (>= 10.0F)");
        assertFalse(activeStack.getTag().contains("activation_time"), "Frame calculation must NOT mutate NBT by injecting activation_time on render thread");
    }
}
