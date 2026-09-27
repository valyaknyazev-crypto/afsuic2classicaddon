package com.afsu.mod.registry;
import com.afsu.mod.AFSUMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AFSUMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AFSUMod.MODID);
    public static final RegistryObject<MenuType<com.afsu.mod.assembler.AdvancedAssemblerContainer>> ADVANCED_ASSEMBLER_MENU = MENU_TYPES.register("advanced_assembler", () -> net.minecraftforge.common.extensions.IForgeMenuType.create((windowId, inv, data) -> new com.afsu.mod.assembler.AdvancedAssemblerContainer(windowId, inv, data)));
}
