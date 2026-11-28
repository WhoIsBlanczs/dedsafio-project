package net.owleafstudio.modminds.init;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.item.ArmaCortaEspacialItem;
import net.owleafstudio.modminds.item.AtomizadorDeAstrosItem;
import net.owleafstudio.modminds.item.CerbatanaDeGoblinItem;
import net.owleafstudio.modminds.item.PerforadorDelVacioItem;
import net.owleafstudio.modminds.item.RifleDePulsoBorg2Item;
import com.geckolib.animatable.GeoItem;

@Mod.EventBusSubscriber
public class ItemAnimationFactory {
   @SubscribeEvent
   public static void animatedItems(TickEvent.PlayerTickEvent event) {
      String animation = "";
      ItemStack mainhandItem = event.player.getMainHandItem().copy();
      ItemStack offhandItem = event.player.getOffhandItem().copy();
      if (event.phase == TickEvent.Phase.START && (mainhandItem.getItem() instanceof GeoItem || offhandItem.getItem() instanceof GeoItem)) {
         if (mainhandItem.getItem() instanceof ArmaCortaEspacialItem animatable) {
            animation = CompatAccess.itemTag(mainhandItem).getStringOr("geckoAnim", "");
            if (!animation.isEmpty()) {
               CompatAccess.itemTag(event.player.getMainHandItem()).putString("geckoAnim", "");
               if (event.player.level().isClientSide()) {
                  ((ArmaCortaEspacialItem)event.player.getMainHandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof ArmaCortaEspacialItem animatablex) {
            animation = CompatAccess.itemTag(offhandItem).getStringOr("geckoAnim", "");
            if (!animation.isEmpty()) {
               CompatAccess.itemTag(event.player.getOffhandItem()).putString("geckoAnim", "");
               if (event.player.level().isClientSide()) {
                  ((ArmaCortaEspacialItem)event.player.getOffhandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (mainhandItem.getItem() instanceof RifleDePulsoBorg2Item animatablexx) {
            animation = CompatAccess.itemTag(mainhandItem).getStringOr("geckoAnim", "");
            if (!animation.isEmpty()) {
               CompatAccess.itemTag(event.player.getMainHandItem()).putString("geckoAnim", "");
               if (event.player.level().isClientSide()) {
                  ((RifleDePulsoBorg2Item)event.player.getMainHandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof RifleDePulsoBorg2Item animatablexxx) {
            animation = CompatAccess.itemTag(offhandItem).getStringOr("geckoAnim", "");
            if (!animation.isEmpty()) {
               CompatAccess.itemTag(event.player.getOffhandItem()).putString("geckoAnim", "");
               if (event.player.level().isClientSide()) {
                  ((RifleDePulsoBorg2Item)event.player.getOffhandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (mainhandItem.getItem() instanceof CerbatanaDeGoblinItem animatablexxxx) {
            animation = CompatAccess.itemTag(mainhandItem).getStringOr("geckoAnim", "");
            if (!animation.isEmpty()) {
               CompatAccess.itemTag(event.player.getMainHandItem()).putString("geckoAnim", "");
               if (event.player.level().isClientSide()) {
                  ((CerbatanaDeGoblinItem)event.player.getMainHandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof CerbatanaDeGoblinItem animatablexxxxx) {
            animation = CompatAccess.itemTag(offhandItem).getStringOr("geckoAnim", "");
            if (!animation.isEmpty()) {
               CompatAccess.itemTag(event.player.getOffhandItem()).putString("geckoAnim", "");
               if (event.player.level().isClientSide()) {
                  ((CerbatanaDeGoblinItem)event.player.getOffhandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (mainhandItem.getItem() instanceof PerforadorDelVacioItem animatablexxxxxx) {
            animation = CompatAccess.itemTag(mainhandItem).getStringOr("geckoAnim", "");
            if (!animation.isEmpty()) {
               CompatAccess.itemTag(event.player.getMainHandItem()).putString("geckoAnim", "");
               if (event.player.level().isClientSide()) {
                  ((PerforadorDelVacioItem)event.player.getMainHandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof PerforadorDelVacioItem animatablexxxxxxx) {
            animation = CompatAccess.itemTag(offhandItem).getStringOr("geckoAnim", "");
            if (!animation.isEmpty()) {
               CompatAccess.itemTag(event.player.getOffhandItem()).putString("geckoAnim", "");
               if (event.player.level().isClientSide()) {
                  ((PerforadorDelVacioItem)event.player.getOffhandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (mainhandItem.getItem() instanceof AtomizadorDeAstrosItem animatablexxxxxxxx) {
            animation = CompatAccess.itemTag(mainhandItem).getStringOr("geckoAnim", "");
            if (!animation.isEmpty()) {
               CompatAccess.itemTag(event.player.getMainHandItem()).putString("geckoAnim", "");
               if (event.player.level().isClientSide()) {
                  ((AtomizadorDeAstrosItem)event.player.getMainHandItem().getItem()).animationprocedure = animation;
               }
            }
         }

         if (offhandItem.getItem() instanceof AtomizadorDeAstrosItem animatablexxxxxxxxx) {
            animation = CompatAccess.itemTag(offhandItem).getStringOr("geckoAnim", "");
            if (!animation.isEmpty()) {
               CompatAccess.itemTag(event.player.getOffhandItem()).putString("geckoAnim", "");
               if (event.player.level().isClientSide()) {
                  ((AtomizadorDeAstrosItem)event.player.getOffhandItem().getItem()).animationprocedure = animation;
               }
            }
         }
      }
   }
}
