package com.afsu.mod.client;

import com.afsu.mod.AFSUMod;
import com.afsu.mod.registry.AFSUItems;
import com.afsu.mod.tools.ElectricSaberItem;
import ic2.api.items.electric.ElectricItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
                (stack, level, entity, seed) -> calculateSaberFrame(stack, level, entity));

            ItemProperties.register(AFSUItems.QUANTUM_SABER.get(), new ResourceLocation(AFSUMod.MODID, "active_frame"),
                (stack, level, entity, seed) -> calculateSaberFrame(stack, level, entity));
        });
    }

    public static float calculateSaberFrame(ItemStack stack, ClientLevel level, LivingEntity entity) {
        if (!ElectricSaberItem.isActive(stack)) return 0.0F;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel clientLevel = level != null ? level : (entity != null && entity.level instanceof ClientLevel cl ? cl : (mc != null ? mc.level : null));
        if (clientLevel == null) return 10.0F;

        // In hand: play blade opening animation if newly turned on
        if (entity != null) {
            long activationTime = stack.getTag().getLong("activation_time");
            if (activationTime > 0) {
                long diff = clientLevel.getGameTime() - activationTime;
                if (diff >= 0 && diff < 27) {
                    return (float) (diff / 3) + 1.0F;
                }
            }
        }

        // In inventory/GUI (or in hand once fully extended): smoothly pulsate between active frames 9 and 10 (overrides 10.0 and 11.0)
        // Pulsate every 2 ticks (~100 ms)
        int pulse = (int) ((clientLevel.getGameTime() / 2) % 2);
        return 10.0F + pulse;
    }
}
