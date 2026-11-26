package net.ruleta.client.dedsafio;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterInit;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterRender;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.resources.ResourceLocation;
import net.ruleta.dedsafio.EscenaPayload;

public final class DedsafioCliente {
   private DedsafioCliente() {
   }

   public static void registrar() {
      CoreShaderRegistrationCallback.EVENT.register((CoreShaderRegistrationCallback)ctx -> {
         ctx.register(ResourceLocation.fromNamespaceAndPath("ruleta", "dedsafio_rayos"), DefaultVertexFormat.POSITION_TEX, RayosDedsafio::shader);
         ctx.register(ResourceLocation.fromNamespaceAndPath("ruleta", "dedsafio_agua"), DefaultVertexFormat.POSITION, AguaDedsafio::shader);
         ctx.register(ResourceLocation.fromNamespaceAndPath("ruleta", "dedsafio_espuma"), DefaultVertexFormat.POSITION_TEX, EspumaDedsafio::shader);
         ctx.register(ResourceLocation.fromNamespaceAndPath("ruleta", "dedsafio_ola"), DefaultVertexFormat.POSITION_TEX, OlaCliente::shaderOla);
         ctx.register(ResourceLocation.fromNamespaceAndPath("ruleta", "dedsafio_olavista"), DefaultVertexFormat.POSITION_TEX, OlaCliente::shaderVista);
      });
      ScreenEvents.AFTER_INIT.register((AfterInit)(cliente, pantalla, ancho, alto) -> {
         if (pantalla instanceof ReceivingLevelScreen) {
            ScreenEvents.afterRender(pantalla).register((AfterRender)(p, g, mx, my, dt) -> HudDedsafio.pantallaCarga(g));
         }
      });
      ClientPlayNetworking.registerGlobalReceiver(EscenaPayload.TYPE, (m, ctx) -> {
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
      DimensionRenderingRegistry.registerSkyRenderer(DedsafioCielo.DIMENSION, ctx -> {});
      DimensionRenderingRegistry.registerCloudRenderer(DedsafioCielo.DIMENSION, ctx -> {});
   }
}
