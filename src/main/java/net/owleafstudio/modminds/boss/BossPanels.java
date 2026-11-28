package net.owleafstudio.modminds.boss;

import io.netty.buffer.Unpooled;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.network.NetworkHooks;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.BombaNegraEntity;
import net.owleafstudio.modminds.entity.ColosoEntity;
import net.owleafstudio.modminds.entity.GorgonaEntity;
import net.owleafstudio.modminds.entity.TikiGuardianEntity;
import net.owleafstudio.modminds.entity.WardenMounstrosityEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;
import net.owleafstudio.modminds.procedures.AgregarPantallaGrisProcedure;
import net.owleafstudio.modminds.procedures.BombaNegraAlJugadorColisionarConLaEntidadProcedure;
import net.owleafstudio.modminds.procedures.NauseaHydraProcedure;
import net.owleafstudio.modminds.procedures.PonerRedScreenProcedure;
import net.owleafstudio.modminds.procedures.QuitarPantallaGrisProcedure;
import net.owleafstudio.modminds.procedures.RayoEfectosEspecialesProcedure;
import net.owleafstudio.modminds.procedures.TikiEscenaProcedure;
import net.owleafstudio.modminds.procedures.TikiGuardianEmbestidaProcedure;
import net.owleafstudio.modminds.procedures.TikiGuardianEspinasProcedure;
import net.owleafstudio.modminds.procedures.TikiGuardianFuriaProcedure;
import net.owleafstudio.modminds.procedures.TikiGuardianGuardianesProcedure;
import net.owleafstudio.modminds.world.inventory.BossPanelMenu;

public final class BossPanels {
   public static final int TIQUI = 1;
   public static final int COLOSO = 2;
   public static final int GORGONA = 3;
   public static final int BOMBA_NEGRA = 4;
   public static final int WARDEN = 5;
   public static final int LIGHTING = 0;
   public static final int GRAY_SCREEN = 1;
   public static final int RED_SCREEN = 2;
   public static final int CLEAR_SCREEN = 3;
   public static final int NAUSEA = 4;
   public static final int TARGET_ME = 5;
   public static final int DROP_TARGET = 6;
   public static final int MOUNT = 7;
   public static final int EMBESTIDA = 10;
   public static final int ESPINAS = 11;
   public static final int FURIA = 12;
   public static final int GUARDIANES = 13;
   public static final int ESCENA = 14;
   public static final int PULSO = 10;
   public static final int PICADO = 11;
   public static final int PETRIFICAR = 10;
   public static final int DETONAR = 10;
   private static final double REACH = 64.0;

