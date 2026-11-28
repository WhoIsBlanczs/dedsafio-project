package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModEnchantments;

@Mod.EventBusSubscriber
public class AspectoDeSanguijuelaProcedureProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingHurtEvent event) {
      if (event != null && event.getEntity() != null) {
         execute(event, event.getEntity().level(), event.getSource().getEntity());
      }
   }

   public static void execute(LevelAccessor world, Entity sourceentity) {
      execute(null, world, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity sourceentity) {
      if (sourceentity != null
         && EnchantmentHelper.getItemEnchantmentLevel(
               CompatAccess.enchantment(ModmindsModEnchantments.ASPECTO_DE_SANGUIJUELA),
               sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY
            )
            != 0
         && Math.random() < 0.7
         && sourceentity instanceof LivingEntity _entity) {
         _entity.setHealth((sourceentity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) + 1.0F);
      }
   }
}
