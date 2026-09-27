package com.afsu.mod.assembler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import ic2.api.recipes.ingridients.inputs.IInput;
import java.util.List;

public class AdvancedAssemblerRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final List<IInput> inputs;
    private final ItemStack result;
    private final net.minecraftforge.fluids.FluidStack fluidRequirement;
    private final int euCost;
    private final int duration;
    private final net.minecraft.core.NonNullList<net.minecraft.world.item.crafting.Ingredient> ingredients;
    
    public AdvancedAssemblerRecipe(ResourceLocation id, List<IInput> inputs, ItemStack result, net.minecraftforge.fluids.FluidStack fluidRequirement, int euCost, int duration) {
        this.id = id;
        this.inputs = inputs;
        this.result = result;
        this.fluidRequirement = fluidRequirement;
        this.euCost = euCost;
        this.duration = duration;
        this.ingredients = net.minecraft.core.NonNullList.create();
        for (IInput in : inputs) {
            this.ingredients.add(net.minecraft.world.item.crafting.Ingredient.of(in.getComponents().stream().map(net.minecraft.world.item.ItemStack::copy).toArray(net.minecraft.world.item.ItemStack[]::new)));
        }
    }

    @Override
    public net.minecraft.core.NonNullList<net.minecraft.world.item.crafting.Ingredient> getIngredients() {
        return this.ingredients;
    }

    public net.minecraftforge.fluids.FluidStack getFluidRequirement() {
        return fluidRequirement;
    }

    public int getEuCost() {
        return euCost;
    }

    public int getDuration() {
        return duration;
    }

    public List<IInput> getInputs() {
        return inputs;
    }
    
    @Override
    public boolean matches(Container inv, Level level) {
        int[] invCounts = new int[6];
        ItemStack[] invStacks = new ItemStack[6];
        for (int i = 0; i < 6; i++) {
            invStacks[i] = inv.getItem(i);
            invCounts[i] = invStacks[i].getCount();
        }

        for (IInput ing : inputs) {
            int needed = ing.getInputSize();
            for (int j = 0; j < 6; j++) {
                if (invCounts[j] > 0 && ing.matches(invStacks[j])) {
                    int consume = Math.min(needed, invCounts[j]);
                    invCounts[j] -= consume;
                    needed -= consume;
                    if (needed <= 0) break;
                }
            }
            if (needed > 0) {
                return false;
            }
        }
        
        // Ensure no extra invalid items exist that don't belong to the recipe
        // Wait, the spec says shapeless input grid, does it allow garbage in other slots?
        // IC2 allows garbage in other slots generally, but let's just return true if all ingredients are satisfied.
        return true; 
    }
    
    @Override
    public ItemStack assemble(Container inv) {
        return result.copy();
    }
    
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }
    
    @Override
    public ItemStack getResultItem() {
        return result;
    }
    
    @Override
    public ResourceLocation getId() {
        return id;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return AdvancedAssemblerRecipeSerializer.INSTANCE;
    }
    
    @Override
    public RecipeType<?> getType() {
        return AdvancedAssemblerRecipeType.INSTANCE;
    }
}
