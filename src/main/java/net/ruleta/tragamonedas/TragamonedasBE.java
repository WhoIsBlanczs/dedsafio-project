package net.ruleta.tragamonedas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TragamonedasBE extends BlockEntity {
   public static final int CARAS = 8;
   public static final int RODILLOS = 3;
   public static final int TICKS_PALANCA = 18;
   public static final int RETRASO = 18;
   public String[] caras = new String[8];
   public int[] resultado = new int[3];
   public int[] previo = new int[3];
   public static final int[] DURACION = new int[]{52, 61, 70};
   public int[] duracion = (int[])DURACION.clone();
   public long inicio = -100000L;
   private boolean pagoPendiente;
   private boolean iniciado;
   private boolean sonidoGiroPendiente;
   public ItemStack[] pilas;

   public TragamonedasBE(BlockPos pos, BlockState state) {
      super(Tragamonedas.BE.get(), pos, state);
      List<String> def = ConfigTragamonedas.carasPorDefecto();

      for (int i = 0; i < 8; i++) {
         this.caras[i] = def.get(i % def.size());
      }
   }

   public int ticksTotales() {
      return 18 + Math.max(this.duracion[0], Math.max(this.duracion[1], this.duracion[2])) + 4;
   }

   public boolean ocupada(Level level) {
      return this.pagoPendiente || level.getGameTime() < this.inicio + this.ticksTotales();
   }

   public static void tickServidor(Level level, BlockPos pos, BlockState estado, TragamonedasBE be) {
      if (level instanceof ServerLevel nivel) {
         if (!be.iniciado) {
            be.iniciado = true;
            be.ponerCarasDeConfig();
            be.sincronizar();
         }

         if (be.sonidoGiroPendiente && nivel.getGameTime() >= be.inicio + 18L) {
            be.sonidoGiroPendiente = false;
            nivel.playSound(null, pos, Tragamonedas.SONIDO_GIRO.get(), SoundSource.BLOCKS, 0.8F, 1.0F);
         }

         if (be.pagoPendiente && nivel.getGameTime() >= be.inicio + be.ticksTotales()) {
            be.pagoPendiente = false;
            be.pagar(nivel, estado);
            be.setChanged();
         }
      }
   }

   private void ponerCarasDeConfig() {
      List<ConfigTragamonedas.Simbolo> s = ConfigTragamonedas.get().simbolos;

      for (int i = 0; i < 8; i++) {
         this.caras[i] = s.get(i % s.size()).item;
      }
   }

   public boolean girar(ServerLevel level) {
      if (this.ocupada(level)) {
         return false;
      }

      ConfigTragamonedas cfg = ConfigTragamonedas.get();
      this.previo = (int[])this.resultado.clone();
      this.ponerCarasDeConfig();
      List<ConfigTragamonedas.Simbolo> simbolos = cfg.simbolos;
      int total = 0;

      for (ConfigTragamonedas.Simbolo s : simbolos) {
         total += Math.max(0, s.peso);
      }

      for (int r = 0; r < 3; r++) {
         int idx = 0;
         if (total > 0) {
            int tirada = level.random.nextInt(total);

            for (int i = 0; i < simbolos.size(); i++) {
               tirada -= Math.max(0, simbolos.get(i).peso);
               if (tirada < 0) {
                  idx = i;
                  break;
               }
            }
         } else {
            idx = level.random.nextInt(simbolos.size());
         }

         List<Integer> candidatas = new ArrayList<>();

         for (int f = idx; f < 8; f += simbolos.size()) {
            candidatas.add(f);
         }

         this.resultado[r] = candidatas.get(level.random.nextInt(candidatas.size()));
      }

      this.duracion = (int[])DURACION.clone();
      this.inicio = level.getGameTime();
      this.pagoPendiente = true;
      this.iniciado = true;
      this.sincronizar();
      level.playSound(null, this.worldPosition, Tragamonedas.SONIDO_PALANCA.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
      this.sonidoGiroPendiente = true;
      return true;
   }

   private void pagar(ServerLevel level, BlockState estado) {
      String a = this.caras[this.resultado[0]];
      String b = this.caras[this.resultado[1]];
      String c = this.caras[this.resultado[2]];

      for (ConfigTragamonedas.Premio p : ConfigTragamonedas.get().premios) {
         if (coincide(p, a, b, c)) {
            this.repartir(level, estado, p);
            return;
         }
      }
   }

   private static boolean coincide(ConfigTragamonedas.Premio p, String a, String b, String c) {
      if ("iguales".equals(p.tipo)) {
         String repetido = null;
         if (p.iguales >= 3) {
            if (a.equals(b) && b.equals(c)) {
               repetido = a;
            }
         } else if (a.equals(b) && !b.equals(c)) {
            repetido = a;
         } else if (a.equals(c) && !a.equals(b)) {
            repetido = a;
         } else if (b.equals(c) && !a.equals(b)) {
            repetido = b;
         }

         return repetido != null && (p.items.isEmpty() || p.items.contains(repetido));
      } else {
         return p.combinacion.size() == 3 && encaja(p.combinacion.get(0), a) && encaja(p.combinacion.get(1), b) && encaja(p.combinacion.get(2), c);
      }
   }

   private void repartir(ServerLevel level, BlockState estado, ConfigTragamonedas.Premio p) {
      Direction f = (Direction)estado.getValue(TragamonedasBloque.FACING);
      BlockPos bandeja = this.worldPosition.below();
      Vec3 salida = new Vec3(bandeja.getX() + 0.5 + f.getStepX() * 0.62, bandeja.getY() + 0.45, bandeja.getZ() + 0.5 + f.getStepZ() * 0.62);
      List<ConfigTragamonedas.Recompensa> dar = new ArrayList<>();
      if ("una".equals(p.modo)) {
         int total = 0;

         for (ConfigTragamonedas.Recompensa r : p.recompensas) {
            total += Math.max(1, r.peso);
         }

         int tirada = level.random.nextInt(Math.max(1, total));

         for (ConfigTragamonedas.Recompensa r : p.recompensas) {
            tirada -= Math.max(1, r.peso);
            if (tirada < 0) {
               dar.add(r);
               break;
            }
         }
      } else {
         dar.addAll(p.recompensas);
      }

      for (ConfigTragamonedas.Recompensa r : dar) {
         darRecompensa(level, r, salida, f);
      }
   }

   private static void darRecompensa(ServerLevel level, ConfigTragamonedas.Recompensa r, Vec3 salida, Direction f) {
      Identifier rl = Identifier.tryParse(r.item);
      Item item = rl == null ? null : (Item)BuiltInRegistries.ITEM.getOptional(rl).orElse(null);
      if (item != null) {
         int max = Math.max(r.cantidad, r.maximo);
         int n = r.cantidad + (max > r.cantidad ? level.random.nextInt(max - r.cantidad + 1) : 0);
         if (item == Items.ENCHANTED_BOOK) {
            for (int i = 0; i < n; i++) {
               ItemStack libro = libroAlAzar(level, r);
               if (!libro.isEmpty()) {
                  soltar(level, libro, salida, f);
               }
            }
         } else {
            int tope = Math.max(1, new ItemStack(item).getMaxStackSize());

            while (n > 0) {
               int k = Math.min(n, tope);
               soltar(level, new ItemStack(item, k), salida, f);
               n -= k;
            }
         }
      }
   }

   private static ItemStack libroAlAzar(ServerLevel level, ConfigTragamonedas.Recompensa r) {
      Registry<Enchantment> reg = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
      if (r.encantamientos != null && !r.encantamientos.isEmpty()) {
         List<ConfigTragamonedas.Encantamiento> lista = new ArrayList<>(r.encantamientos);

         while (!lista.isEmpty()) {
            ConfigTragamonedas.Encantamiento e = lista.remove(level.random.nextInt(lista.size()));
            Identifier id = Identifier.tryParse(e.id);
            Optional<Reference<Enchantment>> h = id == null ? Optional.empty() : reg.getHolder(ResourceKey.create(Registries.ENCHANTMENT, id));
            if (h.isPresent()) {
               int nivel = e.nivelMin + (e.nivelMax > e.nivelMin ? level.random.nextInt(e.nivelMax - e.nivelMin + 1) : 0);
               return EnchantedBookItem.createForEnchantment(new EnchantmentInstance((Holder)h.get(), nivel));
            }
         }

         return ItemStack.EMPTY;
      } else {
         List<Reference<Enchantment>> todos = new ArrayList<>();

         for (Identifier k : reg.keySet()) {
            reg.getHolder(ResourceKey.create(Registries.ENCHANTMENT, k)).ifPresent(todos::add);
         }

         if (todos.isEmpty()) {
            return ItemStack.EMPTY;
         }

         Reference<Enchantment> e = todos.get(level.random.nextInt(todos.size()));
         int nivel = 1 + level.random.nextInt(Math.max(1, ((Enchantment)e.value()).getMaxLevel()));
         return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(e, nivel));
      }
   }

   private static void soltar(ServerLevel level, ItemStack pila, Vec3 salida, Direction f) {
      ItemEntity e = new ItemEntity(level, salida.x, salida.y, salida.z, pila);
      e.setDeltaMovement(f.getStepX() * 0.12, 0.1, f.getStepZ() * 0.12);
      e.setDefaultPickUpDelay();
      level.addFreshEntity(e);
   }

   private static boolean encaja(String patron, String simbolo) {
      return "*".equals(patron) || patron.equals(simbolo);
   }

   private void sincronizar() {
      this.setChanged();
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registros) {
      super.saveAdditional(tag, registros);
      ListTag lista = new ListTag();

      for (String s : this.caras) {
         lista.add(StringTag.valueOf(s));
      }

      tag.put("Caras", lista);
      tag.putIntArray("Resultado", this.resultado);
      tag.putIntArray("Previo", this.previo);
      tag.putLong("Inicio", this.inicio);
      tag.putBoolean("Pago", this.pagoPendiente);
   }

   protected void loadAdditional(CompoundTag tag, Provider registros) {
      super.loadAdditional(tag, registros);
      if (tag.contains("Caras", 9)) {
         ListTag lista = tag.getList("Caras", 8);

         for (int i = 0; i < 8 && i < lista.size(); i++) {
            this.caras[i] = lista.getString(i);
         }

         this.pilas = null;
         this.iniciado = true;
      }

      int[] pre = tag.getIntArray("Previo");
      if (pre.length == 3) {
         this.previo = pre;
      }

      int[] res = tag.getIntArray("Resultado");
      if (res.length == 3) {
         this.resultado = res;
      }

      if (tag.contains("Inicio")) {
         this.inicio = tag.getLong("Inicio");
      }

      this.pagoPendiente = tag.getBoolean("Pago");
   }

   public CompoundTag getUpdateTag(Provider registros) {
      return this.saveWithoutMetadata(registros);
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public ItemStack[] pilas() {
      if (this.pilas == null) {
         this.pilas = new ItemStack[8];

         for (int i = 0; i < 8; i++) {
            Item item = BuiltInRegistries.ITEM.getOptional(Identifier.parse(this.caras[i])).orElse(Items.BARRIER);
            this.pilas[i] = new ItemStack(item);
         }
      }

      return this.pilas;
   }
}
