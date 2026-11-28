package net.owleafstudio.modminds.compat;

import java.lang.reflect.Method;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public final class ClientCompat {
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-compat");
   private static Method registerProperty;
   private static boolean propertyResolved;

   private ClientCompat() {
   }

   public static void registerBlocking(Item item) {
      register(
         item,
         Identifier.parse("blocking"),
         (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F
      );
   }

   public static void register(Item item, Identifier name, ClampedItemPropertyFunction getter) {
      if (item != null) {
         if (!propertyResolved) {
            propertyResolved = true;

            try {
               registerProperty = ItemProperties.class.getDeclaredMethod("register", Item.class, Identifier.class, ClampedItemPropertyFunction.class);
               registerProperty.setAccessible(true);
            } catch (Throwable var5) {
               LOGGER.warn("ItemProperties.register is unreachable, model properties will not animate");
            }
         }

         if (registerProperty != null) {
            try {
               registerProperty.invoke(null, item, name, getter);
            } catch (Throwable var4) {
               LOGGER.warn("Could not register item property {} for {}", new Object[]{name, item, var4});
            }
         }
      }
   }

   public static void renderEntityInInventoryFollowsAngle(
      GuiGraphicsExtractor guiGraphics, int centerX, int centerY, int size, float angleX, float angleY, LivingEntity entity
   ) {
      float scale = entity.getScale();
      Quaternionf cameraAngle = new Quaternionf().rotateZ((float) Math.PI);
      Quaternionf bodyAngle = new Quaternionf().rotateX(angleY * 20.0F * (float) (Math.PI / 180.0));
      Vector3f cameraOffset = new Vector3f(0.0F, entity.getBbHeight() / 2.0F + angleX * scale, 0.0F);
      float bodyYaw = entity.yBodyRot;
      float yaw = entity.getYRot();
      float pitch = entity.getXRot();
      float headYawOld = entity.yHeadRotO;
      float headYaw = entity.yHeadRot;
      entity.yBodyRot = 180.0F + angleX * 20.0F;
      entity.setYRot(180.0F + angleX * 40.0F);
      entity.setXRot(-angleY * 20.0F);
      entity.yHeadRot = entity.getYRot();
      entity.yHeadRotO = entity.getYRot();
      InventoryScreen.renderEntityInInventory(guiGraphics, centerX, centerY, size / scale, cameraOffset, cameraAngle, bodyAngle, entity);
      entity.yBodyRot = bodyYaw;
      entity.setYRot(yaw);
      entity.setXRot(pitch);
      entity.yHeadRotO = headYawOld;
      entity.yHeadRot = headYaw;
   }
}
