package com.afsu.mod.compat.jade;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import net.minecraft.world.level.block.Block;

@WailaPlugin
public class AFSUJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(AssemblerProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(TransformerProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(SolarProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(CableProvider.INSTANCE, Block.class);
    }
}
