package net.ruleta;

import com.supernova.SupernovaMod;
import net.fabricmc.api.ModInitializer;
import net.ruleta.agujero.AgujeroNegro;
import net.ruleta.burbuja.Burbujas;
import net.ruleta.cajero.Cajero;
import net.ruleta.candado.Candados;
import net.ruleta.compat.Enganches;
import net.ruleta.creeperes.Creeperes;
import net.ruleta.creeperrojo.CreeperRojo;
import net.ruleta.estatua.EstatuaNutria;
import net.ruleta.herobrine.Herobrine;
import net.ruleta.meteoritos.MeteoritosMod;
import net.ruleta.mobs.MobsNuevos;
import net.ruleta.nuclear.NuclearCreeperMod;
import net.ruleta.objetos.Objetos;
import net.ruleta.phora.PhoraMod;
import net.ruleta.puerta.PuertaBunker;
import net.ruleta.tragamonedas.Tragamonedas;

/**
 * DEDsafio 4 Ruleta Fan Project, de fan para fans sin fines de lucro (c) 2026 DarkPlayer y colaboradores.
 * Licencia CC BY-NC-SA 4.0. Port a 26.3 solo del catálogo: mobs, entidades, ítems y bloques.
 */
public class RuletaInicio implements ModInitializer {
   @Override
   public void onInitialize() {
      Efectos.init();
      NuclearCreeperMod.init();
      SupernovaMod.init();
      PhoraMod.init();
      MeteoritosMod.init();
      MobsNuevos.init();
      PuertaBunker.init();
      PestanaRuleta.init();
      CreeperRojo.init();
      Creeperes.init();
      AgujeroNegro.init();
      Herobrine.init();
      Objetos.init();
      Candados.init();
      Burbujas.init();
      Tragamonedas.init();
      Cajero.init();
      EstatuaNutria.init();
      Red.registrar();
      Servidor.registrar();
      Enganches.registrar();
   }
}
