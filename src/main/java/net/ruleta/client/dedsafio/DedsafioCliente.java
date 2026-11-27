package net.ruleta.client.dedsafio;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterInit;
import net.minecraft.resources.Identifier;
import net.ruleta.dedsafio.EscenaPayload;

public final class DedsafioCliente {
   private DedsafioCliente() {
   }

   public static void registrar() {
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(EscenaPayload.TYPE, (m, ctx) -> {
         if (m.tipo() == 1) {
            EscenaDedsafio.iniciarRemoto(m.transcurrido());
         } else if (m.tipo() == 2) {
            EscenaDedsafio.cerrarOjo();
         } else if (m.tipo() >= 6) {
            OcultarCliente.recibir(m);
         } else if (m.tipo() >= 3) {
            OlaCliente.recibir(m);
         } else {
            EscenaDedsafio.parar();
         }
      });
   }
}
