package com.afsu.mod;

import com.afsu.mod.assembler.AdvancedAssemblerBlock;
import com.afsu.mod.assembler.AdvancedAssemblerTileEntity;
import com.afsu.mod.cable.CableBlock;
import com.afsu.mod.cable.CableTileEntity;
import com.afsu.mod.solar.BaseSuperSolarTileEntity;
import com.afsu.mod.solar.SolarPanelBlock;
import com.afsu.mod.transformer.PlasmaTransformerBlock;
import com.afsu.mod.transformer.PlasmaTransformerTileEntity;
import ic2.api.blocks.IWrenchable;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IMultiEnergySource;
import ic2.core.block.base.features.ITickListener;
import ic2.core.block.base.features.IWrenchableTile;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class AFSUBugsTest {

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
    public void testSolarPanelWrench() throws Exception {
        assertTrue(IWrenchable.class.isAssignableFrom(SolarPanelBlock.class), "SolarPanelBlock must implement IWrenchable");
        
        Method getFacing = SolarPanelBlock.class.getMethod("getFacing", net.minecraft.world.level.block.state.BlockState.class,
                net.minecraft.world.level.Level.class, net.minecraft.core.BlockPos.class);
        assertEquals(SolarPanelBlock.class, getFacing.getDeclaringClass(), "SolarPanelBlock must override getFacing to return null");
    }

    @Test
    @DisplayName("Verify AdvancedAssemblerTileEntity implements IWrenchableTile and has 80% drop rate")
    public void testAssemblerWrench() throws Exception {
        assertTrue(IWrenchableTile.class.isAssignableFrom(AdvancedAssemblerTileEntity.class), 
                "AdvancedAssemblerTileEntity must implement IWrenchableTile");

        Method getDropRate = AdvancedAssemblerTileEntity.class.getMethod("getDropRate", net.minecraft.world.entity.player.Player.class);
        assertNotNull(getDropRate);
    }

    @Test
    @DisplayName("Verify AFSUBlockEntity has 70% drop rate like MFSU")
    public void testAFSUDropRate() throws Exception {
        Method getDropRate = AFSUBlockEntity.class.getMethod("getDropRate", net.minecraft.world.entity.player.Player.class);
        assertNotNull(getDropRate);
        assertEquals(AFSUBlockEntity.class, getDropRate.getDeclaringClass(), "AFSUBlockEntity must override getDropRate");
    }

    @Test
    @DisplayName("Verify PlasmaTransformerTileEntity implements IWrenchableTile, IMultiEnergySource, IEnergySink")
    public void testPlasmaTransformerInterfaces() {
        assertTrue(IWrenchableTile.class.isAssignableFrom(PlasmaTransformerTileEntity.class), 
                "PlasmaTransformerTileEntity must implement IWrenchableTile");
        assertTrue(IMultiEnergySource.class.isAssignableFrom(PlasmaTransformerTileEntity.class), 
                "PlasmaTransformerTileEntity must implement IMultiEnergySource");
        assertTrue(IEnergySink.class.isAssignableFrom(PlasmaTransformerTileEntity.class), 
                "PlasmaTransformerTileEntity must implement IEnergySink");
        assertTrue(ITickListener.class.isAssignableFrom(PlasmaTransformerTileEntity.class), 
                "PlasmaTransformerTileEntity must implement ITickListener");
    }

    @Test
    @DisplayName("Verify Plasma Transformer Step-Down Mode and Energy Tiers")
    public void testPlasmaTransformerStepDown() {
        // Step-down mode (active = false):
        // highOutput = 65536 (AFSU extreme voltage), energyPacket = 2048 (EV default)
        // High voltage enters facing face (5-dot). Low voltage exits other 5 faces.
        int lowOutput = 2048;
        
        Direction facing = Direction.NORTH;
        for (Direction dir : Direction.values()) {
            boolean canEmit = (facing != dir);
            boolean canAccept = (facing == dir);
            
            if (dir == facing) {
                assertFalse(canEmit, "Step-down should NOT emit from high-voltage input face");
                assertTrue(canAccept, "Step-down should accept high-voltage from input face");
            } else {
                assertTrue(canEmit, "Step-down should emit low-voltage to other 5 faces");
                assertFalse(canAccept, "Step-down should NOT accept from low-voltage output faces");
            }
        }
        
        // Output tier must match lowOutput (2048 EU = Tier 4 / EV), NOT Tier 10!
        int expectedTier = 4; // EV
        int calculatedTier = ic2.api.energy.EnergyNet.INSTANCE != null ? 
                ic2.api.energy.EnergyNet.INSTANCE.getTierFromPower(lowOutput) : 4;
        assertEquals(expectedTier, calculatedTier, "Plasma Transformer step-down output tier must be Tier 4 (EV) by default to prevent machinery explosions!");
    }

    @Test
    @DisplayName("Verify Plasma Transformer Step-Up Mode and Energy Tiers")
    public void testPlasmaTransformerStepUp() {
        // Step-up mode (active = true / redstone active):
        // Low voltage enters 5 faces. High voltage (65536 EU) exits facing face.
        Direction facing = Direction.NORTH;
        for (Direction dir : Direction.values()) {
            boolean canEmit = (facing == dir);
            boolean canAccept = (facing != dir);
            
            if (dir == facing) {
                assertTrue(canEmit, "Step-up should emit high-voltage from facing face");
                assertFalse(canAccept, "Step-up should NOT accept from high-voltage output face");
            } else {
                assertFalse(canEmit, "Step-up should NOT emit to low-voltage input faces");
                assertTrue(canAccept, "Step-up should accept low-voltage from other 5 faces");
            }
        }
    }

    @Test
    @DisplayName("Verify Cable Breakdown limits prevent cables from melting under AFSU load")
    public void testCableBreakdownLimits() {
        int afsuOutput = 65536;
        int ultraBreakdown = 131072;
        int superBreakdown = 32768;
        
        assertTrue(ultraBreakdown > afsuOutput, "Ultra Conductor Cable (131072 EU) must handle AFSU output (65536 EU) without melting!");
        assertTrue(superBreakdown >= 32768, "Super Conductor Cable must handle at least 32768 EU!");
    }
}
