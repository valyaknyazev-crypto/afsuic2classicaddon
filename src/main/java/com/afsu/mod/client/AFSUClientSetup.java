package com.afsu.mod.client;

import com.afsu.mod.AFSUMod;
import com.afsu.mod.registry.AFSUItems;
import ic2.api.items.electric.ElectricItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class AFSUClientSetup {

    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(AFSUItems.UFA_ITEM.get(), new ResourceLocation(AFSUMod.MODID, "charge"),
                (stack, level, entity, seed) -> {
                    int capacity = ElectricItem.MANAGER.getCapacity(stack);
                    int charge = ElectricItem.MANAGER.getCharge(stack);
                    if (capacity == 0) return 0.0f;
                    float ratio = (float) charge / capacity;
                    if (ratio > 0.875f) return 1.0f;
                    if (ratio > 0.625f) return 0.75f;
                    if (ratio > 0.375f) return 0.50f;
                    if (ratio > 0.125f) return 0.25f;
                    return 0.0f;
                });

            ItemProperties.register(AFSUItems.ADVANCED_SABER.get(), new ResourceLocation(AFSUMod.MODID, "active_frame"),
                (stack, level, entity, seed) -> calculateSaberFrame(stack, level));

            ItemProperties.register(AFSUItems.QUANTUM_SABER.get(), new ResourceLocation(AFSUMod.MODID, "active_frame"),
                (stack, level, entity, seed) -> calculateSaberFrame(stack, level));
        });
    }

    private static float calculateSaberFrame(net.minecraft.world.item.ItemStack stack, net.minecraft.client.multiplayer.ClientLevel level) {
        if (!stack.getOrCreateTag().getBoolean("active")) return 0.0F;

        long activationTime = stack.getOrCreateTag().getLong("activation_time");
        if (activationTime == 0 && level != null) {
            activationTime = level.getGameTime();
            stack.getOrCreateTag().putLong("activation_time", activationTime);
        }

        if (level == null) return 0.0F;

        long diff = level.getGameTime() - activationTime;
        int frame = 0;

        if (diff < 27) {
            frame = (int) (diff / 3);
        } else {
            frame = 9 + (int) ((diff - 27) % 2);
        }

        return frame + 1.0F;
    }
}
