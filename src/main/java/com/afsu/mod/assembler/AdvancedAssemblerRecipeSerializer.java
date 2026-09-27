package com.afsu.mod.assembler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import ic2.api.recipes.ingridients.inputs.IInput;
import ic2.api.recipes.ingridients.inputs.IngredientInput;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class AdvancedAssemblerRecipeSerializer implements RecipeSerializer<AdvancedAssemblerRecipe> {
    public static final AdvancedAssemblerRecipeSerializer INSTANCE = new AdvancedAssemblerRecipeSerializer();
    public static final String ID = "advanced_assembler";

    @Override
    public AdvancedAssemblerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
        JsonArray ingredients = pSerializedRecipe.getAsJsonArray("inputs");
        List<IInput> inputs = new ArrayList<>();

        for (int i = 0; i < ingredients.size(); i++) {
            inputs.add(new IngredientInput(ingredients.get(i).getAsJsonObject()));
        }

        ItemStack output = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("result"));

        net.minecraftforge.fluids.FluidStack fluid = net.minecraftforge.fluids.FluidStack.EMPTY;
        if (pSerializedRecipe.has("fluid")) {
            JsonObject fluidObj = pSerializedRecipe.getAsJsonObject("fluid");
            String fluidName = fluidObj.get("fluid").getAsString();
            int amount = fluidObj.has("amount") ? fluidObj.get("amount").getAsInt() : 1000;
            net.minecraft.world.level.material.Fluid f = net.minecraftforge.registries.ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
            if (f != null) {
                fluid = new net.minecraftforge.fluids.FluidStack(f, amount);
            }
        }
        
        int euCost = pSerializedRecipe.has("eu_cost") ? pSerializedRecipe.get("eu_cost").getAsInt() : 20;
        int duration = pSerializedRecipe.has("duration") ? pSerializedRecipe.get("duration").getAsInt() : 100;

        return new AdvancedAssemblerRecipe(pRecipeId, inputs, output, fluid, euCost, duration);
    }

    @Override
    public @Nullable AdvancedAssemblerRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        int i = pBuffer.readVarInt();
        List<IInput> inputs = new ArrayList<>();

        for (int j = 0; j < i; j++) {
            inputs.add(new IngredientInput(pBuffer));
        }

        ItemStack output = pBuffer.readItem();
        net.minecraftforge.fluids.FluidStack fluid = net.minecraftforge.fluids.FluidStack.readFromPacket(pBuffer);
        int euCost = pBuffer.readVarInt();
        int duration = pBuffer.readVarInt();
        return new AdvancedAssemblerRecipe(pRecipeId, inputs, output, fluid, euCost, duration);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, AdvancedAssemblerRecipe pRecipe) {
        pBuffer.writeVarInt(pRecipe.getInputs().size());
        for (IInput ing : pRecipe.getInputs()) {
            ing.serialize(pBuffer);
        }
        pBuffer.writeItem(pRecipe.getResultItem());
        pRecipe.getFluidRequirement().writeToPacket(pBuffer);
        pBuffer.writeVarInt(pRecipe.getEuCost());
        pBuffer.writeVarInt(pRecipe.getDuration());
    }
}
