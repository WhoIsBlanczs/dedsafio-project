package net.ruleta.puerta;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PuertaBunkerBE extends BlockEntity {
   public static final int DURACION = 60;
   private boolean alimentada;
   private long cierraEn = -1L;
   private long abreEn = -1L;
   public float progreso;
   public float anterior;
   private boolean iniciada;

   public PuertaBunkerBE(BlockPos pos, BlockState state) {
      super(PuertaBunker.BE.get(), pos, state);
   }

   public static void tickCliente(Level level, BlockPos pos, BlockState state, PuertaBunkerBE be) {
      float meta = state.getValue(PuertaBunkerBloque.OPEN) ? 1.0F : 0.0F;
      if (!be.iniciada) {
         be.iniciada = true;
         be.progreso = meta;
         be.anterior = meta;
      } else {
         be.anterior = be.progreso;
         float paso = 0.016666668F;
         if (be.progreso < meta) {
            be.progreso = Math.min(meta, be.progreso + paso);
         } else if (be.progreso > meta) {
            be.progreso = Math.max(meta, be.progreso - paso);
         }
      }
   }

   public boolean estaAlimentada() {
      return this.alimentada;
   }

   public void setAlimentada(boolean valor) {
      this.alimentada = valor;
      this.setChanged();
   }

   public long cierraEn() {
      return this.cierraEn;
   }

   public void setCierraEn(long v) {
      if (this.cierraEn != v) {
         this.cierraEn = v;
         this.setChanged();
      }
   }

   public long abreEn() {
      return this.abreEn;
   }

   public void setAbreEn(long v) {
      if (this.abreEn != v) {
         this.abreEn = v;
         this.setChanged();
      }
   }

   protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput tag) {
      super.saveAdditional(tag);
      tag.putBoolean("Alimentada", this.alimentada);
      tag.putLong("CierraEn", this.cierraEn);
      tag.putLong("AbreEn", this.abreEn);
   }

   protected void loadAdditional(net.minecraft.world.level.storage.ValueInput tag) {
      super.loadAdditional(tag);
      this.alimentada = tag.getBooleanOr("Alimentada", false);
      this.cierraEn = tag.child("CierraEn").isPresent() ? tag.getLongOr("CierraEn", 0L) : -1L;
      this.abreEn = tag.child("AbreEn").isPresent() ? tag.getLongOr("AbreEn", 0L) : -1L;
   }
}
