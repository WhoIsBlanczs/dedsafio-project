package net.ruleta.client.burbuja;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ruleta.burbuja.Burbuja;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;

public final class BurbujaCliente {
   private static boolean estabaDentro;

   private BurbujaCliente() {
   }

   static void mover(Burbuja b) { }

   private static void colisionar(Burbuja b, float hundir) {
      Level nivel = b.level();
      double R = b.getDiametro() / 2.0;
      double re = Math.max(0.3, R - hundir);
      Vec3 v = b.getDeltaMovement();
      double cx = b.getX() + v.x;
      double cy = b.getY() + R + v.y;
      double cz = b.getZ() + v.z;
      double nx = 0.0;
      double ny = 0.0;
      double nz = 0.0;

      for (int vuelta = 0; vuelta < 3; vuelta++) {
         boolean toco = false;

         for (int x = Mth.floor(cx - re); x <= Mth.floor(cx + re); x++) {
            for (int y = Mth.floor(cy - re); y <= Mth.floor(cy + re); y++) {
               for (int z = Mth.floor(cz - re); z <= Mth.floor(cz + re); z++) {
                  BlockPos bp = new BlockPos(x, y, z);
                  BlockState st = nivel.getBlockState(bp);
                  if (!st.isAir()) {
                     VoxelShape forma = st.getCollisionShape(nivel, bp);
                     if (!forma.isEmpty()) {
                        for (AABB caja : forma.toAabbs()) {
                           double qx = Mth.clamp(cx, x + caja.minX, x + caja.maxX);
                           double qy = Mth.clamp(cy, y + caja.minY, y + caja.maxY);
                           double qz = Mth.clamp(cz, z + caja.minZ, z + caja.maxZ);
                           double dx = cx - qx;
                           double dy = cy - qy;
                           double dz = cz - qz;
                           double d2 = dx * dx + dy * dy + dz * dz;
                           if (!(d2 >= re * re)) {
                              double d = Math.sqrt(d2);
                              if (d < 1.0E-6) {
                                 dx = 0.0;
                                 dy = 1.0;
                                 dz = 0.0;
                                 d = 0.0;
                              } else {
                                 dx /= d;
                                 dy /= d;
                                 dz /= d;
                              }

                              double empuje = re - d;
                              cx += dx * empuje;
                              cy += dy * empuje;
                              cz += dz * empuje;
                              nx += dx;
                              ny += dy;
                              nz += dz;
                              toco = true;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (!toco) {
            break;
         }
      }

      double ln = Math.sqrt(nx * nx + ny * ny + nz * nz);
      if (ln > 1.0E-6) {
         nx /= ln;
         ny /= ln;
         nz /= ln;
         double hacia = v.x * nx + v.y * ny + v.z * nz;
         if (hacia < 0.0) {
            v = new Vec3(v.x - nx * hacia, v.y - ny * hacia, v.z - nz * hacia);
         }

         b.setDeltaMovement(v.scale(0.92));
      }

      b.setPos(cx, cy - R, cz);
   }

   public static void registrar() {
      Burbuja.movimientoCliente = BurbujaCliente::mover;
   }

   public static final class Teclas {
      @SubscribeEvent
      public static void alTick(TickEvent.ClientTickEvent e) {
         if (e.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            boolean dentro = mc.player != null && mc.player.getVehicle() instanceof Burbuja;
            if (dentro && !BurbujaCliente.estabaDentro) {
               mc.player
                  .sendOverlayMessage(Component.translatable("ruleta.burbuja.bajar", new Object[]{mc.options.keyShift.getTranslatedKeyMessage()}));
            }

            BurbujaCliente.estabaDentro = dentro;
         }
      }
   }
}
