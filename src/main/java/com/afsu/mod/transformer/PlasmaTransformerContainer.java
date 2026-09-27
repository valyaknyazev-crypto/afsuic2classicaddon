package com.afsu.mod.transformer;

import ic2.core.inventory.base.IHasGui;
import ic2.core.inventory.container.ContainerComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PlasmaTransformerContainer extends ContainerComponent<PlasmaTransformerTileEntity> {
    
    // We will use the exact same GUI background as the Adjustable Transformer
    public static final ResourceLocation TEXTURE = new ResourceLocation("ic2", "textures/gui_sprites/blocks/storage/gui_adjustable_transformer.png");

    public PlasmaTransformerContainer(PlasmaTransformerTileEntity key, Player player, int id) {
        super(key, player, id);
        this.addPlayerInventory(player.getInventory());
        this.addComponent(new PlasmaTransformerComponent(key));
    }

    @Override
    public ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    public int getInventorySize() {
        return 0;
    }
}
