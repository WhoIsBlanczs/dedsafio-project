package net.ruleta.objetos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public final class Luces {
   private static final double[] DISTANCIAS = new double[]{3.0, 7.0};
   private static final Map<UUID, Object[]> PUESTAS = new HashMap<>();

   private Luces() {
   }

   static void alumbrar(ServerPlayer p, float intensidad) {
      ServerLevel nivel = p.level();
      float yaw = p.getYRot() * (float) (Math.PI / 180.0);
      Vec3 frente = new Vec3(-Math.sin(yaw), 0.0, Math.cos(yaw));
      Vec3 ojos = p.getEyePosition();
      int fuerte = Math.max(6, Math.min(15, Math.round(15.0F * intensidad)));
      List<Object[]> nuevas = new ArrayList<>();

      for (double dist : DISTANCIAS) {
         Vec3 fin = ojos.add(frente.scale(dist));
         BlockHitResult hit = nivel.clip(new ClipContext(ojos, fin, Block.COLLIDER, Fluid.NONE, p));
         BlockPos pos = hit.getType() == Type.MISS ? BlockPos.containing(fin) : hit.getBlockPos().relative(hit.getDirection());
         int luz = dist > DISTANCIAS[0] ? Math.max(4, fuerte - 3) : fuerte;
         nuevas.add(new Object[]{pos.immutable(), luz});
         if (hit.getType() != Type.MISS) {
            break;
         }
      }

      Object[] antes = PUESTAS.get(p.getUUID());
      if (antes == null || antes[0] != nivel.dimension() || !iguales(antes[1], nuevas)) {
         List<Object[]> puestas = new ArrayList<>();

         for (Object[] n : nuevas) {
            BlockPos pos = (BlockPos)n[0];
            int luz = (Integer)n[1];
            BlockState aqui = nivel.getBlockState(pos);
            if (aqui.isAir() || aqui.is(Blocks.LIGHT)) {
               if (!aqui.is(Blocks.LIGHT) || (Integer)aqui.getValue(LightBlock.LEVEL) != luz) {
                  nivel.setBlock(pos, (BlockState)Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, luz), 3);
               }

               puestas.add(n);
            }
         }

         if (antes != null) {
            List<Object[]> viejas = (List<Object[]>)antes[1];
            ServerLevel nivelViejo = p.level().getServer().getLevel((ResourceKey)antes[0]);

            for (Object[] v : viejas) {
               BlockPos pos = (BlockPos)v[0];
               boolean sigue = antes[0] == nivel.dimension() && puestas.stream().anyMatch(n -> n[0].equals(pos));
               if (!sigue && nivelViejo != null && nivelViejo.getBlockState(pos).is(Blocks.LIGHT)) {
                  nivelViejo.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
               }
            }
         }

         PUESTAS.put(p.getUUID(), new Object[]{nivel.dimension(), puestas});
      }
   }

   private static boolean iguales(Object viejo, List<Object[]> nuevas) {
      List<Object[]> v = (List<Object[]>)viejo;
      if (v.size() != nuevas.size()) {
         return false;
      }

      for (int i = 0; i < v.size(); i++) {
         if (!v.get(i)[0].equals(nuevas.get(i)[0]) || !v.get(i)[1].equals(nuevas.get(i)[1])) {
            return false;
         }
      }

      return true;
   }

   static void quitar(MinecraftServer server, UUID id) {
      Object[] o = PUESTAS.remove(id);
      if (o != null && server != null) {
         ServerLevel nivel = server.getLevel((ResourceKey)o[0]);

         for (Object[] l : (List)o[1]) {
            BlockPos pos = (BlockPos)l[0];
            if (nivel != null && nivel.getBlockState(pos).is(Blocks.LIGHT)) {
               nivel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
         }
      }
   }

   public static void alTick(MinecraftServer servidor) {
      if (!PUESTAS.isEmpty()) {
         MinecraftServer server = servidor;

         for (UUID id : new ArrayList<>(PUESTAS.keySet())) {
            ServerPlayer p = server.getPlayerList().getPlayer(id);
            if (p == null || !p.isAlive() || !prendidaEnMano(p)) {
               quitar(server, id);
            }
         }
      }
   }

   private static boolean prendidaEnMano(ServerPlayer p) {
      return luce(p.getMainHandItem()) || luce(p.getOffhandItem());
   }

   private static boolean luce(ItemStack s) {
      return s.is(Objetos.LINTERNA.get()) && Linterna.prendida(s);
   }

   public static void alSalir(ServerPlayer p) {
      quitar(p.level().getServer(), p.getUUID());
   }

   public static void alCambiarDimension(ServerPlayer p) {
      quitar(p.getServer(), p.getUUID());
   }

   public static void alParar(MinecraftServer servidor) {
      for (UUID id : new ArrayList<>(PUESTAS.keySet())) {
         quitar(servidor, id);
      }
   }
}
