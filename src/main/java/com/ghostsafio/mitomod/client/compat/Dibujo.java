package com.ghostsafio.mitomod.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;

/** Ayuda para dibujar geometría propia con el SubmitNodeCollector de 26.3 reutilizando código que usa PoseStack. */
public final class Dibujo {
   private Dibujo() {
   }

   public static PoseStack pila(PoseStack.Pose pose) {
      PoseStack s = new PoseStack();
      s.last().set(pose);
      return s;
   }
}
