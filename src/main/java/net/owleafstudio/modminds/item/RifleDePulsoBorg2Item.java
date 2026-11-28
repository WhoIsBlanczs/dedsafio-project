package net.owleafstudio.modminds.item;

import net.minecraft.world.InteractionResult;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.owleafstudio.modminds.item.renderer.RifleDePulsoBorg2ItemRenderer;
import net.owleafstudio.modminds.procedures.ArmaCortaEspacialCuandoSeHagaClicDerechoProcedure;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class RifleDePulsoBorg2Item extends Item implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public RifleDePulsoBorg2Item() {
      super(new Item.Properties().durability(26).rarity(Rarity.COMMON));
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return false;
   }

   public void createGeoRenderer(java.util.function.Consumer<com.geckolib.animatable.client.GeoRenderProvider> consumer) {
      consumer.accept(new com.geckolib.animatable.client.GeoRenderProvider() {
         private net.owleafstudio.modminds.item.renderer.RifleDePulsoBorg2ItemRenderer renderer;

         @Override
         public com.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
            if (this.renderer == null) {
               this.renderer = new net.owleafstudio.modminds.item.renderer.RifleDePulsoBorg2ItemRenderer();
            }
            return this.renderer;
         }
      });
   }

   private PlayState idlePredicate(AnimationTest<?> event) {
      if (this.animationprocedure.equals("empty")) {
         event.controller().setAnimation(RawAnimation.begin().thenLoop("animation.armaborgverde.idle"));
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState procedurePredicate(AnimationTest<?> event) {
      if (!this.animationprocedure.equals("empty") && event.controller().hasAnimationFinished()
         || !this.animationprocedure.equals(this.prevAnim) && !this.animationprocedure.equals("empty")) {
         if (!this.animationprocedure.equals(this.prevAnim)) {
            event.controller().reset();
         }

         event.controller().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
         if (event.controller().hasAnimationFinished()) {
            this.animationprocedure = "empty";
            event.controller().reset();
         }
      } else if (this.animationprocedure.equals("empty")) {
         this.prevAnim = "empty";
         return PlayState.STOP;
      }

      this.prevAnim = this.animationprocedure;
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar data) {
      AnimationController procedureController = new AnimationController<>("procedureController", 0, this::procedurePredicate);
      data.add(procedureController);
      AnimationController idleController = new AnimationController<>("idleController", 0, this::idlePredicate);
      data.add(idleController);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
      return 0.0F;
   }

   public void appendHoverText(ItemStack itemstack, net.minecraft.world.item.Item.TooltipContext level, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, java.util.function.Consumer<Component> tooltipAdder$, TooltipFlag flag) {
      java.util.List<Component> list = new java.util.ArrayList<>();
      this.appendHoverText$viejo(itemstack, level, tooltipDisplay$, list, flag);
      list.forEach(tooltipAdder$);
   }

   private void appendHoverText$viejo(ItemStack itemstack, net.minecraft.world.item.Item.TooltipContext level, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, level, tooltipDisplay$, list::add, flag);
      list.add(Component.literal("§7Daño: 13.00"));
      list.add(Component.literal("§7Daño a la cabeza: 13.00"));
      list.add(Component.literal("§7Daño a bolques: 0.00"));
      list.add(Component.literal("§7Frecuencia de disparo: 0.13"));
      list.add(Component.literal("§7Tiempo de recarga: 2.50"));
      list.add(Component.literal("§7Tipo de municion§f: Bala Estandar Borg"));
   }

   public InteractionResult use(Level world, Player entity, InteractionHand hand) {
      InteractionResult ar = super.use(world, entity, hand);
      ItemStack itemstack = (ItemStack)ar.getObject();
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      ArmaCortaEspacialCuandoSeHagaClicDerechoProcedure.execute(entity);
      return ar;
   }
}
