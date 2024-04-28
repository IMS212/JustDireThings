package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.client.renderers.JustDireItemRenderer;
import com.direwolf20.justdirethings.common.entities.CreatureCatcherEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.List;
import java.util.function.Consumer;

public class CreatureCatcher extends Item {
    public CreatureCatcher() {
        super(new Properties()
                .stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        pLevel.playSound(
                null,
                pPlayer.getX(),
                pPlayer.getY(),
                pPlayer.getZ(),
                SoundEvents.SNOWBALL_THROW,
                SoundSource.NEUTRAL,
                0.5F,
                0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!pLevel.isClientSide) {
            CreatureCatcherEntity creatureCatcherEntity = new CreatureCatcherEntity(pLevel, pPlayer);
            creatureCatcherEntity.setItem(itemStack);
            creatureCatcherEntity.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
            pLevel.addFreshEntity(creatureCatcherEntity);
        }

        if (!pPlayer.getAbilities().instabuild) {
            itemStack.shrink(1);
        }


        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }

    public static boolean hasEntity(ItemStack itemStack) {
        return !itemStack.getOrCreateTag().isEmpty();  //Any tag will be an entity
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        Minecraft mc = Minecraft.getInstance();
        if (level == null || mc.player == null) {
            return;
        }
        Mob mob = CreatureCatcherEntity.getEntityFromItemStack(stack, level);
        if (mob == null) return;

        tooltip.add(Component.translatable("justdirethings.creature")
                .withStyle(ChatFormatting.DARK_GRAY)
                .append(Component.literal("")
                        .append(mob.getName())
                        .withStyle(ChatFormatting.GREEN)));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            JustDireItemRenderer diremodel = new JustDireItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return diremodel;
            }
        });
    }
}