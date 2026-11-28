package com.watonstudio.creepermetalico;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import org.apache.commons.lang3.mutable.MutableObject;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.SingletonGeoAnimatable;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class AldeanoPhoraItem extends Item implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   public final MutableObject<Object> renderProviderHolder = new MutableObject();

   public AldeanoPhoraItem(Properties settings) {
      super(settings);
      SingletonGeoAnimatable.registerSyncedAnimatable(this);
   }

   public Component getDescription() {
      return Component.literal("☠ Aldeano Phora de Recursos ☠").withStyle(ChatFormatting.GREEN);
   }

   public Component getName(ItemStack stack) {
      return Component.literal("☠ Aldeano Phora de Recursos ☠").withStyle(ChatFormatting.GREEN);
   }

   public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, java.util.function.Consumer<Component> tooltipAdder$, TooltipFlag type) {
      java.util.List<Component> tooltip = new java.util.ArrayList<>();
      this.appendHoverText$viejo(stack, context, tooltipDisplay$, tooltip, type);
      tooltip.forEach(tooltipAdder$);
   }

   private void appendHoverText$viejo(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, List<Component> tooltip, TooltipFlag type) {
      tooltip.add(Component.empty());
      tooltip.add(Component.literal("Trabajador Phora, llegado en").withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.literal("los Cubos. Utilízalo para realizar").withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.literal("diferentes intercambios.").withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.empty());
      tooltip.add(Component.literal("Requiere una ").withStyle(ChatFormatting.GRAY).append(Component.literal("Llave de Cobre").withStyle(ChatFormatting.AQUA)));
      tooltip.add(Component.literal("para encenderlo por primera vez.").withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.empty());
      tooltip.add(
         Component.literal("Puedes ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal("reiniciarlo").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal(" para obtener").withStyle(ChatFormatting.GRAY))
      );
      tooltip.add(Component.literal("tradeos distintos.").withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.empty());
      tooltip.add(
         Component.literal("⚠ Atención: ")
            .withStyle(ChatFormatting.YELLOW)
            .append(Component.literal("Si lo enciendes, ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("NO").withStyle(ChatFormatting.RED))
      );
      tooltip.add(Component.literal("podrás apagarlo. Debes reiniciarlo").withStyle(ChatFormatting.WHITE));
      tooltip.add(Component.literal("si quieres romperlo y moverlo.").withStyle(ChatFormatting.WHITE));
   }

   public void createGeoRenderer(Consumer consumer) {
      consumer.accept(this.renderProviderHolder.getValue());
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController<>("phora_item_controller", 0, state -> PlayState.STOP));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public InteractionResult useOn(UseOnContext context) {
      if (context.getLevel().isClientSide()) {
         return InteractionResult.SUCCESS;
      }

      if (AldeanoPhoraRegistration.ALDEANO_PHORA.create(context.getLevel(), net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED) instanceof AldeanoPhoraEntity phora) {
         BlockPos placePos = context.getClickedPos().relative(context.getClickedFace());
         float yaw = context.getPlayer() == null ? 0.0F : context.getPlayer().getYRot();
         phora.snapTo(placePos.getX() + 0.5, placePos.getY(), placePos.getZ() + 0.5, yaw, 0.0F);
         if (!context.getLevel().addFreshEntity(phora)) {
            return InteractionResult.FAIL;
         }

         if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
         }

         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.FAIL;
      }
   }
}
