package net.ruleta.candado;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

public final class DatosCandados extends SavedData {
   private static final String NOMBRE = "ruleta_candados";
   private final Map<Long, DatosCandados.Candado> mapa = new HashMap<>();

   public static DatosCandados de(ServerLevel nivel) { return null; }

   public DatosCandados.Candado obtener(BlockPos clave) {
      return this.mapa.get(clave.asLong());
   }

   public void poner(BlockPos clave, Player dueno, String codigo) {
      DatosCandados.Candado c = new DatosCandados.Candado();
      c.propietario = dueno.getUUID();
      c.nombre = dueno.getGameProfile().name();
      c.codigo = codigo;
      c.abiertos.add(dueno.getUUID());
      this.mapa.put(clave.asLong(), c);
      this.setDirty();
   }

   public void quitar(BlockPos clave) {
      if (this.mapa.remove(clave.asLong()) != null) {
         this.setDirty();
      }
   }

   public static DatosCandados cargar(CompoundTag tag, Provider registros) { return null; }

   public CompoundTag save(CompoundTag tag, Provider registros) { return null; }

   public static final class Candado {
      public UUID propietario;
      public String nombre = "";
      public String codigo = "";
      public final Set<UUID> abiertos = new HashSet<>();
   }
}
