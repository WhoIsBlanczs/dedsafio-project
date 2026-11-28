package net.owleafstudio.modminds.compat;

import java.lang.reflect.Method;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
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

   }

   /** En 26.3 las propiedades de modelo de ítems se definen en assets/modminds/items/*.json. */
   public static void register(Item item, Identifier name, Object getter) {
   }

   public static void renderEntityInInventoryFollowsAngle(
      GuiGraphicsExtractor guiGraphics, int centerX, int centerY, int size, float angleX, float angleY, LivingEntity entity
   ) {
      float mouseX = (float)(centerX - Math.tan(angleX) * 40.0);
      float mouseY = (float)(centerY - size - Math.tan(angleY) * 40.0);
      InventoryScreen.extractEntityInInventoryFollowsMouse(
         guiGraphics, centerX - size, centerY - size * 2, centerX + size, centerY, size, 0.0625F, mouseX, mouseY, entity
      );
   }
}
