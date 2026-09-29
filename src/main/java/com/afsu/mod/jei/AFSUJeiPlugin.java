package com.afsu.mod.jei;

import com.afsu.mod.AFSUMod;
import com.afsu.mod.assembler.AdvancedAssemblerRecipe;
import com.afsu.mod.assembler.AdvancedAssemblerContainer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class AFSUJeiPlugin implements IModPlugin {

    private static final ResourceLocation ID = new ResourceLocation(AFSUMod.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
            new AdvancedAssemblerRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        
        List<AdvancedAssemblerRecipe> recipes = recipeManager.getAllRecipesFor((net.minecraft.world.item.crafting.RecipeType<AdvancedAssemblerRecipe>) com.afsu.mod.registry.AFSURecipes.ADVANCED_ASSEMBLER_TYPE.get());
        registration.addRecipes(AdvancedAssemblerRecipeCategory.TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(com.afsu.mod.registry.AFSUBlocks.ADVANCED_ASSEMBLER.get()), AdvancedAssemblerRecipeCategory.TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(AdvancedAssemblerContainer.class, com.afsu.mod.registry.AFSUMenus.ADVANCED_ASSEMBLER_MENU.get(), AdvancedAssemblerRecipeCategory.TYPE, 2, 6, 16, 36);
    }
}

