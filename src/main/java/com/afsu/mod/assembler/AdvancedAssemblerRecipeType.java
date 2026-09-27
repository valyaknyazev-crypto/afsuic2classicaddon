package com.afsu.mod.assembler;

import net.minecraft.world.item.crafting.RecipeType;

public class AdvancedAssemblerRecipeType implements RecipeType<AdvancedAssemblerRecipe> {
    public static final AdvancedAssemblerRecipeType INSTANCE = new AdvancedAssemblerRecipeType();
    public static final String ID = "advanced_assembler";
    
    private AdvancedAssemblerRecipeType() {
    }
    
    @Override
    public String toString() {
        return ID;
    }
}
