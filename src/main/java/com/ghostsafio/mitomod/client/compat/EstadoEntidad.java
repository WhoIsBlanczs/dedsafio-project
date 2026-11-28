package com.ghostsafio.mitomod.client.compat;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

/** Estado de dibujo que guarda la entidad, para renderers viejos que leían la entidad al dibujar. */
public class EstadoEntidad extends EntityRenderState {
   public Entity entidad;
   public float pt;
}
