package com.hexagonsafio2.compat;

import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Ayudantes para métodos de 1.21.1 que ya no existen o cambiaron de forma en 26.3. */
public final class Compat {
   private Compat() {
   }

   public static Permission nivel(int n) {
      return switch (n) {
         case 0, 1 -> Permissions.COMMANDS_MODERATOR;
         case 2 -> Permissions.COMMANDS_GAMEMASTER;
         case 3 -> Permissions.COMMANDS_ADMIN;
         default -> Permissions.COMMANDS_OWNER;
      };
   }

   public static boolean permiso(CommandSourceStack s, int n) {
      return n <= 0 || s.permissions().hasPermission(nivel(n));
   }

   public static boolean permiso(Entity e, int n) {
      if (n <= 0) return true;
      if (e instanceof ServerPlayer sp) return sp.permissions().hasPermission(nivel(n));
      return e instanceof Player p && p.permissions().hasPermission(nivel(n));
   }

   public static void mensaje(Player p, Component c, boolean barra) {
      if (barra) {
         p.sendOverlayMessage(c);
      } else {
         p.sendSystemMessage(c);
      }
   }

   public static BlockPos respawnPos(ServerPlayer p) {
      ServerPlayer.RespawnConfig c = p.getRespawnConfig();
      return c == null ? null : c.respawnData().pos();
   }

   public static ResourceKey<Level> respawnDim(ServerPlayer p) {
      ServerPlayer.RespawnConfig c = p.getRespawnConfig();
      return c == null ? Level.OVERWORLD : c.respawnData().dimension();
   }

   public static float respawnAngulo(ServerPlayer p) {
      ServerPlayer.RespawnConfig c = p.getRespawnConfig();
      return c == null ? 0.0F : c.respawnData().yaw();
   }

   public static boolean respawnForzado(ServerPlayer p) {
      ServerPlayer.RespawnConfig c = p.getRespawnConfig();
      return c != null && c.forced();
   }

   public static BlockPos spawnMundo(ServerLevel l) {
      return l.getRespawnData().pos();
   }

   public static float anguloSpawnMundo(ServerLevel l) {
      return l.getRespawnData().yaw();
   }

   public static net.minecraft.server.permissions.PermissionSet permisos(int n) {
      return switch (n) {
         case 0 -> net.minecraft.server.permissions.LevelBasedPermissionSet.ALL;
         case 1 -> net.minecraft.server.permissions.LevelBasedPermissionSet.MODERATOR;
         case 2 -> net.minecraft.server.permissions.LevelBasedPermissionSet.GAMEMASTER;
         case 3 -> net.minecraft.server.permissions.LevelBasedPermissionSet.ADMIN;
         default -> net.minecraft.server.permissions.LevelBasedPermissionSet.OWNER;
      };
   }

   /** El constructor viejo de 9 argumentos de CommandSourceStack. */
   public static CommandSourceStack fuente(net.minecraft.commands.CommandSource src, net.minecraft.world.phys.Vec3 pos, net.minecraft.world.phys.Vec2 rot,
         ServerLevel nivel, int permiso, String nombre, Component mostrado, net.minecraft.server.MinecraftServer servidor, Entity entidad) {
      return new CommandSourceStack(src, pos, rot, nivel, permisos(permiso), servidor, entidad);
   }

   /** (solo proyectos sin separar cliente) Estado de dibujo que guarda la entidad, para renderers viejos que leían la entidad al dibujar. */
   public static class EstadoEntidad extends net.minecraft.client.renderer.entity.state.EntityRenderState {
      public Entity entidad;
      public float pt;
   }

   public static java.util.List<net.minecraft.world.item.ItemStack> armadura(net.minecraft.world.entity.LivingEntity e) {
      java.util.List<net.minecraft.world.item.ItemStack> l = new java.util.ArrayList<>();
      for (net.minecraft.world.entity.EquipmentSlot s : new net.minecraft.world.entity.EquipmentSlot[]{net.minecraft.world.entity.EquipmentSlot.FEET, net.minecraft.world.entity.EquipmentSlot.LEGS, net.minecraft.world.entity.EquipmentSlot.CHEST, net.minecraft.world.entity.EquipmentSlot.HEAD}) {
         l.add(e.getItemBySlot(s));
      }
      return l;
   }

   public static boolean teleport(Entity e, ServerLevel l, double x, double y, double z, float yaw, float pitch) {
      return e.teleportTo(l, x, y, z, Set.of(), yaw, pitch, true);
   }

   public static double danoBase(net.minecraft.world.entity.projectile.arrow.AbstractArrow a) {
      try {
         java.lang.reflect.Field f = net.minecraft.world.entity.projectile.arrow.AbstractArrow.class.getDeclaredField("baseDamage");
         f.setAccessible(true);
         return f.getDouble(a);
      } catch (Exception e) {
         return 2.0;
      }
   }
}