   public static void open(LevelAccessor world, double x, double y, double z, Entity entity, int boss) {
      if (entity instanceof ServerPlayer _ent) {
         final BlockPos _bpos = BlockPos.containing(x, y, z);
         NetworkHooks.openScreen(_ent, new MenuProvider() {
            public Component getDisplayName() {
               return Component.literal("BossPanel");
            }

            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
               return new BossPanelMenu(id, inventory, BossPanels.openingData(boss, _bpos));
            }
         }, (Consumer<FriendlyByteBuf>)(buffer -> {
            buffer.writeInt(boss);
            buffer.writeBlockPos(_bpos);
         }));
      }
   }

   private static FriendlyByteBuf openingData(int boss, BlockPos pos) {
      return new FriendlyByteBuf(Unpooled.buffer()).writeInt(boss).writeBlockPos(pos);
   }

   public static void activate(Level world, int boss, int ability, int x, int y, int z, Player player) {
      if (world.hasChunkAt(new BlockPos(x, y, z))) {
         if (ability == 0) {
            RayoEfectosEspecialesProcedure.execute(world);
         } else if (ability == 1) {
            AgregarPantallaGrisProcedure.execute(world);
         } else if (ability == 2) {
            PonerRedScreenProcedure.execute(world, x, y, z);
         } else if (ability == 3) {
            QuitarPantallaGrisProcedure.execute(world);
         } else if (ability == 4) {
            NauseaHydraProcedure.execute(world);
         } else {
            Entity idol = boss(world, boss, x, y, z);
            if (idol == null) {
               if (player instanceof ServerPlayer _player) {
                  net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.translatable("gui.modminds.panel.no_boss_near"), true);
               }
            } else if (ability == 5) {
               if (idol instanceof Mob _mob) {
                  _mob.setTarget(player);
               }
            } else if (ability == 6) {
               if (idol instanceof Mob _mob) {
                  _mob.setTarget(null);
               }
            } else if (ability == 7) {
               if (idol instanceof TikiGuardianEntity) {
                  player.startRiding(idol);
               }
            } else {
               LivingEntity target = (LivingEntity)(idol instanceof Mob _mob && _mob.getTarget() != null ? _mob.getTarget() : player);
               if (boss == 1) {
                  if (ability == 10) {
                     TikiGuardianEmbestidaProcedure.execute(world, idol, target);
                  } else if (ability == 11) {
                     TikiGuardianEspinasProcedure.execute(world, idol, target);
                  } else if (ability == 12) {
                     TikiGuardianFuriaProcedure.execute(world, idol, idol.distanceToSqr(target) > 100.0);
                  } else if (ability == 13) {
                     TikiGuardianGuardianesProcedure.execute(world, idol);
                  } else if (ability == 14) {
                     TikiEscenaProcedure.execute(world, idol);
                  }
               } else if (boss == 2) {
                  if (ability == 10) {
                     CompatAccess.persistentData(idol).putDouble("ProyectilCooldownColoso", 79.0);
                  } else if (ability == 11) {
                     idol.setDeltaMovement(idol.getDeltaMovement().add(idol.getLookAngle().scale(1.2)));
                  }
               } else if (boss == 3 && ability == 10) {
                  for (ServerPlayer victim : world.getEntitiesOfClass(
                     ServerPlayer.class, AABB.ofSize(idol.position(), 20.0, 20.0, 20.0), e -> !e.isCreative()
                  )) {
                     ModmindsModVariables.getVariables(victim).ifPresent(capability -> {
                        capability.GorgonaContadorCongelar = 79.0;
                        capability.syncPlayerVariables(victim);
                     });
                  }
               } else if (boss == 4 && ability == 10) {
                  Entity victim = nearest(
                     world.getEntitiesOfClass(Player.class, AABB.ofSize(idol.position(), 32.0, 32.0, 32.0), e -> true), idol.position()
                  );
                  if (victim != null) {
                     BombaNegraAlJugadorColisionarConLaEntidadProcedure.execute(world, idol, victim);
                  }
               }
            }
         }
      }
   }

   private static Entity boss(Level world, int boss, double x, double y, double z) {
      Vec3 center = new Vec3(x, y, z);
      AABB box = AABB.ofSize(center, 128.0, 128.0, 128.0);
      if (boss == 1) {
         return nearest(world.getEntitiesOfClass(TikiGuardianEntity.class, box, Entity::isAlive), center);
      } else if (boss == 2) {
         return nearest(world.getEntitiesOfClass(ColosoEntity.class, box, Entity::isAlive), center);
      } else if (boss == 3) {
         return nearest(world.getEntitiesOfClass(GorgonaEntity.class, box, Entity::isAlive), center);
      } else if (boss == 4) {
         return nearest(world.getEntitiesOfClass(BombaNegraEntity.class, box, Entity::isAlive), center);
      } else {
         return boss == 5 ? nearest(world.getEntitiesOfClass(WardenMounstrosityEntity.class, box, Entity::isAlive), center) : null;
      }
   }

   private static <T extends Entity> T nearest(Iterable<T> candidates, Vec3 at) {
      T best = null;
      double bestDistance = Double.MAX_VALUE;

      for (T candidate : candidates) {
         double distance = candidate.distanceToSqr(at.x, at.y, at.z);
         if (distance < bestDistance) {
            bestDistance = distance;
            best = candidate;
         }
      }

      return best;
   }

   private BossPanels() {
   }
}
