package com.afsu.mod.jei;

import com.afsu.mod.AFSUMod;
import com.afsu.mod.assembler.AdvancedAssemblerRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class AdvancedAssemblerRecipeCategory implements IRecipeCategory<AdvancedAssemblerRecipe> {

    public static final RecipeType<AdvancedAssemblerRecipe> TYPE = RecipeType.create(AFSUMod.MODID, "advanced_assembler", AdvancedAssemblerRecipe.class);
    
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated chargeBar;
    private final IDrawableStatic tankOverlay;

    public AdvancedAssemblerRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation guiTexture = new ResourceLocation(AFSUMod.MODID, "textures/gui/advanced_assembler_jeigui.png");
        
        // Crop the JEI GUI texture precisely to avoid showing window borders
        // Crop: X=22, Y=11, Width=130, Height=65
        this.background = guiHelper.createDrawable(guiTexture, 22, 11, 130, 65);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(com.afsu.mod.registry.AFSUBlocks.ADVANCED_ASSEMBLER.get()));
        
        IDrawableStatic arrowStatic = guiHelper.createDrawable(guiTexture, 176, 14, 24, 17);
        this.arrow = guiHelper.createAnimatedDrawable(arrowStatic, 100, IDrawableAnimated.StartDirection.LEFT, false);
        
        // Full charge bar from 176, 0
        IDrawableStatic chargeBarStatic = guiHelper.createDrawable(guiTexture, 176, 0, 14, 14);
        this.chargeBar = guiHelper.createAnimatedDrawable(chargeBarStatic, 100, IDrawableAnimated.StartDirection.TOP, true);
        
        this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 31, 16, 58);
    }

    @Override
    public RecipeType<AdvancedAssemblerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.afsu.advanced_assembler");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AdvancedAssemblerRecipe recipe, IFocusGroup focuses) {
        // Inputs (6 slots, 2 cols x 3 rows)
        for (int i = 0; i < 6; i++) {
            var slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, 27 + (i % 2) * 20, 4 + (i / 2) * 20);
            if (i < recipe.getInputs().size()) {
                ic2.api.recipes.ingridients.inputs.IInput input = recipe.getInputs().get(i);
                List<ItemStack> scaled = new ArrayList<>();
                for (ItemStack stack : input.getComponents()) {
                    ItemStack copy = stack.copy();
                    copy.setCount(input.getInputSize());
                    scaled.add(copy);
                }
                slotBuilder.addIngredients(VanillaTypes.ITEM_STACK, scaled);
            }
        }
        
        // Fluid Input (Left Tank) at relative X: 8, Y: 5
        FluidStack fluid = recipe.getFluidRequirement();
        if (fluid != null && !fluid.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 6, 3)
                   .addIngredients(ForgeTypes.FLUID_STACK, List.of(fluid))
                   .setFluidRenderer(16000, false, 16, 58)
                   .setOverlay(tankOverlay, 0, 0);
                   
            // Hardcode fluid byproduct logic for JEI display in the Right Tank (Relative X: 113, Y: 5)
            net.minecraft.world.level.material.Fluid byproduct = null;
            if (fluid.getFluid() == com.afsu.mod.registry.AFSUFluids.SULFURIC_ACID.get()) {
                byproduct = com.afsu.mod.registry.AFSUFluids.WASTE_ACID.get();
            } else if (fluid.getFluid() == com.afsu.mod.registry.AFSUFluids.HYDROGEN_PEROXIDE.get()) {
                byproduct = com.afsu.mod.registry.AFSUFluids.TOXIC_SLUDGE.get();
            }
            
            if (byproduct != null) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 111, 3)
                       .addIngredients(ForgeTypes.FLUID_STACK, List.of(new FluidStack(byproduct, fluid.getAmount())))
                       .setFluidRenderer(16000, false, 16, 58)
                       .setOverlay(tankOverlay, 0, 0);
            }
        }
        
        // Outputs (2 slots, 1 col x 2 rows)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 15)
               .addItemStack(recipe.getResultItem());
               
        if (recipe.getSecondaryResult() != null && !recipe.getSecondaryResult().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 35)
                   .addItemStack(recipe.getSecondaryResult());
        } else {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 35);
        }
    }

    @Override
    public void draw(AdvancedAssemblerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        // Tank overlays for empty tanks
        FluidStack fluid = recipe.getFluidRequirement();
        if (fluid == null || fluid.isEmpty()) {
            tankOverlay.draw(stack, 6, 3);
        }
        
        // Charge bar
        chargeBar.draw(stack, 68, 46);
        
        // Arrow
        arrow.draw(stack, 65, 25);
    }

    @Override
    public List<Component> getTooltipStrings(AdvancedAssemblerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltips = new ArrayList<>();
        
        // Charge bar bounds
        if (mouseX >= 68 && mouseX < 82 && mouseY >= 46 && mouseY < 60) {
            tooltips.add(Component.translatable("jei.afsu.recipe.cost", recipe.getEuCost()));
        }
        
        // Left Tank bounds
        FluidStack fluid = recipe.getFluidRequirement();
        if ((fluid == null || fluid.isEmpty()) && mouseX >= 7 && mouseX < 23 && mouseY >= 4 && mouseY < 62) {
            tooltips.add(Component.translatable("jei.afsu.recipe.no_fluid"));
        }
        return tooltips;
    }
}
