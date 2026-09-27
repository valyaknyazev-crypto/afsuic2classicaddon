package com.afsu.mod.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;

public class BugTrackerItem extends Item {

    public BugTrackerItem(Properties properties) {
        super(properties);
    }

    // Toggle global tick profiling mode
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            boolean isNowLogging = DebugLogger.toggleLogging();
            if (isNowLogging) {
                player.sendSystemMessage(Component.literal("AFSU Debug Tracker: TICK PROFILER ACTIVATED (Logs straight to disk)."));
            } else {
                player.sendSystemMessage(Component.literal("AFSU Debug Tracker: TICK PROFILER DEACTIVATED. Log saved."));
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    // Standard Mod Debugger behavior: Click a machine to see its Diagnostics / NBT
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (!level.isClientSide && player != null) {
            BlockEntity be = level.getBlockEntity(context.getClickedPos());
            if (be instanceof IDebuggableMachine) {
                player.sendSystemMessage(Component.literal("=== Анализ Механизма ===").withStyle(net.minecraft.ChatFormatting.YELLOW));
                java.util.List<Component> diagnostics = ((IDebuggableMachine) be).getDiagnostics();
                if (diagnostics != null) {
                    for (Component line : diagnostics) {
                        if (line != null) {
                            player.sendSystemMessage(line);
                        }
                    }
                }
                player.sendSystemMessage(Component.literal("========================").withStyle(net.minecraft.ChatFormatting.YELLOW));
            }
            if (be != null) {
                net.minecraft.nbt.CompoundTag tag = be.saveWithFullMetadata();
                String tagStr = tag.toString();
                if (tagStr.length() > 1000) {
                    tagStr = tagStr.substring(0, 1000) + "... [TRUNCATED]";
                }
                player.sendSystemMessage(Component.literal("TileEntity Data: " + tagStr));
            } else {
                player.sendSystemMessage(Component.literal("No TileEntity at " + context.getClickedPos().toShortString()));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
