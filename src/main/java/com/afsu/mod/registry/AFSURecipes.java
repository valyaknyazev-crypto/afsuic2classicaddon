package com.afsu.mod.registry;
import com.afsu.mod.AFSUMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.core.Registry;

public class AFSURecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AFSUMod.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, AFSUMod.MODID);

    public static final RegistryObject<RecipeSerializer<?>> ADVANCED_ASSEMBLER_SERIALIZER = RECIPE_SERIALIZERS.register("advanced_assembler", () -> com.afsu.mod.assembler.AdvancedAssemblerRecipeSerializer.INSTANCE);
    public static final RegistryObject<RecipeType<?>> ADVANCED_ASSEMBLER_TYPE = RECIPE_TYPES.register("advanced_assembler", () -> com.afsu.mod.assembler.AdvancedAssemblerRecipeType.INSTANCE);
}
