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
        ResourceLocation guiTexture = new ResourceLocation(AFSUMod.MODID, "textures/gui/advanced_assembler.png");
        
        // Crop the actual machine GUI texture! X: 24->154, Y: 11->76 (Width: 130, Height: 65)
        this.background = guiHelper.createDrawable(guiTexture, 24, 11, 127, 65);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(AFSUMod.ADVANCED_ASSEMBLER.get()));
        
        IDrawableStatic arrowStatic = guiHelper.createDrawable(guiTexture, 176, 15, 24, 16);
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
        // In GUI: item starts at 49, 15. Relative to 24, 11 -> 25, 4
        for (int i = 0; i < 6; i++) {
            var slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, 25 + (i % 2) * 20, 4 + (i / 2) * 20);
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
        
        // Fluid
        // In GUI: item starts at 29, 15. Relative to 24, 11 -> 5, 4
        FluidStack fluid = recipe.getFluidRequirement();
        if (fluid != null && !fluid.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 5, 4) // +1px border
                   .addIngredients(ForgeTypes.FLUID_STACK, List.of(fluid))
                   .setFluidRenderer(16000, false, 14, 56)
                   .setOverlay(tankOverlay, -1, -1);
        }
        
        // Outputs (4 slots, 2 cols x 2 rows)
        // In GUI: item starts at 113, 26. Relative to 24, 11 -> 89, 15
        builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 15)
               .addItemStack(recipe.getResultItem());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 15);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 35);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 35);
    }

    @Override
    public void draw(AdvancedAssemblerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        // Tank is at 4, 3
        FluidStack fluid = recipe.getFluidRequirement();
        if (fluid == null || fluid.isEmpty()) {
            tankOverlay.draw(stack, 4, 3);
        }
        
        // Charge bar is at 66, 46 (90, 57 relative to 24, 11)
        chargeBar.draw(stack, 66, 46);
        
        // Arrow is at 64, 24 (88, 35 relative to 24, 11)
        arrow.draw(stack, 64, 24);
    }

    @Override
    public List<Component> getTooltipStrings(AdvancedAssemblerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltips = new ArrayList<>();
        
        // Charge bar bounds (X=66, Y=46, W=14, H=14)
        if (mouseX >= 66 && mouseX < 80 && mouseY >= 46 && mouseY < 60) {
            tooltips.add(Component.translatable("jei.afsu.recipe.cost", recipe.getEuCost()));
        }
        
        // Tank bounds (X=4, Y=3, W=16, H=58)
        FluidStack fluid = recipe.getFluidRequirement();
        if ((fluid == null || fluid.isEmpty()) && mouseX >= 4 && mouseX < 20 && mouseY >= 3 && mouseY < 61) {
            tooltips.add(Component.translatable("jei.afsu.recipe.no_fluid"));
        }
        return tooltips;
    }
}
