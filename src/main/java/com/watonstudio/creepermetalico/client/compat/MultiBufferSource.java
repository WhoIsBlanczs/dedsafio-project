package com.watonstudio.creepermetalico.client.compat;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.rendertype.RenderType;

/** MultiBufferSource ya no existe en 26.3: los renderers viejos escriben en esto y Grabadora lo reenvía al colector. */
public interface MultiBufferSource {
   VertexConsumer getBuffer(RenderType tipo);
}
