package net.ruleta.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.ruleta.OndaPayload;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import org.joml.Matrix4f;

public final class OndaExplosion {
   private static final float DURACION = 1.1F;
   private static final float PIXEL = 0.5F;
   private static final int MAXIMO = 24;
   private static final List<OndaExplosion.Onda> ONDAS = new ArrayList<>();

   private OndaExplosion() {
   }

   static void agregar(OndaPayload p) {
      if (ONDAS.size() >= 24) {
         ONDAS.remove(0);
      }

      ONDAS.add(new OndaExplosion.Onda(p.x(), p.y(), p.z(), p.radio(), System.nanoTime()));
   }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut evento) {
      ONDAS.clear();
   }

   @SubscribeEvent
   public static void alDibujar(RenderLevelStageEvent evento) { }

   private static boolean dibujar(Object b, Matrix4f m, OndaExplosion.Onda o, float t, Vec3 cam) { return false; }

   private static int color(float u, float t) {
      if (t > 0.6F) {
         return u > 0.5F ? (t > 0.8F ? -523753444 : -252681196) : -385890284;
      } else if (u > 0.82F) {
         return -117474816;
      } else if (u > 0.55F) {
         return -184564736;
      } else {
         return u > 0.3F ? -419435892 : -1258293032;
      }
   }

   private record Onda(double x, double y, double z, float radio, long inicio) {
   }
}
