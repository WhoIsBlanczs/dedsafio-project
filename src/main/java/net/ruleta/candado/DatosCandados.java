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
import net.minecraft.world.level.saveddata.SavedData.Factory;

public final class DatosCandados extends SavedData {
   private static final String NOMBRE = "ruleta_candados";
   private final Map<Long, DatosCandados.Candado> mapa = new HashMap<>();

   public static DatosCandados de(ServerLevel nivel) {
      return (DatosCandados)nivel.getDataStorage()
         .computeIfAbsent(new Factory(DatosCandados::new, DatosCandados::cargar, DataFixTypes.LEVEL), "ruleta_candados");
   }

   public DatosCandados.Candado obtener(BlockPos clave) {
      return this.mapa.get(clave.asLong());
   }

   public void poner(BlockPos clave, Player dueno, String codigo) {
      DatosCandados.Candado c = new DatosCandados.Candado();
      c.propietario = dueno.getUUID();
      c.nombre = dueno.getGameProfile().getName();
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

   public static DatosCandados cargar(CompoundTag tag, Provider registros) {
      DatosCandados d = new DatosCandados();
      ListTag lista = tag.getList("Candados", 10);

      for (int i = 0; i < lista.size(); i++) {
         CompoundTag t = lista.getCompound(i);
         DatosCandados.Candado c = new DatosCandados.Candado();
         c.propietario = t.hasUUID("Dueno") ? t.getUUID("Dueno") : null;
         c.nombre = t.getString("Nombre");
         c.codigo = t.getString("Codigo");
         ListTag ab = t.getList("Abiertos", 11);

         for (int j = 0; j < ab.size(); j++) {
            c.abiertos.add(NbtUtils.loadUUID(ab.get(j)));
         }

         d.mapa.put(t.getLong("Pos"), c);
      }

      return d;
   }

   public CompoundTag save(CompoundTag tag, Provider registros) {
      ListTag lista = new ListTag();

      for (Entry<Long, DatosCandados.Candado> en : this.mapa.entrySet()) {
         CompoundTag t = new CompoundTag();
         t.putLong("Pos", en.getKey());
         DatosCandados.Candado c = en.getValue();
         if (c.propietario != null) {
            t.putUUID("Dueno", c.propietario);
         }

         t.putString("Nombre", c.nombre);
         t.putString("Codigo", c.codigo);
         ListTag ab = new ListTag();

         for (UUID u : c.abiertos) {
            ab.add(NbtUtils.createUUID(u));
         }

         t.put("Abiertos", ab);
         lista.add(t);
      }

      tag.put("Candados", lista);
      return tag;
   }

   public static final class Candado {
      public UUID propietario;
      public String nombre = "";
      public String codigo = "";
      public final Set<UUID> abiertos = new HashSet<>();
   }
}
