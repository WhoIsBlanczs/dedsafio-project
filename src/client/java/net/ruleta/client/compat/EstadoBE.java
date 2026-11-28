package net.ruleta.client.compat;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Estado de dibujo que guarda el bloque-entidad y el tick parcial para los renderers portados de 1.21.1. */
public class EstadoBE<T extends BlockEntity> extends BlockEntityRenderState {
   public T be;
   public float pt;
}
