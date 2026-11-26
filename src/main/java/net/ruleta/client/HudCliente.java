package net.ruleta.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.ruleta.ReviilPayload;
import net.ruleta.client.eclipse.EclipseCliente;
import net.ruleta.client.escenas.AnimacionMuerte;
import net.ruleta.client.escenas.MomentoNutria;
import net.ruleta.client.meteoritos.MeteoritosCliente;
import net.ruleta.client.phora.PhoraCliente;
import net.ruleta.client.reviil.AnimacionReviil;
import net.ruleta.client.reviil.CieloReviil;
import net.ruleta.client.ruleta.RuletaHud;

public final class HudCliente {
   private static final SoundEvent CAMPANITA = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("ruleta", "notificacion_mensaje"));

   private HudCliente() {
   }

   public static void recibir(ReviilPayload p) {
      boolean poner = p.valor() >= 0;
      switch (p.pieza()) {
         case "temporizador":
            TemporizadorHud.poner(p.valor());
            break;
         case "aviso":
            if (poner) {
               AvisoHud.poner(p.titulo(), p.aviso());
            } else {
               AvisoHud.quitar();
            }
            break;
         case "mensaje":
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(CAMPANITA, 1.0F, 1.0F));
            break;
         case "cielo":
            CieloReviil.recibir(p.valor());
            break;
         case "cabezazo":
            CieloReviil.cabezazo();
            break;
         case "cielo_pieza":
            CieloReviil.piezaCielo(p.valor());
            break;
         case "grieta_pieza":
            CieloReviil.piezaGrieta(p.valor());
            break;
         case "musica_reviil":
            CieloReviil.musicaSinCielo();
            break;
         case "tema_momento":
            CieloReviil.temaMomento(true);
            break;
         case "reviil_activo":
            LluviaRoja.poner(p.valor() == 1);
            if (p.valor() == 0) {
               CieloReviil.recibir(-1);
            }
            break;
         case "animacion":
            if (OpcionesCliente.animReviil) {
               if (p.valor() == 4) {
                  CieloReviil.pararMusicaSola();
               }

               AnimacionReviil.empezar(p.valor() == 2, p.valor() == 3, p.valor() == 4);
            } else if (p.valor() == 4) {
               CieloReviil.temaMomento(true);
            } else if (p.valor() == 2) {
               CieloReviil.temaAdelantado();
            }
            break;
         case "muerte":
            if (OpcionesCliente.animMuerte) {
               ChatRojo.esperar(p.titulo());
               AnimacionMuerte.empezar(p.titulo());
            }
            break;
         case "nutria":
            if (OpcionesCliente.nutria) {
               MomentoNutria.empezar();
            }
            break;
         case "abrir_cambios":
            Minecraft.getInstance().setScreenAndShow(new PantallaCambiosActivos());
            break;
         case "eclipse":
            EclipseCliente.recibir(p.valor(), p.titulo().getString());
            break;
         case "phora":
            PhoraCliente.recibir(p.valor());
            break;
         case "phora_voz":
            PhoraCliente.voz();
            break;
         case "meteoritos":
            MeteoritosCliente.recibir(p.valor());
            break;
         case "meteorito_impacto":
            MeteoritosCliente.impacto(p.valor(), p.titulo().getString());
            break;
         case "impacto":
            PhoraCliente.impacto(p.valor(), p.titulo().getString());
            break;
         case "momentos":
            MomentosCliente.poner(p.titulo().getString(), p.aviso().getString());
            break;
         case "parar":
            parar();
      }
   }

   public static void parar() {
      RuletaHud.parar();
      AnimacionReviil.parar();
      CieloReviil.pararMusicaSola();
      MomentoNutria.parar();
      TemporizadorHud.poner(-1);
      AvisoHud.quitar();
   }

   public static void limpiar() {
      AnimacionReviil.parar();
      MomentoNutria.parar();
      AnimacionMuerte.parar();
      EclipseCliente.limpiar();
      PhoraCliente.limpiar();
      MeteoritosCliente.limpiar();
      MomentosCliente.limpiar();
      TemporizadorHud.limpiar();
      AvisoHud.limpiar();
   }

   static boolean tapado() {
      return RuletaHud.girando();
   }

   public static void render(GuiGraphicsExtractor ctx) {
      Font tr = Minecraft.getInstance().font;
      if (Minecraft.getInstance().player != null && !Minecraft.getInstance().options.hideGui) {
         AvisoHud.coordenadas(ctx, tr);
      }

      TemporizadorHud.render(ctx, tr);
      AvisoHud.render(ctx, tr);
   }
}
