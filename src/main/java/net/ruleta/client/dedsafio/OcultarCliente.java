package net.ruleta.client.dedsafio;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.ruleta.dedsafio.EscenaPayload;

public final class OcultarCliente {
   private static boolean todos = true;
   private static final Set<Integer> visibles = new HashSet<>();

   private OcultarCliente() {
   }

   public static void recibir(EscenaPayload m) {
      if (m.tipo() == 6) {
         todos = m.a() > 0.5F;
      } else if (m.tipo() == 7) {
         visibles.clear();
      } else if (m.tipo() == 8) {
         visibles.add((int)m.a());
      }
   }

   public static boolean ocultar(Entity e) {
      if (todos && e instanceof Player) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null && mc.player != null && e != mc.player) {
            ResourceLocation d = mc.level.dimension().location();
            return d.getNamespace().equals("ruleta") && d.getPath().equals("dedsafio") && !visibles.contains(e.getId());
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
