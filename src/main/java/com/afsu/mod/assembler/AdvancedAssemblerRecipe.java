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
    private final ItemStack secondaryResult;
    private final net.minecraftforge.fluids.FluidStack fluidRequirement;
    private final int euCost;
    private final int duration;
    private final net.minecraft.core.NonNullList<net.minecraft.world.item.crafting.Ingredient> ingredients;
    
    public AdvancedAssemblerRecipe(ResourceLocation id, List<IInput> inputs, ItemStack result, ItemStack secondaryResult, net.minecraftforge.fluids.FluidStack fluidRequirement, int euCost, int duration) {
        this.id = id;
        this.inputs = inputs;
        this.result = result;
        this.secondaryResult = secondaryResult;
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
        ItemStack[] invStacks = new ItemStack[6];
        int[] remainingCounts = new int[6];
        for (int i = 0; i < 6; i++) {
            invStacks[i] = inv.getItem(i);
            remainingCounts[i] = invStacks[i].getCount();
        }

        // Reject recipe if extraneous unmapped items exist in any input slot
        for (int j = 0; j < 6; j++) {
            if (invStacks[j].isEmpty()) continue;
            boolean matchesAny = false;
            for (IInput ing : inputs) {
                if (ing.matches(invStacks[j])) {
                    matchesAny = true;
                    break;
                }
            }
            if (!matchesAny) {
                return false;
            }
        }

        // Ensure all required ingredient quantities are fulfilled across slots
        for (IInput ing : inputs) {
            int needed = ing.getInputSize();
            for (int j = 0; j < 6; j++) {
                if (remainingCounts[j] > 0 && ing.matches(invStacks[j])) {
                    int consume = Math.min(needed, remainingCounts[j]);
                    remainingCounts[j] -= consume;
                    needed -= consume;
                    if (needed <= 0) break;
                }
            }
            if (needed > 0) {
                return false;
            }
        }

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
    
    public ItemStack getSecondaryResult() {
        return secondaryResult;
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
