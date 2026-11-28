package com.novastudio.dedsafio4;

import com.novastudio.dedsafio4.mixin.CreeperMaxSwellAccessor;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.registries.BuiltInRegistries;

public final class CreeperAbilities {
   private CreeperAbilities() {
   }

   private static int fuseTicks(String type) {
      int base = switch (type) {
         case "dorado" -> 39;
         case "floral" -> 40;
         case "metalico" -> 32;
         case "creeper" -> 35;
         case "tronco" -> 48;
         default -> 30;
      };
      return CreeperExtras.fuse(base, type);
   }

   public static void applyFuse(Creeper creeper, String type) {
      ((CreeperMaxSwellAccessor)creeper).nova$setMaxSwell(fuseTicks(type));
   }

   private static List<Player> playersNear(CreeperGeoEntity c, double r) {
      AABB box = new AABB(
         c.getX() - r, c.getY() - r, c.getZ() - r, c.getX() + r, c.getY() + r, c.getZ() + r
      );
      return c.level().getEntitiesOfClass(Player.class, box);
   }

   public static boolean onExplode(CreeperGeoEntity c) {
      CreeperExtras.onExplode(c);
      String var1 = c.animationId();
      switch (var1) {
         case "floral":
            for (Player p : playersNear(c, 6.0)) {
               p.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 40, 0));
            }
            break;
         case "tronco":
            for (Player p : playersNear(c, 6.0)) {
               p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 8));
            }
            break;
         case "metalico":
            launch(c);
      }

      return true;
   }

   private static void launch(CreeperGeoEntity c) {
      for (Player p : playersNear(c, 6.0)) {
         double dx = p.getX() - c.getX();
         double dz = p.getZ() - c.getZ();
         double len = Math.sqrt(dx * dx + dz * dz);
         if (len < 0.001) {
            dx = 1.0;
            dz = 0.0;
            len = 1.0;
         }

         dx /= len;
         dz /= len;
         p.setDeltaMovement(dx * 0.95, 3.45, dz * 0.95);
         if (p instanceof ServerPlayer sp) {
            sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
         }

         p.fallDistance = 0.0F;
      }
   }

   public static void popTotem(Player p) {
      for (ItemStack stack : new ItemStack[]{p.getOffhandItem(), p.getMainHandItem()}) {
         if (!stack.isEmpty() && isTotem(stack)) {
            stack.shrink(1);
            p.level().broadcastEntityEvent(p, (byte)35);
            break;
         }
      }
   }

   private static boolean isTotem(ItemStack stack) {
      if (stack.is(Items.TOTEM_OF_UNDYING)) {
         return true;
      } else {
         Identifier key = BuiltInRegistries.ITEM.getKey(stack.getItem());
         return key.getNamespace().equals("nexo") && key.getPath().startsWith("totem");
      }
   }
}
