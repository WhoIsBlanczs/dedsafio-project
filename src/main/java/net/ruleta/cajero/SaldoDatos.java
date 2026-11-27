package net.ruleta.cajero;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;

public class SaldoDatos extends SavedData {
   private static final String NOMBRE = "ruleta_cajero";
   public static final long MAXIMO = 9000000000000L;
   private final Map<UUID, Long> saldos = new HashMap<>();

   public static SaldoDatos get(MinecraftServer servidor) {
      return (SaldoDatos)servidor.overworld()
         .getDataStorage()
         .computeIfAbsent(new Factory(SaldoDatos::new, SaldoDatos::cargar, DataFixTypes.LEVEL), "ruleta_cajero");
   }

   public long saldo(UUID jugador) {
      return this.saldos.getOrDefault(jugador, 0L);
   }

   public void poner(UUID jugador, long valor) {
      long v = Math.max(0L, Math.min(9000000000000L, valor));
      if (v == 0L) {
         this.saldos.remove(jugador);
      } else {
         this.saldos.put(jugador, v);
      }

      this.setDirty();
   }

   public static SaldoDatos cargar(CompoundTag tag, Provider registros) {
      SaldoDatos d = new SaldoDatos();
      ListTag lista = tag.getListOrEmpty("saldos");

      for (int i = 0; i < lista.size(); i++) {
         CompoundTag c = lista.getCompoundOrEmpty(i);

         try {
            d.saldos.put(UUID.fromString(c.getStringOr("id", "")), c.getLongOr("saldo", 0L));
         } catch (IllegalArgumentException var7) {
         }
      }

      return d;
   }

   public CompoundTag save(CompoundTag tag, Provider registros) {
      ListTag lista = new ListTag();

      for (Entry<UUID, Long> e : this.saldos.entrySet()) {
         CompoundTag c = new CompoundTag();
         c.putString("id", e.getKey().toString());
         c.putLong("saldo", e.getValue());
         lista.add(c);
      }

      tag.put("saldos", lista);
      return tag;
   }
}
