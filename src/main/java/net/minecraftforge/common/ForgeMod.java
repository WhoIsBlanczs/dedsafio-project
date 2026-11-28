package net.minecraftforge.common;

import java.util.function.Supplier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ForgeMod {
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-compat");
   public static final Supplier<Holder<Attribute>> SWIM_SPEED = ForgeMod::swimSpeed;
   private static volatile Holder<Attribute> swimSpeedHolder;

   private static Holder<Attribute> swimSpeed() {
      Holder<Attribute> local = swimSpeedHolder;
      if (local == null) {
         synchronized (ForgeMod.class) {
            local = swimSpeedHolder;
            if (local == null) {
               Attribute attr = new RangedAttribute("attribute.name.modminds.swim_speed", 0.0, -1024.0, 1024.0).setSyncable(true);
               Identifier id = Identifier.fromNamespaceAndPath("modminds", "swim_speed");

               try {
                  local = Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, id, attr);
               } catch (Throwable var6) {
                  LOGGER.warn("swim_speed attribute already frozen, using direct holder: {}", var6.toString());
                  local = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attr);
               }

               swimSpeedHolder = local;
            }
         }
      }

      return local;
   }

   private ForgeMod() {
   }
}
