package net.owleafstudio.modminds.init;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.owleafstudio.modminds.block.AceroDeLimboBlock;
import net.owleafstudio.modminds.block.AdobePulidoBlock;
import net.owleafstudio.modminds.block.AguaDelLimboBlock;
import net.owleafstudio.modminds.block.AquaStoneBlock;
import net.owleafstudio.modminds.block.BaldeConPinturaAzulBlock;
import net.owleafstudio.modminds.block.BaldeConPinturaBlancaBlock;
import net.owleafstudio.modminds.block.BaldeLlenoBlock;
import net.owleafstudio.modminds.block.BaldeVacioBlock;
import net.owleafstudio.modminds.block.BarricadaDeSeguridadBlock;
import net.owleafstudio.modminds.block.BarsBlockBlock;
import net.owleafstudio.modminds.block.BloqueAzulDeRingBlock;
import net.owleafstudio.modminds.block.BloqueBorgAnimadoNucleo1Block;
import net.owleafstudio.modminds.block.BloqueBorgBlock;
import net.owleafstudio.modminds.block.BloqueBorgChiseladoBlock;
import net.owleafstudio.modminds.block.BloqueBorgCinceladoBlock;
import net.owleafstudio.modminds.block.BloqueBorgConPatronBlock;
import net.owleafstudio.modminds.block.BloqueBorgGrisConNaranjaBlock;
import net.owleafstudio.modminds.block.BloqueBorgPanelesMagentasBlock;
import net.owleafstudio.modminds.block.BloqueBorgVariante10Block;
import net.owleafstudio.modminds.block.BloqueBorgVariante11Block;
import net.owleafstudio.modminds.block.BloqueBorgVariante12Block;
import net.owleafstudio.modminds.block.BloqueBorgVariante13Block;
import net.owleafstudio.modminds.block.BloqueBorgVariante14Block;
import net.owleafstudio.modminds.block.BloqueBorgVariante15Block;
import net.owleafstudio.modminds.block.BloqueBorgVariante16Block;
import net.owleafstudio.modminds.block.BloqueDeCoralNaranjaLuminosoBlock;
import net.owleafstudio.modminds.block.BloqueDeCoralPurpuraLuminosoBlock;
import net.owleafstudio.modminds.block.BloqueDeCoralRosadoLuminosoBlock;
import net.owleafstudio.modminds.block.BloqueDeHierroReforzadoBlock;
import net.owleafstudio.modminds.block.BloqueDeIslasDeEspacioBlock;
import net.owleafstudio.modminds.block.BloqueDeLimboLuminosoBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesHorizontalesAmarilloBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesHorizontalesAzulBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesHorizontalesAzulClaroBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesHorizontalesBlancoBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesHorizontalesCianBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesHorizontalesMagentaBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesHorizontalesNaranjaBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesHorizontalesPurpuraBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesHorizontalesRojoBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesHorizontalesRosaBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesHorizontalesVerdeBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesHorizontalesVerdeLimaBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesVerticalesAmarilloBlock;
import net.owleafstudio.modminds.block.BloqueDeLucesVerticalesRojoBlock;
import net.owleafstudio.modminds.block.BloqueDeLuzVerticaCianBlock;
import net.owleafstudio.modminds.block.BloqueDeLuzVerticalAzulBlock;
import net.owleafstudio.modminds.block.BloqueDeLuzVerticalAzulClaroBlock;
import net.owleafstudio.modminds.block.BloqueDeLuzVerticalBlancoBlock;
import net.owleafstudio.modminds.block.BloqueDeLuzVerticalMagentaBlock;
import net.owleafstudio.modminds.block.BloqueDeLuzVerticalNaranjaBlock;
import net.owleafstudio.modminds.block.BloqueDeLuzVerticalPurpuraBlock;
import net.owleafstudio.modminds.block.BloqueDeLuzVerticalRosaBlock;
import net.owleafstudio.modminds.block.BloqueDeLuzVerticalVerdeBlock;
import net.owleafstudio.modminds.block.BloqueDeLuzVerticalVerdeLimaBlock;
import net.owleafstudio.modminds.block.BloqueDeNereidaBlock;
import net.owleafstudio.modminds.block.BloqueDePisoBorgDivididoVerticalBlock;
import net.owleafstudio.modminds.block.BloqueDeRejillasRotasBlock;
import net.owleafstudio.modminds.block.BloqueDeRestaurantrBlock;
import net.owleafstudio.modminds.block.BloqueDeTuberiaBorgBlock;
import net.owleafstudio.modminds.block.BloqueGulagFinal2Block;
import net.owleafstudio.modminds.block.BloqueGulagFinal3Block;
import net.owleafstudio.modminds.block.BloqueGulagFinalBlock;
import net.owleafstudio.modminds.block.BloqueIluminanteAmarilloBlock;
import net.owleafstudio.modminds.block.BloqueIluminanteAzulBlock;
import net.owleafstudio.modminds.block.BloqueIluminanteAzulClaroBlock;
import net.owleafstudio.modminds.block.BloqueIluminanteBorgNaranjaBlock;
import net.owleafstudio.modminds.block.BloqueIluminanteCianBlock;
import net.owleafstudio.modminds.block.BloqueIluminanteMagentaBlock;
import net.owleafstudio.modminds.block.BloqueIluminanteNaranjaBlock;
import net.owleafstudio.modminds.block.BloqueIluminantePurpuraBlock;
import net.owleafstudio.modminds.block.BloqueIluminanteRojoBlock;
import net.owleafstudio.modminds.block.BloqueIluminanteRosaBlock;
import net.owleafstudio.modminds.block.BloqueIluminanteVerdeBlock;
import net.owleafstudio.modminds.block.BloqueIluminanteVerdeLirmaBlock;
import net.owleafstudio.modminds.block.BloqueNegroDeRingBlock;
import net.owleafstudio.modminds.block.BloquePulidoBlock;
import net.owleafstudio.modminds.block.BloqueRojoDeRingBlock;
import net.owleafstudio.modminds.block.BloqueRuletaAmarilloBlock;
import net.owleafstudio.modminds.block.BloqueRuletaAzulBlock;
import net.owleafstudio.modminds.block.BloqueRuletaAzulGlowingBlock;
import net.owleafstudio.modminds.block.BloqueRuletaCyanBlock;
import net.owleafstudio.modminds.block.BloqueRuletaGlowingAmarilloBlock;
import net.owleafstudio.modminds.block.BloqueRuletaGlowingCyanBlock;
import net.owleafstudio.modminds.block.BloqueRuletaGlowingMoradaBlock;
import net.owleafstudio.modminds.block.BloqueRuletaGlowingNaranjaBlock;
import net.owleafstudio.modminds.block.BloqueRuletaGlowingRojoBlock;
import net.owleafstudio.modminds.block.BloqueRuletaGlowingRosaBlock;
import net.owleafstudio.modminds.block.BloqueRuletaGlowingVerdeBlock;
import net.owleafstudio.modminds.block.BloqueRuletaMoradoBlock;
import net.owleafstudio.modminds.block.BloqueRuletaNaranjaBlock;
import net.owleafstudio.modminds.block.BloqueRuletaRojoBlock;
import net.owleafstudio.modminds.block.BloqueRuletaRosaBlock;
import net.owleafstudio.modminds.block.BloqueRuletaVerdeBlock;
import net.owleafstudio.modminds.block.BloquesBorgConLucesRojasBlock;
import net.owleafstudio.modminds.block.BloquesBorgConLucesVerdesBlock;
import net.owleafstudio.modminds.block.BlueStoneBlock;
import net.owleafstudio.modminds.block.CarmesiPulidoBlock;
import net.owleafstudio.modminds.block.CintaDeSeguridadBlock;
import net.owleafstudio.modminds.block.ConoDeSeguridadBlock;
import net.owleafstudio.modminds.block.ConoGordoBlock;
import net.owleafstudio.modminds.block.CrimsonTalladoBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioAmarilloBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioAnaranjadoBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioAzulBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioAzulClaroBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioBlancoBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioCianBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioIndestructibleBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioMagentaBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioMoradoBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioRojoBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioRosadoBlock;
import net.owleafstudio.modminds.block.CristalDeDilitioVerdeBlock;
import net.owleafstudio.modminds.block.CristalDeTemploNegroBlock;
import net.owleafstudio.modminds.block.CristalReforzadoBlock;
import net.owleafstudio.modminds.block.CristalVerdeEspacialBlock;
import net.owleafstudio.modminds.block.CuatroTablasDeMaderaBlock;
import net.owleafstudio.modminds.block.CuerdaBlock;
import net.owleafstudio.modminds.block.DosPalasAcostadasBlock;
import net.owleafstudio.modminds.block.DosTablasDeMaderaBlock;
import net.owleafstudio.modminds.block.EscaleraDeBloqueIluminanteVerdeLimaBlock;
import net.owleafstudio.modminds.block.EscaleraDeCoralPurpuraLuminosoBlock;
import net.owleafstudio.modminds.block.EscaleraDeCoralRosadoLuminosoBlock;
import net.owleafstudio.modminds.block.EscaleraDeCristalDeDilitioRosadoBlock;
import net.owleafstudio.modminds.block.EscaleraDeCristalDeTemploNegroBlock;
import net.owleafstudio.modminds.block.EscaleraDeLuzVerticaCianBlock;
import net.owleafstudio.modminds.block.EscaleraDeLuzVerticalAzulBlock;
import net.owleafstudio.modminds.block.EscaleraDeLuzVerticalAzulClaroBlock;
import net.owleafstudio.modminds.block.EscaleraDeLuzVerticalBlancoBlock;
import net.owleafstudio.modminds.block.EscaleraDeMaderaCarmesiPulidaBlock;
import net.owleafstudio.modminds.block.EscaleraDePisoBorgDivididoVerticalBlock;
import net.owleafstudio.modminds.block.EscaleraMaderaDistorsionadaPulidaBlock;
import net.owleafstudio.modminds.block.EscalerasBloquesBorgConLucesRojasBlock;
import net.owleafstudio.modminds.block.EscalerasBorgConLucesVerdesBlock;
import net.owleafstudio.modminds.block.EscalerasDeAbobePulidoBlock;
import net.owleafstudio.modminds.block.EscalerasDeAquaStoneBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueBorgBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueBorgChiseladoBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueBorgCinceladoBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueBorgPanelesMagentasBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueIluminanteAmarilloBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueIluminanteAzulBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueIluminanteAzulClaroBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueIluminanteCianBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueIluminanteMagentaBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueIluminanteNaranjaBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueIluminantePurpuraBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueIluminanteRojoBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueIluminanteRosaBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloqueIluminanteVerdeBlock;
import net.owleafstudio.modminds.block.EscalerasDeBloquePulidoBlock;
import net.owleafstudio.modminds.block.EscalerasDeBlueStoneBlock;
import net.owleafstudio.modminds.block.EscalerasDeBorgAnimadoNucleo1Block;
import net.owleafstudio.modminds.block.EscalerasDeBorgVariante10Block;
import net.owleafstudio.modminds.block.EscalerasDeCoralPurpuraLuminosoBlock;
import net.owleafstudio.modminds.block.EscalerasDeCristalDeDilitioAmarilloBlock;
import net.owleafstudio.modminds.block.EscalerasDeCristalDeDilitioAnaranjadoBlock;
import net.owleafstudio.modminds.block.EscalerasDeCristalDeDilitioAzulBlock;
import net.owleafstudio.modminds.block.EscalerasDeCristalDeDilitioAzulClaroBlock;
import net.owleafstudio.modminds.block.EscalerasDeCristalDeDilitioCianBlock;
import net.owleafstudio.modminds.block.EscalerasDeCristalDeDilitioIndestructibleBlock;
import net.owleafstudio.modminds.block.EscalerasDeCristalDeDilitioMagentaBlock;
import net.owleafstudio.modminds.block.EscalerasDeCristalDeDilitioMoradoBlock;
import net.owleafstudio.modminds.block.EscalerasDeCristalDeDilitioRojoBlock;
import net.owleafstudio.modminds.block.EscalerasDeCristalDeDilitioVerdeBlock;
import net.owleafstudio.modminds.block.EscalerasDeCritalDeDilitioBlancoBlock;
import net.owleafstudio.modminds.block.EscalerasDeHenoDeTikiBlock;
import net.owleafstudio.modminds.block.EscalerasDeLadrilloBorgBlock;
import net.owleafstudio.modminds.block.EscalerasDeLadrilloDeFortalezaCinceladoBlock;
import net.owleafstudio.modminds.block.EscalerasDeLadrilloDeMansionBlock;
import net.owleafstudio.modminds.block.EscalerasDeLadrilloDePisoBorgBlock;
import net.owleafstudio.modminds.block.EscalerasDeLadrilloPulidoDeMansionBlock;
import net.owleafstudio.modminds.block.EscalerasDeLadrillosDeAdobeAntiguosBlock;
import net.owleafstudio.modminds.block.EscalerasDeLadrillosDeAdobeCinceladaBlock;
import net.owleafstudio.modminds.block.EscalerasDeLadrillosDeFortalezaBlock;
import net.owleafstudio.modminds.block.EscalerasDeLadrillosDeMagmaBlock;
import net.owleafstudio.modminds.block.EscalerasDeLadrillosNegrosAgrietadosBlock;
import net.owleafstudio.modminds.block.EscalerasDeLimboLuminosoBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesHorizontalePurpuraBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesHorizontalesAmarilloBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesHorizontalesAzulBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesHorizontalesAzulClaroBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesHorizontalesBlancoBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesHorizontalesCianBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesHorizontalesMagentaBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesHorizontalesNaranjaBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesHorizontalesRojoBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesHorizontalesRosaBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesHorizontalesVerdeBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesHorizontalesVerdeLimaBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesVerticalesAmarilloBlock;
import net.owleafstudio.modminds.block.EscalerasDeLucesVerticalesRojoBlock;
import net.owleafstudio.modminds.block.EscalerasDeLuzVerticalMagentaBlock;
import net.owleafstudio.modminds.block.EscalerasDeLuzVerticalNaranjaBlock;
import net.owleafstudio.modminds.block.EscalerasDeLuzVerticalPurpuraBlock;
import net.owleafstudio.modminds.block.EscalerasDeLuzVerticalRosaBlock;
import net.owleafstudio.modminds.block.EscalerasDeLuzVerticalVerdeBlock;
import net.owleafstudio.modminds.block.EscalerasDeLuzVerticalVerdeLimaBlock;
import net.owleafstudio.modminds.block.EscalerasDeParedBorgBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarBorgBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarBorgConLuzVercicalAmarilloBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarBorgConLuzVercicalAzulClaroBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarBorgConLuzVercicalCianBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarBorgConLuzVercicalPurpuraBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarBorgConLuzVercicalRosaBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarBorgConLuzVerticalBlancoBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarBorgConLuzVerticalMagentaBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarBorgConLuzVerticalNaranjaBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarBorgConLuzVerticalRojoBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarBorgConLuzVerticalVerdeBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarBorgConLuzVerticalVerdeLimaBlock;
import net.owleafstudio.modminds.block.EscalerasDePilarDeWarpedBlock;
import net.owleafstudio.modminds.block.EscalerasDePisoBorgBlock;
import net.owleafstudio.modminds.block.EscalerasDeTablonesAmarillosAntiguosBlock;
import net.owleafstudio.modminds.block.EscalerasDeTablonesAntiguosBlock;
import net.owleafstudio.modminds.block.EscalerasDeTablonesMagentasAntiguosBlock;
import net.owleafstudio.modminds.block.EscalerasDeTablonesMarronesAntiguosBlock;
import net.owleafstudio.modminds.block.EscalerasDeTablonesRojosAntiguosBlock;
import net.owleafstudio.modminds.block.EscalerasDeTablonesRosasAntiguosBlock;
import net.owleafstudio.modminds.block.EscalerasDeTablonesVerdesAntiguosBlock;
import net.owleafstudio.modminds.block.EscalerasDeTuberiaBorgBlock;
import net.owleafstudio.modminds.block.EscalerasPilarBorgConLuzVercicalAzulBlock;
import net.owleafstudio.modminds.block.EstatuaCrucificadoBlock;
import net.owleafstudio.modminds.block.EstatuaEchadaBlock;
import net.owleafstudio.modminds.block.EstatuaSentadoBlock;
import net.owleafstudio.modminds.block.HenoDeTikiBlock;
import net.owleafstudio.modminds.block.HenoDeTikiLosaVerticalBlock;
import net.owleafstudio.modminds.block.LadrilloBorgBlock;
import net.owleafstudio.modminds.block.LadrilloDeFortalezaCinceladoBlock;
import net.owleafstudio.modminds.block.LadrilloDeMansionBlock;
import net.owleafstudio.modminds.block.LadrilloDePisoBorgBlock;
import net.owleafstudio.modminds.block.LadrilloPulidoDeMansionBlock;
import net.owleafstudio.modminds.block.LadrillosDeAdobeAntiguosBlock;
import net.owleafstudio.modminds.block.LadrillosDeAdobeCinceladaBlock;
import net.owleafstudio.modminds.block.LadrillosDeFortalezaBlock;
import net.owleafstudio.modminds.block.LadrillosDeMagmaBlock;
import net.owleafstudio.modminds.block.LadrillosNegrosAgrietadosBlock;
import net.owleafstudio.modminds.block.LargeBarsBlockBlock;
import net.owleafstudio.modminds.block.LimboPortalBlock;
import net.owleafstudio.modminds.block.LosaBloquesBorgConLucesRojasBlock;
import net.owleafstudio.modminds.block.LosaBorgConLucesVerdesBlock;
import net.owleafstudio.modminds.block.LosaCristalDeDilitioVerdeBlock;
import net.owleafstudio.modminds.block.LosaDeAdobePulidoBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueBorgBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueBorgChiseladoBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueBorgCinceladoBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueBorgPanelesMagentasBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueDeCoralPurpuraLuminosoBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueIluminanteAmarilloBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueIluminanteAzulBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueIluminanteAzulClaroBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueIluminanteCianBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueIluminanteMagentaBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueIluminanteNaranjaBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueIluminantePurpuraBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueIluminanteRojoBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueIluminanteRosaBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueIluminanteVerdeBlock;
import net.owleafstudio.modminds.block.LosaDeBloqueIluminanteVerdeLimaBlock;
import net.owleafstudio.modminds.block.LosaDeBorgAnimadoNucleo1Block;
import net.owleafstudio.modminds.block.LosaDeBorgVariante10Block;
import net.owleafstudio.modminds.block.LosaDeCoralPurpuraLuminosoBlock;
import net.owleafstudio.modminds.block.LosaDeCoralRosadoLuminosoBlock;
import net.owleafstudio.modminds.block.LosaDeCristalDeDilitioAmarilloBlock;
import net.owleafstudio.modminds.block.LosaDeCristalDeDilitioAnaranjadoBlock;
import net.owleafstudio.modminds.block.LosaDeCristalDeDilitioAzulBlock;
import net.owleafstudio.modminds.block.LosaDeCristalDeDilitioAzulClaroBlock;
import net.owleafstudio.modminds.block.LosaDeCristalDeDilitioCianBlock;
import net.owleafstudio.modminds.block.LosaDeCristalDeDilitioIndestructibleBlock;
import net.owleafstudio.modminds.block.LosaDeCristalDeDilitioMagentaBlock;
import net.owleafstudio.modminds.block.LosaDeCristalDeDilitioMoradoBlock;
import net.owleafstudio.modminds.block.LosaDeCristalDeDilitioRojoBlock;
import net.owleafstudio.modminds.block.LosaDeCristalDeDilitioRosadoBlock;
import net.owleafstudio.modminds.block.LosaDeCristalDeTemploNegroBlock;
import net.owleafstudio.modminds.block.LosaDeCritalDeDilitioBlancoBlock;
import net.owleafstudio.modminds.block.LosaDeHenoDeTikiBlock;
import net.owleafstudio.modminds.block.LosaDeLadrilloBorgBlock;
import net.owleafstudio.modminds.block.LosaDeLadrilloDeFortalezaCinceladoBlock;
import net.owleafstudio.modminds.block.LosaDeLadrilloDeMansionBlock;
import net.owleafstudio.modminds.block.LosaDeLadrilloDePisoBorgBlock;
import net.owleafstudio.modminds.block.LosaDeLadrilloPulidoDeMansionBlock;
import net.owleafstudio.modminds.block.LosaDeLadrillosDeAbobeAntiguosBlock;
import net.owleafstudio.modminds.block.LosaDeLadrillosDeAdobeCinceladaBlock;
import net.owleafstudio.modminds.block.LosaDeLadrillosDeFortalezaBlock;
import net.owleafstudio.modminds.block.LosaDeLadrillosDeMagmaBlock;
import net.owleafstudio.modminds.block.LosaDeLadrillosNegrosAgrietadosBlock;
import net.owleafstudio.modminds.block.LosaDeLimboLuminosoBlock;
import net.owleafstudio.modminds.block.LosaDeLucesHorizontalePurpuraBlock;
import net.owleafstudio.modminds.block.LosaDeLucesHorizontalesAmarilloBlock;
import net.owleafstudio.modminds.block.LosaDeLucesHorizontalesAzulBlock;
import net.owleafstudio.modminds.block.LosaDeLucesHorizontalesAzulClaroBlock;
import net.owleafstudio.modminds.block.LosaDeLucesHorizontalesBlancoBlock;
import net.owleafstudio.modminds.block.LosaDeLucesHorizontalesCianBlock;
import net.owleafstudio.modminds.block.LosaDeLucesHorizontalesMagentaBlock;
import net.owleafstudio.modminds.block.LosaDeLucesHorizontalesNaranjaBlock;
import net.owleafstudio.modminds.block.LosaDeLucesHorizontalesRojoBlock;
import net.owleafstudio.modminds.block.LosaDeLucesHorizontalesRosaBlock;
import net.owleafstudio.modminds.block.LosaDeLucesHorizontalesVerdeBlock;
import net.owleafstudio.modminds.block.LosaDeLucesHorizontalesVerdeLimaBlock;
import net.owleafstudio.modminds.block.LosaDeLucesVerticalesAmarilloBlock;
import net.owleafstudio.modminds.block.LosaDeLucesVerticalesRojoBlock;
import net.owleafstudio.modminds.block.LosaDeLuzVerticaCianBlock;
import net.owleafstudio.modminds.block.LosaDeLuzVerticalAzulBlock;
import net.owleafstudio.modminds.block.LosaDeLuzVerticalAzulClaroBlock;
import net.owleafstudio.modminds.block.LosaDeLuzVerticalBlancoBlock;
import net.owleafstudio.modminds.block.LosaDeLuzVerticalMagentaBlock;
import net.owleafstudio.modminds.block.LosaDeLuzVerticalNaranjaBlock;
import net.owleafstudio.modminds.block.LosaDeLuzVerticalPurpuraBlock;
import net.owleafstudio.modminds.block.LosaDeLuzVerticalRosaBlock;
import net.owleafstudio.modminds.block.LosaDeLuzVerticalVerdeBlock;
import net.owleafstudio.modminds.block.LosaDeLuzVerticalVerdeLimaBlock;
import net.owleafstudio.modminds.block.LosaDeMaderaCarmesiPulidaBlock;
import net.owleafstudio.modminds.block.LosaDeParedBorgBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgConLuzVercicalAzulBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgConLuzVercicalAzulClaroBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgConLuzVercicalCianBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgConLuzVercicalPurpuraBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgConLuzVercicalRosaBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgConLuzVercicalVerdeBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgConLuzVerticalBlancoBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgConLuzVerticalMagentaBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgConLuzVerticalNaranjaBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgConLuzVerticalRojoBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgConLuzVerticalVerdeBlock;
import net.owleafstudio.modminds.block.LosaDePilarBorgConLuzVerticalVerdeLimaBlock;
import net.owleafstudio.modminds.block.LosaDePilarDeWarpedBlock;
import net.owleafstudio.modminds.block.LosaDePisoBorgBlock;
import net.owleafstudio.modminds.block.LosaDePisoBorgDivididoVerticalBlock;
import net.owleafstudio.modminds.block.LosaDeTablonesAmarillosAntiguosBlock;
import net.owleafstudio.modminds.block.LosaDeTablonesAntiguosBlock;
import net.owleafstudio.modminds.block.LosaDeTablonesMagentasAntiguosBlock;
import net.owleafstudio.modminds.block.LosaDeTablonesMarronesAntiguosBlock;
import net.owleafstudio.modminds.block.LosaDeTablonesRojosAntiguosBlock;
import net.owleafstudio.modminds.block.LosaDeTablonesRosasAntiguosBlock;
import net.owleafstudio.modminds.block.LosaDeTablonesVerdesAntiguosBlock;
import net.owleafstudio.modminds.block.LosaDeTuberiaBorgBlock;
import net.owleafstudio.modminds.block.LosaMaderaDistorsionadaPulidaBlock;
import net.owleafstudio.modminds.block.LosasDeAquaStoneBlock;
import net.owleafstudio.modminds.block.LosasDeBloquePulidoBlock;
import net.owleafstudio.modminds.block.LosasDeBlueStoneBlock;
import net.owleafstudio.modminds.block.MacetaBlancaBlock;
import net.owleafstudio.modminds.block.MacetaNegraBlock;
import net.owleafstudio.modminds.block.MaderaCarmesiPulidaBlock;
import net.owleafstudio.modminds.block.MaderaDistorsionadaPulidaBlock;
import net.owleafstudio.modminds.block.MansionBlock;
import net.owleafstudio.modminds.block.MineralDeNereidaBlock;
import net.owleafstudio.modminds.block.MounstrosityDimensionPortalBlock;
import net.owleafstudio.modminds.block.MuroDeAquaStoneBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueBorgBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueBorgChiseladoBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueBorgCinceladoBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueIluminanteAmarilloBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueIluminanteAzulBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueIluminanteAzulClaroBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueIluminanteCianBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueIluminanteMagentaBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueIluminanteNaranjaBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueIluminantePurpuraBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueIluminanteRojoBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueIluminanteRosaBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueIluminanteVerdeBlock;
import net.owleafstudio.modminds.block.MuroDeBloqueIluminanteVerdeLimaBlock;
import net.owleafstudio.modminds.block.MuroDeBloquePulidoBlock;
import net.owleafstudio.modminds.block.MuroDeBlueStoneBlock;
import net.owleafstudio.modminds.block.MuroDeBorgVariante10Block;
import net.owleafstudio.modminds.block.MuroDeLadrilloBorgBlock;
import net.owleafstudio.modminds.block.MuroDeLadrillosDeFortalezaBlock;
import net.owleafstudio.modminds.block.MuroDeLadrillosDeFortalezaCinceladoBlock;
import net.owleafstudio.modminds.block.MuroDeLadrillosDeMagmaBlock;
import net.owleafstudio.modminds.block.MuroDeLadrillosNegrosAgrietadosBlock;
import net.owleafstudio.modminds.block.MuroDeNegroDeRingBlock;
import net.owleafstudio.modminds.block.MuroDePilarBorgBlock;
import net.owleafstudio.modminds.block.MuroDePisoBorgDivididoVerticalBlock;
import net.owleafstudio.modminds.block.MuroDeTablonesAmarillosAntiguosBlock;
import net.owleafstudio.modminds.block.MuroDeTablonesAntiguosBlock;
import net.owleafstudio.modminds.block.MuroDeTablonesMagentasAntiguosBlock;
import net.owleafstudio.modminds.block.MuroDeTablonesRojosAntiguosBlock;
import net.owleafstudio.modminds.block.MuroDeTablonesRosasAntiguosBlock;
import net.owleafstudio.modminds.block.MuroDeTablonesVerdesAntiguosBlock;
import net.owleafstudio.modminds.block.PalaAcostadaBlock;
import net.owleafstudio.modminds.block.PapelDeBanoBlock;
import net.owleafstudio.modminds.block.ParedBorgBlock;
import net.owleafstudio.modminds.block.PiedraPulidaDeSueloExtranoBlock;
import net.owleafstudio.modminds.block.PiedraRocosaDelLimboBlock;
import net.owleafstudio.modminds.block.PiedraRosadaDelLimboBlock;
import net.owleafstudio.modminds.block.PilarBorgBlock;
import net.owleafstudio.modminds.block.PilarBorgConLuzVercicalAmarillaBlock;
import net.owleafstudio.modminds.block.PilarBorgConLuzVercicalAzulBlock;
import net.owleafstudio.modminds.block.PilarBorgConLuzVercicalAzulClaroBlock;
import net.owleafstudio.modminds.block.PilarBorgConLuzVercicalCianBlock;
import net.owleafstudio.modminds.block.PilarBorgConLuzVercicalPurpuraBlock;
import net.owleafstudio.modminds.block.PilarBorgConLuzVercicalRosaBlock;
import net.owleafstudio.modminds.block.PilarBorgConLuzVerticalBlancoBlock;
import net.owleafstudio.modminds.block.PilarBorgConLuzVerticalMagentaBlock;
import net.owleafstudio.modminds.block.PilarBorgConLuzVerticalNaranjaBlock;
import net.owleafstudio.modminds.block.PilarBorgConLuzVerticalRojoBlock;
import net.owleafstudio.modminds.block.PilarBorgConLuzVerticalVerdeBlock;
import net.owleafstudio.modminds.block.PilarBorgConLuzVerticalVerdeLimaBlock;
import net.owleafstudio.modminds.block.PilarDeMansionBlock;
import net.owleafstudio.modminds.block.PilarDeWarpedBlock;
import net.owleafstudio.modminds.block.PiscinaDeDilitioBorgBlock;
import net.owleafstudio.modminds.block.PisoBorgBlock;
import net.owleafstudio.modminds.block.PisoEscenarioBlock;
import net.owleafstudio.modminds.block.PisoEscenarioDegradadoBlock;
import net.owleafstudio.modminds.block.PuertaDeVallaDeTablonesAntiguosBlock;
import net.owleafstudio.modminds.block.PuertaDeVallaTablonesAmarillosAntiguosBlock;
import net.owleafstudio.modminds.block.PuertaDeVallaTablonesMagentasAntiguosBlock;
import net.owleafstudio.modminds.block.PuertaDeVallaTablonesMarronesAntiguosBlock;
import net.owleafstudio.modminds.block.PuertaDeVallaTablonesRojosAntiguosBlock;
import net.owleafstudio.modminds.block.PuertaDeVallaTablonesVerdesAntiguosBlock;
import net.owleafstudio.modminds.block.RayoLevitanteBorgBlock;
import net.owleafstudio.modminds.block.RingStairsBlock;
import net.owleafstudio.modminds.block.SeatBlock;
import net.owleafstudio.modminds.block.TablonesAmarillosAntiguosBlock;
import net.owleafstudio.modminds.block.TablonesAmarillosAntiguosLosaVerticalBlock;
import net.owleafstudio.modminds.block.TablonesAntiguosBlock;
import net.owleafstudio.modminds.block.TablonesAntiguosRosasBlock;
import net.owleafstudio.modminds.block.TablonesMagentasAntiguosBlock;
import net.owleafstudio.modminds.block.TablonesMagentasAntiguosLosaVerticalBlock;
import net.owleafstudio.modminds.block.TablonesMarronesAntiguosBlock;
import net.owleafstudio.modminds.block.TablonesMarronesAntiguosLosaVerticalBlock;
import net.owleafstudio.modminds.block.TablonesRojosAntiguosBlock;
import net.owleafstudio.modminds.block.TablonesRojosAntiguosLosaVerticalBlock;
import net.owleafstudio.modminds.block.TablonesRosaAntiguosLosaVerticalBlock;
import net.owleafstudio.modminds.block.TablonesVerdeAntiguosLosaVerticalBlock;
import net.owleafstudio.modminds.block.TablonesVerdesAntiguosBlock;
import net.owleafstudio.modminds.block.TresPalasAcostadasBlock;
import net.owleafstudio.modminds.block.TresTablasDeMaderaBlock;
import net.owleafstudio.modminds.block.TresTablasDeMaderaEntrelazadasBlock;
import net.owleafstudio.modminds.block.TuberiaBorgBlock;
import net.owleafstudio.modminds.block.VallaDeTablonesAmarillosAntiguosBlock;
import net.owleafstudio.modminds.block.VallaDeTablonesAntiguosBlock;
import net.owleafstudio.modminds.block.VallaDeTablonesMagentasAntiguosBlock;
import net.owleafstudio.modminds.block.VallaDeTablonesMarronesAntiguosBlock;
import net.owleafstudio.modminds.block.VallaDeTablonesRojosAntiguosBlock;
import net.owleafstudio.modminds.block.VallaDeTablonesVerdesAntiguosBlock;

public class ModmindsModBlocks {
   public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "modminds");
   public static final RegistryObject<Block> TABLONES_ANTIGUOS = REGISTRY.register("tablones_antiguos", () -> new TablonesAntiguosBlock());
   public static final RegistryObject<Block> LOSA_DE_TABLONES_ANTIGUOS = REGISTRY.register(
      "losa_de_tablones_antiguos", () -> new LosaDeTablonesAntiguosBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_TABLONES_ANTIGUOS = REGISTRY.register(
      "escaleras_de_tablones_antiguos", () -> new EscalerasDeTablonesAntiguosBlock()
   );
   public static final RegistryObject<Block> VALLA_DE_TABLONES_ANTIGUOS = REGISTRY.register(
      "valla_de_tablones_antiguos", () -> new VallaDeTablonesAntiguosBlock()
   );
   public static final RegistryObject<Block> PUERTA_DE_VALLA_DE_TABLONES_ANTIGUOS = REGISTRY.register(
      "puerta_de_valla_de_tablones_antiguos", () -> new PuertaDeVallaDeTablonesAntiguosBlock()
   );
   public static final RegistryObject<Block> TABLONES_AMARILLOS_ANTIGUOS = REGISTRY.register(
      "tablones_amarillos_antiguos", () -> new TablonesAmarillosAntiguosBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_TABLONES_AMARILLOS_ANTIGUOS = REGISTRY.register(
      "losa_de_tablones_amarillos_antiguos", () -> new LosaDeTablonesAmarillosAntiguosBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_TABLONES_AMARILLOS_ANTIGUOS = REGISTRY.register(
      "escaleras_de_tablones_amarillos_antiguos", () -> new EscalerasDeTablonesAmarillosAntiguosBlock()
   );
   public static final RegistryObject<Block> VALLA_DE_TABLONES_AMARILLOS_ANTIGUOS = REGISTRY.register(
      "valla_de_tablones_amarillos_antiguos", () -> new VallaDeTablonesAmarillosAntiguosBlock()
   );
   public static final RegistryObject<Block> PUERTA_DE_VALLA_TABLONES_AMARILLOS_ANTIGUOS = REGISTRY.register(
      "puerta_de_valla_tablones_amarillos_antiguos", () -> new PuertaDeVallaTablonesAmarillosAntiguosBlock()
   );
   public static final RegistryObject<Block> TABLONES_VERDES_ANTIGUOS = REGISTRY.register(
      "tablones_verdes_antiguos", () -> new TablonesVerdesAntiguosBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_TABLONES_VERDES_ANTIGUOS = REGISTRY.register(
      "losa_de_tablones_verdes_antiguos", () -> new LosaDeTablonesVerdesAntiguosBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_TABLONES_VERDES_ANTIGUOS = REGISTRY.register(
      "escaleras_de_tablones_verdes_antiguos", () -> new EscalerasDeTablonesVerdesAntiguosBlock()
   );
   public static final RegistryObject<Block> VALLA_DE_TABLONES_VERDES_ANTIGUOS = REGISTRY.register(
      "valla_de_tablones_verdes_antiguos", () -> new VallaDeTablonesVerdesAntiguosBlock()
   );
   public static final RegistryObject<Block> PUERTA_DE_VALLA_TABLONES_VERDES_ANTIGUOS = REGISTRY.register(
      "puerta_de_valla_tablones_verdes_antiguos", () -> new PuertaDeVallaTablonesVerdesAntiguosBlock()
   );
   public static final RegistryObject<Block> TABLONES_ROJOS_ANTIGUOS = REGISTRY.register("tablones_rojos_antiguos", () -> new TablonesRojosAntiguosBlock());
   public static final RegistryObject<Block> LOSA_DE_TABLONES_ROJOS_ANTIGUOS = REGISTRY.register(
      "losa_de_tablones_rojos_antiguos", () -> new LosaDeTablonesRojosAntiguosBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_TABLONES_ROJOS_ANTIGUOS = REGISTRY.register(
      "escaleras_de_tablones_rojos_antiguos", () -> new EscalerasDeTablonesRojosAntiguosBlock()
   );
   public static final RegistryObject<Block> VALLA_DE_TABLONES_ROJOS_ANTIGUOS = REGISTRY.register(
      "valla_de_tablones_rojos_antiguos", () -> new VallaDeTablonesRojosAntiguosBlock()
   );
   public static final RegistryObject<Block> PUERTA_DE_VALLA_TABLONES_ROJOS_ANTIGUOS = REGISTRY.register(
      "puerta_de_valla_tablones_rojos_antiguos", () -> new PuertaDeVallaTablonesRojosAntiguosBlock()
   );
   public static final RegistryObject<Block> LADRILLOS_NEGROS_AGRIETADOS = REGISTRY.register(
      "ladrillos_negros_agrietados", () -> new LadrillosNegrosAgrietadosBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LADRILLOS_NEGROS_AGRIETADOS = REGISTRY.register(
      "losa_de_ladrillos_negros_agrietados", () -> new LosaDeLadrillosNegrosAgrietadosBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LADRILLOS_NEGROS_AGRIETADOS = REGISTRY.register(
      "escaleras_de_ladrillos_negros_agrietados", () -> new EscalerasDeLadrillosNegrosAgrietadosBlock()
   );
   public static final RegistryObject<Block> LADRILLOS_DE_MAGMA = REGISTRY.register("ladrillos_de_magma", () -> new LadrillosDeMagmaBlock());
   public static final RegistryObject<Block> LOSA_DE_LADRILLOS_DE_MAGMA = REGISTRY.register(
      "losa_de_ladrillos_de_magma", () -> new LosaDeLadrillosDeMagmaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LADRILLOS_DE_MAGMA = REGISTRY.register(
      "escaleras_de_ladrillos_de_magma", () -> new EscalerasDeLadrillosDeMagmaBlock()
   );
   public static final RegistryObject<Block> TABLONES_MAGENTAS_ANTIGUOS = REGISTRY.register(
      "tablones_magentas_antiguos", () -> new TablonesMagentasAntiguosBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_TABLONES_MAGENTAS_ANTIGUOS = REGISTRY.register(
      "losa_de_tablones_magentas_antiguos", () -> new LosaDeTablonesMagentasAntiguosBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_TABLONES_MAGENTAS_ANTIGUOS = REGISTRY.register(
      "escaleras_de_tablones_magentas_antiguos", () -> new EscalerasDeTablonesMagentasAntiguosBlock()
   );
   public static final RegistryObject<Block> VALLA_DE_TABLONES_MAGENTAS_ANTIGUOS = REGISTRY.register(
      "valla_de_tablones_magentas_antiguos", () -> new VallaDeTablonesMagentasAntiguosBlock()
   );
   public static final RegistryObject<Block> PUERTA_DE_VALLA_TABLONES_MAGENTAS_ANTIGUOS = REGISTRY.register(
      "puerta_de_valla_tablones_magentas_antiguos", () -> new PuertaDeVallaTablonesMagentasAntiguosBlock()
   );
   public static final RegistryObject<Block> TABLONES_MARRONES_ANTIGUOS = REGISTRY.register(
      "tablones_marrones_antiguos", () -> new TablonesMarronesAntiguosBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_TABLONES_MARRONES_ANTIGUOS = REGISTRY.register(
      "losa_de_tablones_marrones_antiguos", () -> new LosaDeTablonesMarronesAntiguosBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_TABLONES_MARRONES_ANTIGUOS = REGISTRY.register(
      "escaleras_de_tablones_marrones_antiguos", () -> new EscalerasDeTablonesMarronesAntiguosBlock()
   );
   public static final RegistryObject<Block> VALLA_DE_TABLONES_MARRONES_ANTIGUOS = REGISTRY.register(
      "valla_de_tablones_marrones_antiguos", () -> new VallaDeTablonesMarronesAntiguosBlock()
   );
   public static final RegistryObject<Block> PUERTA_DE_VALLA_TABLONES_MARRONES_ANTIGUOS = REGISTRY.register(
      "puerta_de_valla_tablones_marrones_antiguos", () -> new PuertaDeVallaTablonesMarronesAntiguosBlock()
   );
   public static final RegistryObject<Block> BARS_BLOCK = REGISTRY.register("bars_block", () -> new BarsBlockBlock());
   public static final RegistryObject<Block> LARGE_BARS_BLOCK = REGISTRY.register("large_bars_block", () -> new LargeBarsBlockBlock());
   public static final RegistryObject<Block> LADRILLOS_DE_FORTALEZA = REGISTRY.register("ladrillos_de_fortaleza", () -> new LadrillosDeFortalezaBlock());
   public static final RegistryObject<Block> LOSA_DE_LADRILLOS_DE_FORTALEZA = REGISTRY.register(
      "losa_de_ladrillos_de_fortaleza", () -> new LosaDeLadrillosDeFortalezaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LADRILLOS_DE_FORTALEZA = REGISTRY.register(
      "escaleras_de_ladrillos_de_fortaleza", () -> new EscalerasDeLadrillosDeFortalezaBlock()
   );
   public static final RegistryObject<Block> MURO_DE_LADRILLOS_DE_FORTALEZA = REGISTRY.register(
      "muro_de_ladrillos_de_fortaleza", () -> new MuroDeLadrillosDeFortalezaBlock()
   );
   public static final RegistryObject<Block> MURO_DE_LADRILLOS_DE_FORTALEZA_CINCELADO = REGISTRY.register(
      "muro_de_ladrillos_de_fortaleza_cincelado", () -> new MuroDeLadrillosDeFortalezaCinceladoBlock()
   );
   public static final RegistryObject<Block> LADRILLO_DE_FORTALEZA_CINCELADO = REGISTRY.register(
      "ladrillo_de_fortaleza_cincelado", () -> new LadrilloDeFortalezaCinceladoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LADRILLO_DE_FORTALEZA_CINCELADO = REGISTRY.register(
      "losa_de_ladrillo_de_fortaleza_cincelado", () -> new LosaDeLadrilloDeFortalezaCinceladoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LADRILLO_DE_FORTALEZA_CINCELADO = REGISTRY.register(
      "escaleras_de_ladrillo_de_fortaleza_cincelado", () -> new EscalerasDeLadrilloDeFortalezaCinceladoBlock()
   );
   public static final RegistryObject<Block> CRISTAL_DE_TEMPLO_NEGRO = REGISTRY.register("cristal_de_templo_negro", () -> new CristalDeTemploNegroBlock());
   public static final RegistryObject<Block> LOSA_DE_CRISTAL_DE_TEMPLO_NEGRO = REGISTRY.register(
      "losa_de_cristal_de_templo_negro", () -> new LosaDeCristalDeTemploNegroBlock()
   );
   public static final RegistryObject<Block> ESCALERA_DE_CRISTAL_DE_TEMPLO_NEGRO = REGISTRY.register(
      "escalera_de_cristal_de_templo_negro", () -> new EscaleraDeCristalDeTemploNegroBlock()
   );
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO = REGISTRY.register("cristal_de_dilitio", () -> new CristalDeDilitioBlock());
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO_ROJO = REGISTRY.register("cristal_de_dilitio_rojo", () -> new CristalDeDilitioRojoBlock());
   public static final RegistryObject<Block> LOSA_DE_CRISTAL_DE_DILITIO_ROJO = REGISTRY.register(
      "losa_de_cristal_de_dilitio_rojo", () -> new LosaDeCristalDeDilitioRojoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_CRISTAL_DE_DILITIO_ROJO = REGISTRY.register(
      "escaleras_de_cristal_de_dilitio_rojo", () -> new EscalerasDeCristalDeDilitioRojoBlock()
   );
   public static final RegistryObject<Block> CRISTAL_REFORZADO = REGISTRY.register("cristal_reforzado", () -> new CristalReforzadoBlock());
   public static final RegistryObject<Block> PISO_ESCENARIO = REGISTRY.register("piso_escenario", () -> new PisoEscenarioBlock());
   public static final RegistryObject<Block> PISO_ESCENARIO_DEGRADADO = REGISTRY.register(
      "piso_escenario_degradado", () -> new PisoEscenarioDegradadoBlock()
   );
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO_MORADO = REGISTRY.register(
      "cristal_de_dilitio_morado", () -> new CristalDeDilitioMoradoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_CRISTAL_DE_DILITIO_MORADO = REGISTRY.register(
      "losa_de_cristal_de_dilitio_morado", () -> new LosaDeCristalDeDilitioMoradoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_CRISTAL_DE_DILITIO_MORADO = REGISTRY.register(
      "escaleras_de_cristal_de_dilitio_morado", () -> new EscalerasDeCristalDeDilitioMoradoBlock()
   );
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO_AZUL = REGISTRY.register("cristal_de_dilitio_azul", () -> new CristalDeDilitioAzulBlock());
   public static final RegistryObject<Block> LOSA_DE_CRISTAL_DE_DILITIO_AZUL = REGISTRY.register(
      "losa_de_cristal_de_dilitio_azul", () -> new LosaDeCristalDeDilitioAzulBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_CRISTAL_DE_DILITIO_AZUL = REGISTRY.register(
      "escaleras_de_cristal_de_dilitio_azul", () -> new EscalerasDeCristalDeDilitioAzulBlock()
   );
   public static final RegistryObject<Block> ESTATUA_ECHADA = REGISTRY.register("estatua_echada", () -> new EstatuaEchadaBlock());
   public static final RegistryObject<Block> ESTATUA_SENTADO = REGISTRY.register("estatua_sentado", () -> new EstatuaSentadoBlock());
   public static final RegistryObject<Block> ESTATUA_CRUCIFICADO = REGISTRY.register("estatua_crucificado", () -> new EstatuaCrucificadoBlock());
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO_CIAN = REGISTRY.register("cristal_de_dilitio_cian", () -> new CristalDeDilitioCianBlock());
   public static final RegistryObject<Block> LOSA_DE_CRISTAL_DE_DILITIO_CIAN = REGISTRY.register(
      "losa_de_cristal_de_dilitio_cian", () -> new LosaDeCristalDeDilitioCianBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_CRISTAL_DE_DILITIO_CIAN = REGISTRY.register(
      "escaleras_de_cristal_de_dilitio_cian", () -> new EscalerasDeCristalDeDilitioCianBlock()
   );
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO_ROSADO = REGISTRY.register(
      "cristal_de_dilitio_rosado", () -> new CristalDeDilitioRosadoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_CRISTAL_DE_DILITIO_ROSADO = REGISTRY.register(
      "losa_de_cristal_de_dilitio_rosado", () -> new LosaDeCristalDeDilitioRosadoBlock()
   );
   public static final RegistryObject<Block> ESCALERA_DE_CRISTAL_DE_DILITIO_ROSADO = REGISTRY.register(
      "escalera_de_cristal_de_dilitio_rosado", () -> new EscaleraDeCristalDeDilitioRosadoBlock()
   );
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO_ANARANJADO = REGISTRY.register(
      "cristal_de_dilitio_anaranjado", () -> new CristalDeDilitioAnaranjadoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_CRISTAL_DE_DILITIO_ANARANJADO = REGISTRY.register(
      "losa_de_cristal_de_dilitio_anaranjado", () -> new LosaDeCristalDeDilitioAnaranjadoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_CRISTAL_DE_DILITIO_ANARANJADO = REGISTRY.register(
      "escaleras_de_cristal_de_dilitio_anaranjado", () -> new EscalerasDeCristalDeDilitioAnaranjadoBlock()
   );
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO_AMARILLO = REGISTRY.register(
      "cristal_de_dilitio_amarillo", () -> new CristalDeDilitioAmarilloBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_CRISTAL_DE_DILITIO_AMARILLO = REGISTRY.register(
      "losa_de_cristal_de_dilitio_amarillo", () -> new LosaDeCristalDeDilitioAmarilloBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_CRISTAL_DE_DILITIO_AMARILLO = REGISTRY.register(
      "escaleras_de_cristal_de_dilitio_amarillo", () -> new EscalerasDeCristalDeDilitioAmarilloBlock()
   );
   public static final RegistryObject<Block> MINERAL_DE_NEREIDA = REGISTRY.register("mineral_de_nereida", () -> new MineralDeNereidaBlock());
   public static final RegistryObject<Block> PISO_BORG = REGISTRY.register("piso_borg", () -> new PisoBorgBlock());
   public static final RegistryObject<Block> LOSA_DE_PISO_BORG = REGISTRY.register("losa_de_piso_borg", () -> new LosaDePisoBorgBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_PISO_BORG = REGISTRY.register("escaleras_de_piso_borg", () -> new EscalerasDePisoBorgBlock());
   public static final RegistryObject<Block> PILAR_BORG = REGISTRY.register("pilar_borg", () -> new PilarBorgBlock());
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG = REGISTRY.register("losa_de_pilar_borg", () -> new LosaDePilarBorgBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_BORG = REGISTRY.register("escaleras_de_pilar_borg", () -> new EscalerasDePilarBorgBlock());
   public static final RegistryObject<Block> PARED_BORG = REGISTRY.register("pared_borg", () -> new ParedBorgBlock());
   public static final RegistryObject<Block> LOSA_DE_PARED_BORG = REGISTRY.register("losa_de_pared_borg", () -> new LosaDeParedBorgBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_PARED_BORG = REGISTRY.register("escaleras_de_pared_borg", () -> new EscalerasDeParedBorgBlock());
   public static final RegistryObject<Block> LADRILLO_DE_PISO_BORG = REGISTRY.register("ladrillo_de_piso_borg", () -> new LadrilloDePisoBorgBlock());
   public static final RegistryObject<Block> LOSA_DE_LADRILLO_DE_PISO_BORG = REGISTRY.register(
      "losa_de_ladrillo_de_piso_borg", () -> new LosaDeLadrilloDePisoBorgBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LADRILLO_DE_PISO_BORG = REGISTRY.register(
      "escaleras_de_ladrillo_de_piso_borg", () -> new EscalerasDeLadrilloDePisoBorgBlock()
   );
   public static final RegistryObject<Block> LADRILLO_BORG = REGISTRY.register("ladrillo_borg", () -> new LadrilloBorgBlock());
   public static final RegistryObject<Block> LOSA_DE_LADRILLO_BORG = REGISTRY.register("losa_de_ladrillo_borg", () -> new LosaDeLadrilloBorgBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_LADRILLO_BORG = REGISTRY.register(
      "escaleras_de_ladrillo_borg", () -> new EscalerasDeLadrilloBorgBlock()
   );
   public static final RegistryObject<Block> BLOQUE_BORG_CINCELADO = REGISTRY.register("bloque_borg_cincelado", () -> new BloqueBorgCinceladoBlock());
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_BORG_CINCELADO = REGISTRY.register(
      "losa_de_bloque_borg_cincelado", () -> new LosaDeBloqueBorgCinceladoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_BORG_CINCELADO = REGISTRY.register(
      "escaleras_de_bloque_borg_cincelado", () -> new EscalerasDeBloqueBorgCinceladoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_BORG = REGISTRY.register("bloque_borg", () -> new BloqueBorgBlock());
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_BORG = REGISTRY.register("losa_de_bloque_borg", () -> new LosaDeBloqueBorgBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_BORG = REGISTRY.register(
      "escaleras_de_bloque_borg", () -> new EscalerasDeBloqueBorgBlock()
   );
   public static final RegistryObject<Block> BARRICADA_DE_SEGURIDAD = REGISTRY.register("barricada_de_seguridad", () -> new BarricadaDeSeguridadBlock());
   public static final RegistryObject<Block> CONO_DE_SEGURIDAD = REGISTRY.register("cono_de_seguridad", () -> new ConoDeSeguridadBlock());
   public static final RegistryObject<Block> BLOQUES_BORG_CON_LUCES_VERDES = REGISTRY.register(
      "bloques_borg_con_luces_verdes", () -> new BloquesBorgConLucesVerdesBlock()
   );
   public static final RegistryObject<Block> BLOQUES_BORG_CON_LUCES_ROJAS = REGISTRY.register(
      "bloques_borg_con_luces_rojas", () -> new BloquesBorgConLucesRojasBlock()
   );
   public static final RegistryObject<Block> BLOQUE_AZUL_DE_RING = REGISTRY.register("bloque_azul_de_ring", () -> new BloqueAzulDeRingBlock());
   public static final RegistryObject<Block> TABLONES_ROSAS_ANTIGUOS = REGISTRY.register("tablones_rosas_antiguos", () -> new TablonesAntiguosRosasBlock());
   public static final RegistryObject<Block> LOSA_DE_TABLONES_ROSAS_ANTIGUOS = REGISTRY.register(
      "losa_de_tablones_rosas_antiguos", () -> new LosaDeTablonesRosasAntiguosBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_TABLONES_ROSAS_ANTIGUOS = REGISTRY.register(
      "escaleras_de_tablones_rosas_antiguos", () -> new EscalerasDeTablonesRosasAntiguosBlock()
   );
   public static final RegistryObject<Block> BLOQUE_NEGRO_DE_RING = REGISTRY.register("bloque_negro_de_ring", () -> new BloqueNegroDeRingBlock());
   public static final RegistryObject<Block> BLOQUE_ROJO_DE_RING = REGISTRY.register("bloque_rojo_de_ring", () -> new BloqueRojoDeRingBlock());
   public static final RegistryObject<Block> HENO_DE_TIKI = REGISTRY.register("heno_de_tiki", () -> new HenoDeTikiBlock());
   public static final RegistryObject<Block> LOSA_DE_HENO_DE_TIKI = REGISTRY.register("losa_de_heno_de_tiki", () -> new LosaDeHenoDeTikiBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_HENO_DE_TIKI = REGISTRY.register(
      "escaleras_de_heno_de_tiki", () -> new EscalerasDeHenoDeTikiBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_CORAL_ROSADO_LUMINOSO = REGISTRY.register(
      "bloque_de_coral_rosado_luminoso", () -> new BloqueDeCoralRosadoLuminosoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_CORAL_ROSADO_LUMINOSO = REGISTRY.register(
      "losa_de_coral_rosado_luminoso", () -> new LosaDeCoralRosadoLuminosoBlock()
   );
   public static final RegistryObject<Block> ESCALERA_DE_CORAL_ROSADO_LUMINOSO = REGISTRY.register(
      "escalera_de_coral_rosado_luminoso", () -> new EscaleraDeCoralRosadoLuminosoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_CORAL_PURPURA_LUMINOSO = REGISTRY.register(
      "bloque_de_coral_purpura_luminoso", () -> new BloqueDeCoralPurpuraLuminosoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_CORAL_PURPURA_LUMINOSO = REGISTRY.register(
      "losa_de_coral_purpura_luminoso", () -> new LosaDeCoralPurpuraLuminosoBlock()
   );
   public static final RegistryObject<Block> ESCALERA_DE_CORAL_PURPURA_LUMINOSO = REGISTRY.register(
      "escalera_de_coral_purpura_luminoso", () -> new EscaleraDeCoralPurpuraLuminosoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_NEREIDA = REGISTRY.register("bloque_de_nereida", () -> new BloqueDeNereidaBlock());
   public static final RegistryObject<Block> CINTA_DE_SEGURIDAD = REGISTRY.register("cinta_de_seguridad", () -> new CintaDeSeguridadBlock());
   public static final RegistryObject<Block> TABLONES_VERDE_ANTIGUOS_LOSA_VERTICAL = REGISTRY.register(
      "tablones_verde_antiguos_losa_vertical", () -> new TablonesVerdeAntiguosLosaVerticalBlock()
   );
   public static final RegistryObject<Block> TABLONES_ROSA_ANTIGUOS_LOSA_VERTICAL = REGISTRY.register(
      "tablones_rosa_antiguos_losa_vertical", () -> new TablonesRosaAntiguosLosaVerticalBlock()
   );
   public static final RegistryObject<Block> TABLONES_AMARILLOS_ANTIGUOS_LOSA_VERTICAL = REGISTRY.register(
      "tablones_amarillos_antiguos_losa_vertical", () -> new TablonesAmarillosAntiguosLosaVerticalBlock()
   );
   public static final RegistryObject<Block> TABLONES_ROJOS_ANTIGUOS_LOSA_VERTICAL = REGISTRY.register(
      "tablones_rojos_antiguos_losa_vertical", () -> new TablonesRojosAntiguosLosaVerticalBlock()
   );
   public static final RegistryObject<Block> TABLONES_MARRONES_ANTIGUOS_LOSA_VERTICAL = REGISTRY.register(
      "tablones_marrones_antiguos_losa_vertical", () -> new TablonesMarronesAntiguosLosaVerticalBlock()
   );
   public static final RegistryObject<Block> TABLONES_MAGENTAS_ANTIGUOS_LOSA_VERTICAL = REGISTRY.register(
      "tablones_magentas_antiguos_losa_vertical", () -> new TablonesMagentasAntiguosLosaVerticalBlock()
   );
   public static final RegistryObject<Block> HENO_DE_TIKI_LOSA_VERTICAL = REGISTRY.register(
      "heno_de_tiki_losa_vertical", () -> new HenoDeTikiLosaVerticalBlock()
   );
   public static final RegistryObject<Block> CRISTAL_VERDE_ESPACIAL = REGISTRY.register("cristal_verde_espacial", () -> new CristalVerdeEspacialBlock());
   public static final RegistryObject<Block> BLOQUE_DE_ISLAS_DE_ESPACIO = REGISTRY.register(
      "bloque_de_islas_de_espacio", () -> new BloqueDeIslasDeEspacioBlock()
   );
   public static final RegistryObject<Block> ACERO_DE_LIMBO = REGISTRY.register("acero_de_limbo", () -> new AceroDeLimboBlock());
   public static final RegistryObject<Block> CRIMSON_TALLADO = REGISTRY.register("crimson_tallado", () -> new CrimsonTalladoBlock());
   public static final RegistryObject<Block> BLOQUE_DE_REJILLAS_ROTAS = REGISTRY.register(
      "bloque_de_rejillas_rotas", () -> new BloqueDeRejillasRotasBlock()
   );
   public static final RegistryObject<Block> PIEDRA_ROSADA_DEL_LIMBO = REGISTRY.register("piedra_rosada_del_limbo", () -> new PiedraRosadaDelLimboBlock());
   public static final RegistryObject<Block> PIEDRA_ROCOSA_DEL_LIMBO = REGISTRY.register("piedra_rocosa_del_limbo", () -> new PiedraRocosaDelLimboBlock());
   public static final RegistryObject<Block> CARMESI_PULIDO = REGISTRY.register("carmesi_pulido", () -> new CarmesiPulidoBlock());
   public static final RegistryObject<Block> PILAR_DE_MANSION = REGISTRY.register("pilar_de_mansion", () -> new PilarDeMansionBlock());
   public static final RegistryObject<Block> LADRILLO_DE_MANSION = REGISTRY.register("ladrillo_de_mansion", () -> new LadrilloDeMansionBlock());
   public static final RegistryObject<Block> PIEDRA_PULIDA_DE_SUELO_EXTRANO = REGISTRY.register(
      "piedra_pulida_de_suelo_extrano", () -> new PiedraPulidaDeSueloExtranoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_BORG_CHISELADO = REGISTRY.register("bloque_borg_chiselado", () -> new BloqueBorgChiseladoBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_BORG_CHISELADO = REGISTRY.register(
      "escaleras_de_bloque_borg_chiselado", () -> new EscalerasDeBloqueBorgChiseladoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_BORG_CHISELADO = REGISTRY.register(
      "losa_de_bloque_borg_chiselado", () -> new LosaDeBloqueBorgChiseladoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_PULIDO = REGISTRY.register("bloque_pulido", () -> new BloquePulidoBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_PULIDO = REGISTRY.register(
      "escaleras_de_bloque_pulido", () -> new EscalerasDeBloquePulidoBlock()
   );
   public static final RegistryObject<Block> LOSAS_DE_BLOQUE_PULIDO = REGISTRY.register("losas_de_bloque_pulido", () -> new LosasDeBloquePulidoBlock());
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO_INDESTRUCTIBLE = REGISTRY.register(
      "cristal_de_dilitio_indestructible", () -> new CristalDeDilitioIndestructibleBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_CRISTAL_DE_DILITIO_INDESTRUCTIBLE = REGISTRY.register(
      "losa_de_cristal_de_dilitio_indestructible", () -> new LosaDeCristalDeDilitioIndestructibleBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_CRISTAL_DE_DILITIO_INDESTRUCTIBLE = REGISTRY.register(
      "escaleras_de_cristal_de_dilitio_indestructible", () -> new EscalerasDeCristalDeDilitioIndestructibleBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_CORAL_NARANJA_LUMINOSO = REGISTRY.register(
      "bloque_de_coral_naranja_luminoso", () -> new BloqueDeCoralNaranjaLuminosoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_DE_CORAL_PURPURA_LUMINOSO = REGISTRY.register(
      "losa_de_bloque_de_coral_purpura_luminoso", () -> new LosaDeBloqueDeCoralPurpuraLuminosoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_CORAL_PURPURA_LUMINOSO = REGISTRY.register(
      "escaleras_de_coral_purpura_luminoso", () -> new EscalerasDeCoralPurpuraLuminosoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LIMBO_LUMINOSO = REGISTRY.register(
      "bloque_de_limbo_luminoso", () -> new BloqueDeLimboLuminosoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LIMBO_LUMINOSO = REGISTRY.register("losa_de_limbo_luminoso", () -> new LosaDeLimboLuminosoBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_LIMBO_LUMINOSO = REGISTRY.register(
      "escaleras_de_limbo_luminoso", () -> new EscalerasDeLimboLuminosoBlock()
   );
   public static final RegistryObject<Block> LADRILLOS_DE_ADOBE_CINCELADA = REGISTRY.register(
      "ladrillos_de_adobe_cincelada", () -> new LadrillosDeAdobeCinceladaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LADRILLOS_DE_ADOBE_CINCELADA = REGISTRY.register(
      "losa_de_ladrillos_de_adobe_cincelada", () -> new LosaDeLadrillosDeAdobeCinceladaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LADRILLOS_DE_ADOBE_CINCELADA = REGISTRY.register(
      "escaleras_de_ladrillos_de_adobe_cincelada", () -> new EscalerasDeLadrillosDeAdobeCinceladaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_HORIZONTALES_ROJO = REGISTRY.register(
      "bloque_de_luces_horizontales_rojo", () -> new BloqueDeLucesHorizontalesRojoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_HORIZONTALES_AMARILLO = REGISTRY.register(
      "bloque_de_luces_horizontales_amarillo", () -> new BloqueDeLucesHorizontalesAmarilloBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_HORIZONTALES_ROJO = REGISTRY.register(
      "losa_de_luces_horizontales_rojo", () -> new LosaDeLucesHorizontalesRojoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_HORIZONTALES_ROJO = REGISTRY.register(
      "escaleras_de_luces_horizontales_rojo", () -> new EscalerasDeLucesHorizontalesRojoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_HORIZONTALES_AMARILLO = REGISTRY.register(
      "losa_de_luces_horizontales_amarillo", () -> new LosaDeLucesHorizontalesAmarilloBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_HORIZONTALES_AMARILLO = REGISTRY.register(
      "escaleras_de_luces_horizontales_amarillo", () -> new EscalerasDeLucesHorizontalesAmarilloBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_VERTICALES_ROJO = REGISTRY.register(
      "bloque_de_luces_verticales_rojo", () -> new BloqueDeLucesVerticalesRojoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_VERTICALES_ROJO = REGISTRY.register(
      "losa_de_luces_verticales_rojo", () -> new LosaDeLucesVerticalesRojoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_VERTICALES_ROJO = REGISTRY.register(
      "escaleras_de_luces_verticales_rojo", () -> new EscalerasDeLucesVerticalesRojoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_VERTICALES_AMARILLO = REGISTRY.register(
      "bloque_de_luces_verticales_amarillo", () -> new BloqueDeLucesVerticalesAmarilloBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_VERTICALES_AMARILLO = REGISTRY.register(
      "losa_de_luces_verticales_amarillo", () -> new LosaDeLucesVerticalesAmarilloBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_VERTICALES_AMARILLO = REGISTRY.register(
      "escaleras_de_luces_verticales_amarillo", () -> new EscalerasDeLucesVerticalesAmarilloBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_TUBERIA_BORG = REGISTRY.register("bloque_de_tuberia_borg", () -> new BloqueDeTuberiaBorgBlock());
   public static final RegistryObject<Block> LOSA_DE_TUBERIA_BORG = REGISTRY.register("losa_de_tuberia_borg", () -> new LosaDeTuberiaBorgBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_TUBERIA_BORG = REGISTRY.register(
      "escaleras_de_tuberia_borg", () -> new EscalerasDeTuberiaBorgBlock()
   );
   public static final RegistryObject<Block> TRES_PALAS_ACOSTADAS = REGISTRY.register("tres_palas_acostadas", () -> new TresPalasAcostadasBlock());
   public static final RegistryObject<Block> CUATRO_TABLAS_DE_MADERA = REGISTRY.register("cuatro_tablas_de_madera", () -> new CuatroTablasDeMaderaBlock());
   public static final RegistryObject<Block> TRES_TABLAS_DE_MADERA_ENTRELAZADAS = REGISTRY.register(
      "tres_tablas_de_madera_entrelazadas", () -> new TresTablasDeMaderaEntrelazadasBlock()
   );
   public static final RegistryObject<Block> DOS_TABLAS_DE_MADERA = REGISTRY.register("dos_tablas_de_madera", () -> new DosTablasDeMaderaBlock());
   public static final RegistryObject<Block> TRES_TABLAS_DE_MADERA = REGISTRY.register("tres_tablas_de_madera", () -> new TresTablasDeMaderaBlock());
   public static final RegistryObject<Block> PAPEL_DE_BANO = REGISTRY.register("papel_de_bano", () -> new PapelDeBanoBlock());
   public static final RegistryObject<Block> PALA_ACOSTADA = REGISTRY.register("pala_acostada", () -> new PalaAcostadaBlock());
   public static final RegistryObject<Block> MACETA_NEGRA = REGISTRY.register("maceta_negra", () -> new MacetaNegraBlock());
   public static final RegistryObject<Block> DOS_PALAS_ACOSTADAS = REGISTRY.register("dos_palas_acostadas", () -> new DosPalasAcostadasBlock());
   public static final RegistryObject<Block> CUERDA = REGISTRY.register("cuerda", () -> new CuerdaBlock());
   public static final RegistryObject<Block> BALDE_VACIO = REGISTRY.register("balde_vacio", () -> new BaldeVacioBlock());
   public static final RegistryObject<Block> BALDE_LLENO = REGISTRY.register("balde_lleno", () -> new BaldeLlenoBlock());
   public static final RegistryObject<Block> BALDE_CON_PINTURA_AZUL = REGISTRY.register("balde_con_pintura_azul", () -> new BaldeConPinturaAzulBlock());
   public static final RegistryObject<Block> BALDE_CON_PINTURA_BLANCA = REGISTRY.register(
      "balde_con_pintura_blanca", () -> new BaldeConPinturaBlancaBlock()
   );
   public static final RegistryObject<Block> MACETA_BLANCA = REGISTRY.register("maceta_blanca", () -> new MacetaBlancaBlock());
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO_AZUL_CLARO = REGISTRY.register(
      "cristal_de_dilitio_azul_claro", () -> new CristalDeDilitioAzulClaroBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_CRISTAL_DE_DILITIO_AZUL_CLARO = REGISTRY.register(
      "losa_de_cristal_de_dilitio_azul_claro", () -> new LosaDeCristalDeDilitioAzulClaroBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_CRISTAL_DE_DILITIO_AZUL_CLARO = REGISTRY.register(
      "escaleras_de_cristal_de_dilitio_azul_claro", () -> new EscalerasDeCristalDeDilitioAzulClaroBlock()
   );
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO_MAGENTA = REGISTRY.register(
      "cristal_de_dilitio_magenta", () -> new CristalDeDilitioMagentaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_CRISTAL_DE_DILITIO_MAGENTA = REGISTRY.register(
      "losa_de_cristal_de_dilitio_magenta", () -> new LosaDeCristalDeDilitioMagentaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_CRISTAL_DE_DILITIO_MAGENTA = REGISTRY.register(
      "escaleras_de_cristal_de_dilitio_magenta", () -> new EscalerasDeCristalDeDilitioMagentaBlock()
   );
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO_VERDE = REGISTRY.register(
      "cristal_de_dilitio_verde", () -> new CristalDeDilitioVerdeBlock()
   );
   public static final RegistryObject<Block> LOSA_CRISTAL_DE_DILITIO_VERDE = REGISTRY.register(
      "losa_cristal_de_dilitio_verde", () -> new LosaCristalDeDilitioVerdeBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_CRISTAL_DE_DILITIO_VERDE = REGISTRY.register(
      "escaleras_de_cristal_de_dilitio_verde", () -> new EscalerasDeCristalDeDilitioVerdeBlock()
   );
   public static final RegistryObject<Block> MURO_DE_LADRILLOS_NEGROS_AGRIETADOS = REGISTRY.register(
      "muro_de_ladrillos_negros_agrietados", () -> new MuroDeLadrillosNegrosAgrietadosBlock()
   );
   public static final RegistryObject<Block> MURO_DE_LADRILLOS_DE_MAGMA = REGISTRY.register(
      "muro_de_ladrillos_de_magma", () -> new MuroDeLadrillosDeMagmaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LADRILLO_DE_MANSION = REGISTRY.register(
      "losa_de_ladrillo_de_mansion", () -> new LosaDeLadrilloDeMansionBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LADRILLO_DE_MANSION = REGISTRY.register(
      "escaleras_de_ladrillo_de_mansion", () -> new EscalerasDeLadrilloDeMansionBlock()
   );
   public static final RegistryObject<Block> ADOBE_PULIDO = REGISTRY.register("adobe_pulido", () -> new AdobePulidoBlock());
   public static final RegistryObject<Block> LOSA_DE_ADOBE_PULIDO = REGISTRY.register("losa_de_adobe_pulido", () -> new LosaDeAdobePulidoBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_ABOBE_PULIDO = REGISTRY.register(
      "escaleras_de_abobe_pulido", () -> new EscalerasDeAbobePulidoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_HORIZONTALES_AZUL_CLARO = REGISTRY.register(
      "bloque_de_luces_horizontales_azul_claro", () -> new BloqueDeLucesHorizontalesAzulClaroBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_HORIZONTALES_AZUL_CLARO = REGISTRY.register(
      "losa_de_luces_horizontales_azul_claro", () -> new LosaDeLucesHorizontalesAzulClaroBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_HORIZONTALES_AZUL_CLARO = REGISTRY.register(
      "escaleras_de_luces_horizontales_azul_claro", () -> new EscalerasDeLucesHorizontalesAzulClaroBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_HORIZONTALES_AZUL = REGISTRY.register(
      "bloque_de_luces_horizontales_azul", () -> new BloqueDeLucesHorizontalesAzulBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_HORIZONTALES_AZUL = REGISTRY.register(
      "losa_de_luces_horizontales_azul", () -> new LosaDeLucesHorizontalesAzulBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_HORIZONTALES_AZUL = REGISTRY.register(
      "escaleras_de_luces_horizontales_azul", () -> new EscalerasDeLucesHorizontalesAzulBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_HORIZONTALES_BLANCO = REGISTRY.register(
      "bloque_de_luces_horizontales_blanco", () -> new BloqueDeLucesHorizontalesBlancoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_HORIZONTALES_BLANCO = REGISTRY.register(
      "losa_de_luces_horizontales_blanco", () -> new LosaDeLucesHorizontalesBlancoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_HORIZONTALES_BLANCO = REGISTRY.register(
      "escaleras_de_luces_horizontales_blanco", () -> new EscalerasDeLucesHorizontalesBlancoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_HORIZONTALES_CIAN = REGISTRY.register(
      "bloque_de_luces_horizontales_cian", () -> new BloqueDeLucesHorizontalesCianBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_HORIZONTALES_CIAN = REGISTRY.register(
      "losa_de_luces_horizontales_cian", () -> new LosaDeLucesHorizontalesCianBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_HORIZONTALES_CIAN = REGISTRY.register(
      "escaleras_de_luces_horizontales_cian", () -> new EscalerasDeLucesHorizontalesCianBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_HORIZONTALES_MAGENTA = REGISTRY.register(
      "bloque_de_luces_horizontales_magenta", () -> new BloqueDeLucesHorizontalesMagentaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_HORIZONTALES_MAGENTA = REGISTRY.register(
      "losa_de_luces_horizontales_magenta", () -> new LosaDeLucesHorizontalesMagentaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_HORIZONTALES_MAGENTA = REGISTRY.register(
      "escaleras_de_luces_horizontales_magenta", () -> new EscalerasDeLucesHorizontalesMagentaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_HORIZONTALES_NARANJA = REGISTRY.register(
      "bloque_de_luces_horizontales_naranja", () -> new BloqueDeLucesHorizontalesNaranjaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_HORIZONTALES_NARANJA = REGISTRY.register(
      "losa_de_luces_horizontales_naranja", () -> new LosaDeLucesHorizontalesNaranjaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_HORIZONTALES_NARANJA = REGISTRY.register(
      "escaleras_de_luces_horizontales_naranja", () -> new EscalerasDeLucesHorizontalesNaranjaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_HORIZONTALES_PURPURA = REGISTRY.register(
      "bloque_de_luces_horizontales_purpura", () -> new BloqueDeLucesHorizontalesPurpuraBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_HORIZONTALES_PURPURA = REGISTRY.register(
      "losa_de_luces_horizontales_purpura", () -> new LosaDeLucesHorizontalePurpuraBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_HORIZONTALES_PURPURA = REGISTRY.register(
      "escaleras_de_luces_horizontales_purpura", () -> new EscalerasDeLucesHorizontalePurpuraBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_HORIZONTALES_ROSA = REGISTRY.register(
      "bloque_de_luces_horizontales_rosa", () -> new BloqueDeLucesHorizontalesRosaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_HORIZONTALES_ROSA = REGISTRY.register(
      "losa_de_luces_horizontales_rosa", () -> new LosaDeLucesHorizontalesRosaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_HORIZONTALES_ROSA = REGISTRY.register(
      "escaleras_de_luces_horizontales_rosa", () -> new EscalerasDeLucesHorizontalesRosaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_HORIZONTALES_VERDE_LIMA = REGISTRY.register(
      "bloque_de_luces_horizontales_verde_lima", () -> new BloqueDeLucesHorizontalesVerdeLimaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_HORIZONTALES_VERDE_LIMA = REGISTRY.register(
      "losa_de_luces_horizontales_verde_lima", () -> new LosaDeLucesHorizontalesVerdeLimaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_HORIZONTALES_VERDE_LIMA = REGISTRY.register(
      "escaleras_de_luces_horizontales_verde_lima", () -> new EscalerasDeLucesHorizontalesVerdeLimaBlock()
   );
   public static final RegistryObject<Block> CRISTAL_DE_DILITIO_BLANCO = REGISTRY.register(
      "cristal_de_dilitio_blanco", () -> new CristalDeDilitioBlancoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_CRITAL_DE_DILITIO_BLANCO = REGISTRY.register(
      "losa_de_crital_de_dilitio_blanco", () -> new LosaDeCritalDeDilitioBlancoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_CRITAL_DE_DILITIO_BLANCO = REGISTRY.register(
      "escaleras_de_crital_de_dilitio_blanco", () -> new EscalerasDeCritalDeDilitioBlancoBlock()
   );
   public static final RegistryObject<Block> MADERA_CARMESI_PULIDA = REGISTRY.register("madera_carmesi_pulida", () -> new MaderaCarmesiPulidaBlock());
   public static final RegistryObject<Block> MADERA_DISTORSIONADA_PULIDA = REGISTRY.register(
      "madera_distorsionada_pulida", () -> new MaderaDistorsionadaPulidaBlock()
   );
   public static final RegistryObject<Block> LADRILLOS_DE_ADOBE_ANTIGUOS = REGISTRY.register(
      "ladrillos_de_adobe_antiguos", () -> new LadrillosDeAdobeAntiguosBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LADRILLOS_DE_ABOBE_ANTIGUOS = REGISTRY.register(
      "losa_de_ladrillos_de_abobe_antiguos", () -> new LosaDeLadrillosDeAbobeAntiguosBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LADRILLOS_DE_ADOBE_ANTIGUOS = REGISTRY.register(
      "escaleras_de_ladrillos_de_adobe_antiguos", () -> new EscalerasDeLadrillosDeAdobeAntiguosBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUCES_HORIZONTALES_VERDE = REGISTRY.register(
      "bloque_de_luces_horizontales_verde", () -> new BloqueDeLucesHorizontalesVerdeBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUCES_HORIZONTALES_VERDE = REGISTRY.register(
      "losa_de_luces_horizontales_verde", () -> new LosaDeLucesHorizontalesVerdeBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUCES_HORIZONTALES_VERDE = REGISTRY.register(
      "escaleras_de_luces_horizontales_verde", () -> new EscalerasDeLucesHorizontalesVerdeBlock()
   );
   public static final RegistryObject<Block> MURO_DE_PILAR_BORG = REGISTRY.register("muro_de_pilar_borg", () -> new MuroDePilarBorgBlock());
   public static final RegistryObject<Block> MURO_DE_BLOQUE_BORG_CHISELADO = REGISTRY.register(
      "muro_de_bloque_borg_chiselado", () -> new MuroDeBloqueBorgChiseladoBlock()
   );
   public static final RegistryObject<Block> MURO_DE_BLOQUE_BORG = REGISTRY.register("muro_de_bloque_borg", () -> new MuroDeBloqueBorgBlock());
   public static final RegistryObject<Block> MURO_DE_BLOQUE_BORG_CINCELADO = REGISTRY.register(
      "muro_de_bloque_borg_cincelado", () -> new MuroDeBloqueBorgCinceladoBlock()
   );
   public static final RegistryObject<Block> LOSA_BORG_CON_LUCES_VERDES = REGISTRY.register(
      "losa_borg_con_luces_verdes", () -> new LosaBorgConLucesVerdesBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_BORG_CON_LUCES_VERDES = REGISTRY.register(
      "escaleras_borg_con_luces_verdes", () -> new EscalerasBorgConLucesVerdesBlock()
   );
   public static final RegistryObject<Block> LOSA_BLOQUES_BORG_CON_LUCES_ROJAS = REGISTRY.register(
      "losa_bloques_borg_con_luces_rojas", () -> new LosaBloquesBorgConLucesRojasBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_BLOQUES_BORG_CON_LUCES_ROJAS = REGISTRY.register(
      "escaleras_bloques_borg_con_luces_rojas", () -> new EscalerasBloquesBorgConLucesRojasBlock()
   );
   public static final RegistryObject<Block> MURO_DE_LADRILLO_BORG = REGISTRY.register("muro_de_ladrillo_borg", () -> new MuroDeLadrilloBorgBlock());
   public static final RegistryObject<Block> MURO_DE_BLOQUE_PULIDO = REGISTRY.register("muro_de_bloque_pulido", () -> new MuroDeBloquePulidoBlock());
   public static final RegistryObject<Block> ESCALERA_DE_MADERA_CARMESI_PULIDA = REGISTRY.register(
      "escalera_de_madera_carmesi_pulida", () -> new EscaleraDeMaderaCarmesiPulidaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_MADERA_CARMESI_PULIDA = REGISTRY.register(
      "losa_de_madera_carmesi_pulida", () -> new LosaDeMaderaCarmesiPulidaBlock()
   );
   public static final RegistryObject<Block> LOSA_MADERA_DISTORSIONADA_PULIDA = REGISTRY.register(
      "losa_madera_distorsionada_pulida", () -> new LosaMaderaDistorsionadaPulidaBlock()
   );
   public static final RegistryObject<Block> ESCALERA_MADERA_DISTORSIONADA_PULIDA = REGISTRY.register(
      "escalera_madera_distorsionada_pulida", () -> new EscaleraMaderaDistorsionadaPulidaBlock()
   );
   public static final RegistryObject<Block> MURO_DE_TABLONES_ANTIGUOS = REGISTRY.register(
      "muro_de_tablones_antiguos", () -> new MuroDeTablonesAntiguosBlock()
   );
   public static final RegistryObject<Block> MURO_DE_TABLONES_AMARILLOS_ANTIGUOS = REGISTRY.register(
      "muro_de_tablones_amarillos_antiguos", () -> new MuroDeTablonesAmarillosAntiguosBlock()
   );
   public static final RegistryObject<Block> MURO_DE_TABLONES_ROJOS_ANTIGUOS = REGISTRY.register(
      "muro_de_tablones_rojos_antiguos", () -> new MuroDeTablonesRojosAntiguosBlock()
   );
   public static final RegistryObject<Block> MURO_DE_TABLONES_VERDES_ANTIGUOS = REGISTRY.register(
      "muro_de_tablones_verdes_antiguos", () -> new MuroDeTablonesVerdesAntiguosBlock()
   );
   public static final RegistryObject<Block> MURO_DE_TABLONES_MAGENTAS_ANTIGUOS = REGISTRY.register(
      "muro_de_tablones_magentas_antiguos", () -> new MuroDeTablonesMagentasAntiguosBlock()
   );
   public static final RegistryObject<Block> MURO_DE_TABLONES_ROSAS_ANTIGUOS = REGISTRY.register(
      "muro_de_tablones_rosas_antiguos", () -> new MuroDeTablonesRosasAntiguosBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUZ_VERTICAL_AZUL = REGISTRY.register(
      "bloque_de_luz_vertical_azul", () -> new BloqueDeLuzVerticalAzulBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUZ_VERTICAL_AZUL = REGISTRY.register(
      "losa_de_luz_vertical_azul", () -> new LosaDeLuzVerticalAzulBlock()
   );
   public static final RegistryObject<Block> ESCALERA_DE_LUZ_VERTICAL_AZUL = REGISTRY.register(
      "escalera_de_luz_vertical_azul", () -> new EscaleraDeLuzVerticalAzulBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUZ_VERTICAL_AZUL_CLARO = REGISTRY.register(
      "bloque_de_luz_vertical_azul_claro", () -> new BloqueDeLuzVerticalAzulClaroBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUZ_VERTICAL_AZUL_CLARO = REGISTRY.register(
      "losa_de_luz_vertical_azul_claro", () -> new LosaDeLuzVerticalAzulClaroBlock()
   );
   public static final RegistryObject<Block> ESCALERA_DE_LUZ_VERTICAL_AZUL_CLARO = REGISTRY.register(
      "escalera_de_luz_vertical_azul_claro", () -> new EscaleraDeLuzVerticalAzulClaroBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUZ_VERTICA_CIAN = REGISTRY.register(
      "bloque_de_luz_vertica_cian", () -> new BloqueDeLuzVerticaCianBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUZ_VERTICA_CIAN = REGISTRY.register(
      "losa_de_luz_vertica_cian", () -> new LosaDeLuzVerticaCianBlock()
   );
   public static final RegistryObject<Block> ESCALERA_DE_LUZ_VERTICA_CIAN = REGISTRY.register(
      "escalera_de_luz_vertica_cian", () -> new EscaleraDeLuzVerticaCianBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUZ_VERTICAL_BLANCO = REGISTRY.register(
      "bloque_de_luz_vertical_blanco", () -> new BloqueDeLuzVerticalBlancoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUZ_VERTICAL_BLANCO = REGISTRY.register(
      "losa_de_luz_vertical_blanco", () -> new LosaDeLuzVerticalBlancoBlock()
   );
   public static final RegistryObject<Block> ESCALERA_DE_LUZ_VERTICAL_BLANCO = REGISTRY.register(
      "escalera_de_luz_vertical_blanco", () -> new EscaleraDeLuzVerticalBlancoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUZ_VERTICAL_MAGENTA = REGISTRY.register(
      "bloque_de_luz_vertical_magenta", () -> new BloqueDeLuzVerticalMagentaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUZ_VERTICAL_MAGENTA = REGISTRY.register(
      "losa_de_luz_vertical_magenta", () -> new LosaDeLuzVerticalMagentaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUZ_VERTICAL_MAGENTA = REGISTRY.register(
      "escaleras_de_luz_vertical_magenta", () -> new EscalerasDeLuzVerticalMagentaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUZ_VERTICAL_NARANJA = REGISTRY.register(
      "bloque_de_luz_vertical_naranja", () -> new BloqueDeLuzVerticalNaranjaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUZ_VERTICAL_NARANJA = REGISTRY.register(
      "losa_de_luz_vertical_naranja", () -> new LosaDeLuzVerticalNaranjaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUZ_VERTICAL_NARANJA = REGISTRY.register(
      "escaleras_de_luz_vertical_naranja", () -> new EscalerasDeLuzVerticalNaranjaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUZ_VERTICAL_PURPURA = REGISTRY.register(
      "bloque_de_luz_vertical_purpura", () -> new BloqueDeLuzVerticalPurpuraBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUZ_VERTICAL_PURPURA = REGISTRY.register(
      "losa_de_luz_vertical_purpura", () -> new LosaDeLuzVerticalPurpuraBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUZ_VERTICAL_PURPURA = REGISTRY.register(
      "escaleras_de_luz_vertical_purpura", () -> new EscalerasDeLuzVerticalPurpuraBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUZ_VERTICAL_ROSA = REGISTRY.register(
      "bloque_de_luz_vertical_rosa", () -> new BloqueDeLuzVerticalRosaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUZ_VERTICAL_ROSA = REGISTRY.register(
      "losa_de_luz_vertical_rosa", () -> new LosaDeLuzVerticalRosaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUZ_VERTICAL_ROSA = REGISTRY.register(
      "escaleras_de_luz_vertical_rosa", () -> new EscalerasDeLuzVerticalRosaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUZ_VERTICAL_VERDE_LIMA = REGISTRY.register(
      "bloque_de_luz_vertical_verde_lima", () -> new BloqueDeLuzVerticalVerdeLimaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUZ_VERTICAL_VERDE_LIMA = REGISTRY.register(
      "losa_de_luz_vertical_verde_lima", () -> new LosaDeLuzVerticalVerdeLimaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUZ_VERTICAL_VERDE_LIMA = REGISTRY.register(
      "escaleras_de_luz_vertical_verde_lima", () -> new EscalerasDeLuzVerticalVerdeLimaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_LUZ_VERTICAL_VERDE = REGISTRY.register(
      "bloque_de_luz_vertical_verde", () -> new BloqueDeLuzVerticalVerdeBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LUZ_VERTICAL_VERDE = REGISTRY.register(
      "losa_de_luz_vertical_verde", () -> new LosaDeLuzVerticalVerdeBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LUZ_VERTICAL_VERDE = REGISTRY.register(
      "escaleras_de_luz_vertical_verde", () -> new EscalerasDeLuzVerticalVerdeBlock()
   );
   public static final RegistryObject<Block> PILAR_BORG_CON_LUZ_VERCICAL_AMARILLA = REGISTRY.register(
      "pilar_borg_con_luz_vercical_amarilla", () -> new PilarBorgConLuzVercicalAmarillaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_VERDE = REGISTRY.register(
      "losa_de_pilar_borg_con_luz_vercical_verde", () -> new LosaDePilarBorgConLuzVercicalVerdeBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_AMARILLO = REGISTRY.register(
      "escaleras_de_pilar_borg_con_luz_vercical_amarillo", () -> new EscalerasDePilarBorgConLuzVercicalAmarilloBlock()
   );
   public static final RegistryObject<Block> PILAR_BORG_CON_LUZ_VERCICAL_AZUL = REGISTRY.register(
      "pilar_borg_con_luz_vercical_azul", () -> new PilarBorgConLuzVercicalAzulBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_AZUL = REGISTRY.register(
      "losa_de_pilar_borg_con_luz_vercical_azul", () -> new LosaDePilarBorgConLuzVercicalAzulBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_PILAR_BORG_CON_LUZ_VERCICAL_AZUL = REGISTRY.register(
      "escaleras_pilar_borg_con_luz_vercical_azul", () -> new EscalerasPilarBorgConLuzVercicalAzulBlock()
   );
   public static final RegistryObject<Block> PILAR_BORG_CON_LUZ_VERCICAL_CIAN = REGISTRY.register(
      "pilar_borg_con_luz_vercical_cian", () -> new PilarBorgConLuzVercicalCianBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_CIAN = REGISTRY.register(
      "losa_de_pilar_borg_con_luz_vercical_cian", () -> new LosaDePilarBorgConLuzVercicalCianBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_CIAN = REGISTRY.register(
      "escaleras_de_pilar_borg_con_luz_vercical_cian", () -> new EscalerasDePilarBorgConLuzVercicalCianBlock()
   );
   public static final RegistryObject<Block> PILAR_BORG_CON_LUZ_VERCICAL_AZUL_CLARO = REGISTRY.register(
      "pilar_borg_con_luz_vercical_azul_claro", () -> new PilarBorgConLuzVercicalAzulClaroBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_AZUL_CLARO = REGISTRY.register(
      "losa_de_pilar_borg_con_luz_vercical_azul_claro", () -> new LosaDePilarBorgConLuzVercicalAzulClaroBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_AZUL_CLARO = REGISTRY.register(
      "escaleras_de_pilar_borg_con_luz_vercical_azul_claro", () -> new EscalerasDePilarBorgConLuzVercicalAzulClaroBlock()
   );
   public static final RegistryObject<Block> PILAR_BORG_CON_LUZ_VERCICAL_PURPURA = REGISTRY.register(
      "pilar_borg_con_luz_vercical_purpura", () -> new PilarBorgConLuzVercicalPurpuraBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_PURPURA = REGISTRY.register(
      "losa_de_pilar_borg_con_luz_vercical_purpura", () -> new LosaDePilarBorgConLuzVercicalPurpuraBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_PURPURA = REGISTRY.register(
      "escaleras_de_pilar_borg_con_luz_vercical_purpura", () -> new EscalerasDePilarBorgConLuzVercicalPurpuraBlock()
   );
   public static final RegistryObject<Block> PILAR_BORG_CON_LUZ_VERCICAL_ROSA = REGISTRY.register(
      "pilar_borg_con_luz_vercical_rosa", () -> new PilarBorgConLuzVercicalRosaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG_CON_LUZ_VERCICAL_ROSA = REGISTRY.register(
      "losa_de_pilar_borg_con_luz_vercical_rosa", () -> new LosaDePilarBorgConLuzVercicalRosaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERCICAL_ROSA = REGISTRY.register(
      "escaleras_de_pilar_borg_con_luz_vercical_rosa", () -> new EscalerasDePilarBorgConLuzVercicalRosaBlock()
   );
   public static final RegistryObject<Block> PILAR_BORG_CON_LUZ_VERTICAL_MAGENTA = REGISTRY.register(
      "pilar_borg_con_luz_vertical_magenta", () -> new PilarBorgConLuzVerticalMagentaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_MAGENTA = REGISTRY.register(
      "losa_de_pilar_borg_con_luz_vertical_magenta", () -> new LosaDePilarBorgConLuzVerticalMagentaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_MAGENTA = REGISTRY.register(
      "escaleras_de_pilar_borg_con_luz_vertical_magenta", () -> new EscalerasDePilarBorgConLuzVerticalMagentaBlock()
   );
   public static final RegistryObject<Block> PILAR_BORG_CON_LUZ_VERTICAL_BLANCO = REGISTRY.register(
      "pilar_borg_con_luz_vertical_blanco", () -> new PilarBorgConLuzVerticalBlancoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_BLANCO = REGISTRY.register(
      "losa_de_pilar_borg_con_luz_vertical_blanco", () -> new LosaDePilarBorgConLuzVerticalBlancoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_BLANCO = REGISTRY.register(
      "escaleras_de_pilar_borg_con_luz_vertical_blanco", () -> new EscalerasDePilarBorgConLuzVerticalBlancoBlock()
   );
   public static final RegistryObject<Block> PILAR_BORG_CON_LUZ_VERTICAL_NARANJA = REGISTRY.register(
      "pilar_borg_con_luz_vertical_naranja", () -> new PilarBorgConLuzVerticalNaranjaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_NARANJA = REGISTRY.register(
      "losa_de_pilar_borg_con_luz_vertical_naranja", () -> new LosaDePilarBorgConLuzVerticalNaranjaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_NARANJA = REGISTRY.register(
      "escaleras_de_pilar_borg_con_luz_vertical_naranja", () -> new EscalerasDePilarBorgConLuzVerticalNaranjaBlock()
   );
   public static final RegistryObject<Block> PILAR_BORG_CON_LUZ_VERTICAL_ROJO = REGISTRY.register(
      "pilar_borg_con_luz_vertical_rojo", () -> new PilarBorgConLuzVerticalRojoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_ROJO = REGISTRY.register(
      "losa_de_pilar_borg_con_luz_vertical_rojo", () -> new LosaDePilarBorgConLuzVerticalRojoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_ROJO = REGISTRY.register(
      "escaleras_de_pilar_borg_con_luz_vertical_rojo", () -> new EscalerasDePilarBorgConLuzVerticalRojoBlock()
   );
   public static final RegistryObject<Block> PILAR_BORG_CON_LUZ_VERTICAL_VERDE_LIMA = REGISTRY.register(
      "pilar_borg_con_luz_vertical_verde_lima", () -> new PilarBorgConLuzVerticalVerdeLimaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_VERDE_LIMA = REGISTRY.register(
      "losa_de_pilar_borg_con_luz_vertical_verde_lima", () -> new LosaDePilarBorgConLuzVerticalVerdeLimaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_VERDE_LIMA = REGISTRY.register(
      "escaleras_de_pilar_borg_con_luz_vertical_verde_lima", () -> new EscalerasDePilarBorgConLuzVerticalVerdeLimaBlock()
   );
   public static final RegistryObject<Block> PILAR_BORG_CON_LUZ_VERTICAL_VERDE = REGISTRY.register(
      "pilar_borg_con_luz_vertical_verde", () -> new PilarBorgConLuzVerticalVerdeBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PILAR_BORG_CON_LUZ_VERTICAL_VERDE = REGISTRY.register(
      "losa_de_pilar_borg_con_luz_vertical_verde", () -> new LosaDePilarBorgConLuzVerticalVerdeBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_BORG_CON_LUZ_VERTICAL_VERDE = REGISTRY.register(
      "escaleras_de_pilar_borg_con_luz_vertical_verde", () -> new EscalerasDePilarBorgConLuzVerticalVerdeBlock()
   );
   public static final RegistryObject<Block> BLOQUE_ILUMINANTE_AMARILLO = REGISTRY.register(
      "bloque_iluminante_amarillo", () -> new BloqueIluminanteAmarilloBlock()
   );
   public static final RegistryObject<Block> BLOQUE_ILUMINANTE_AZUL = REGISTRY.register("bloque_iluminante_azul", () -> new BloqueIluminanteAzulBlock());
   public static final RegistryObject<Block> BLOQUE_ILUMINANTE_AZUL_CLARO = REGISTRY.register(
      "bloque_iluminante_azul_claro", () -> new BloqueIluminanteAzulClaroBlock()
   );
   public static final RegistryObject<Block> BLOQUE_ILUMINANTE_CIAN = REGISTRY.register("bloque_iluminante_cian", () -> new BloqueIluminanteCianBlock());
   public static final RegistryObject<Block> BLOQUE_ILUMINANTE_MAGENTA = REGISTRY.register(
      "bloque_iluminante_magenta", () -> new BloqueIluminanteMagentaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_ILUMINANTE_NARANJA = REGISTRY.register(
      "bloque_iluminante_naranja", () -> new BloqueIluminanteNaranjaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_ILUMINANTE_PURPURA = REGISTRY.register(
      "bloque_iluminante_purpura", () -> new BloqueIluminantePurpuraBlock()
   );
   public static final RegistryObject<Block> BLOQUE_ILUMINANTE_ROJO = REGISTRY.register("bloque_iluminante_rojo", () -> new BloqueIluminanteRojoBlock());
   public static final RegistryObject<Block> BLOQUE_ILUMINANTE_ROSA = REGISTRY.register("bloque_iluminante_rosa", () -> new BloqueIluminanteRosaBlock());
   public static final RegistryObject<Block> BLOQUE_ILUMINANTE_VERDE = REGISTRY.register("bloque_iluminante_verde", () -> new BloqueIluminanteVerdeBlock());
   public static final RegistryObject<Block> BLOQUE_ILUMINANTE_VERDE_LIRMA = REGISTRY.register(
      "bloque_iluminante_verde_lirma", () -> new BloqueIluminanteVerdeLirmaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_ILUMINANTE_AMARILLO = REGISTRY.register(
      "escaleras_de_bloque_iluminante_amarillo", () -> new EscalerasDeBloqueIluminanteAmarilloBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_ILUMINANTE_AMARILLO = REGISTRY.register(
      "losa_de_bloque_iluminante_amarillo", () -> new LosaDeBloqueIluminanteAmarilloBlock()
   );
   public static final RegistryObject<Block> MURO_DE_BLOQUE_ILUMINANTE_AMARILLO = REGISTRY.register(
      "muro_de_bloque_iluminante_amarillo", () -> new MuroDeBloqueIluminanteAmarilloBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_ILUMINANTE_AZUL = REGISTRY.register(
      "escaleras_de_bloque_iluminante_azul", () -> new EscalerasDeBloqueIluminanteAzulBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_ILUMINANTE_AZUL = REGISTRY.register(
      "losa_de_bloque_iluminante_azul", () -> new LosaDeBloqueIluminanteAzulBlock()
   );
   public static final RegistryObject<Block> MURO_DE_BLOQUE_ILUMINANTE_AZUL = REGISTRY.register(
      "muro_de_bloque_iluminante_azul", () -> new MuroDeBloqueIluminanteAzulBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_ILUMINANTE_AZUL_CLARO = REGISTRY.register(
      "escaleras_de_bloque_iluminante_azul_claro", () -> new EscalerasDeBloqueIluminanteAzulClaroBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_ILUMINANTE_AZUL_CLARO = REGISTRY.register(
      "losa_de_bloque_iluminante_azul_claro", () -> new LosaDeBloqueIluminanteAzulClaroBlock()
   );
   public static final RegistryObject<Block> MURO_DE_BLOQUE_ILUMINANTE_AZUL_CLARO = REGISTRY.register(
      "muro_de_bloque_iluminante_azul_claro", () -> new MuroDeBloqueIluminanteAzulClaroBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_ILUMINANTE_CIAN = REGISTRY.register(
      "escaleras_de_bloque_iluminante_cian", () -> new EscalerasDeBloqueIluminanteCianBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_ILUMINANTE_CIAN = REGISTRY.register(
      "losa_de_bloque_iluminante_cian", () -> new LosaDeBloqueIluminanteCianBlock()
   );
   public static final RegistryObject<Block> MURO_DE_BLOQUE_ILUMINANTE_CIAN = REGISTRY.register(
      "muro_de_bloque_iluminante_cian", () -> new MuroDeBloqueIluminanteCianBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_ILUMINANTE_MAGENTA = REGISTRY.register(
      "escaleras_de_bloque_iluminante_magenta", () -> new EscalerasDeBloqueIluminanteMagentaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_ILUMINANTE_MAGENTA = REGISTRY.register(
      "losa_de_bloque_iluminante_magenta", () -> new LosaDeBloqueIluminanteMagentaBlock()
   );
   public static final RegistryObject<Block> MURO_DE_BLOQUE_ILUMINANTE_MAGENTA = REGISTRY.register(
      "muro_de_bloque_iluminante_magenta", () -> new MuroDeBloqueIluminanteMagentaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_ILUMINANTE_NARANJA = REGISTRY.register(
      "escaleras_de_bloque_iluminante_naranja", () -> new EscalerasDeBloqueIluminanteNaranjaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_ILUMINANTE_NARANJA = REGISTRY.register(
      "losa_de_bloque_iluminante_naranja", () -> new LosaDeBloqueIluminanteNaranjaBlock()
   );
   public static final RegistryObject<Block> MURO_DE_BLOQUE_ILUMINANTE_NARANJA = REGISTRY.register(
      "muro_de_bloque_iluminante_naranja", () -> new MuroDeBloqueIluminanteNaranjaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_ILUMINANTE_PURPURA = REGISTRY.register(
      "escaleras_de_bloque_iluminante_purpura", () -> new EscalerasDeBloqueIluminantePurpuraBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_ILUMINANTE_PURPURA = REGISTRY.register(
      "losa_de_bloque_iluminante_purpura", () -> new LosaDeBloqueIluminantePurpuraBlock()
   );
   public static final RegistryObject<Block> MURO_DE_BLOQUE_ILUMINANTE_PURPURA = REGISTRY.register(
      "muro_de_bloque_iluminante_purpura", () -> new MuroDeBloqueIluminantePurpuraBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_ILUMINANTE_ROJO = REGISTRY.register(
      "escaleras_de_bloque_iluminante_rojo", () -> new EscalerasDeBloqueIluminanteRojoBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_ILUMINANTE_ROJO = REGISTRY.register(
      "losa_de_bloque_iluminante_rojo", () -> new LosaDeBloqueIluminanteRojoBlock()
   );
   public static final RegistryObject<Block> MURO_DE_BLOQUE_ILUMINANTE_ROJO = REGISTRY.register(
      "muro_de_bloque_iluminante_rojo", () -> new MuroDeBloqueIluminanteRojoBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_ILUMINANTE_ROSA = REGISTRY.register(
      "escaleras_de_bloque_iluminante_rosa", () -> new EscalerasDeBloqueIluminanteRosaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_ILUMINANTE_ROSA = REGISTRY.register(
      "losa_de_bloque_iluminante_rosa", () -> new LosaDeBloqueIluminanteRosaBlock()
   );
   public static final RegistryObject<Block> MURO_DE_BLOQUE_ILUMINANTE_ROSA = REGISTRY.register(
      "muro_de_bloque_iluminante_rosa", () -> new MuroDeBloqueIluminanteRosaBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_ILUMINANTE_VERDE = REGISTRY.register(
      "escaleras_de_bloque_iluminante_verde", () -> new EscalerasDeBloqueIluminanteVerdeBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_ILUMINANTE_VERDE = REGISTRY.register(
      "losa_de_bloque_iluminante_verde", () -> new LosaDeBloqueIluminanteVerdeBlock()
   );
   public static final RegistryObject<Block> MURO_DE_BLOQUE_ILUMINANTE_VERDE = REGISTRY.register(
      "muro_de_bloque_iluminante_verde", () -> new MuroDeBloqueIluminanteVerdeBlock()
   );
   public static final RegistryObject<Block> ESCALERA_DE_BLOQUE_ILUMINANTE_VERDE_LIMA = REGISTRY.register(
      "escalera_de_bloque_iluminante_verde_lima", () -> new EscaleraDeBloqueIluminanteVerdeLimaBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_ILUMINANTE_VERDE_LIMA = REGISTRY.register(
      "losa_de_bloque_iluminante_verde_lima", () -> new LosaDeBloqueIluminanteVerdeLimaBlock()
   );
   public static final RegistryObject<Block> MURO_DE_BLOQUE_ILUMINANTE_VERDE_LIMA = REGISTRY.register(
      "muro_de_bloque_iluminante_verde_lima", () -> new MuroDeBloqueIluminanteVerdeLimaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_PISO_BORG_DIVIDIDO_VERTICAL = REGISTRY.register(
      "bloque_de_piso_borg_dividido_vertical", () -> new BloqueDePisoBorgDivididoVerticalBlock()
   );
   public static final RegistryObject<Block> ESCALERA_DE_PISO_BORG_DIVIDIDO_VERTICAL = REGISTRY.register(
      "escalera_de_piso_borg_dividido_vertical", () -> new EscaleraDePisoBorgDivididoVerticalBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_PISO_BORG_DIVIDIDO_VERTICAL = REGISTRY.register(
      "losa_de_piso_borg_dividido_vertical", () -> new LosaDePisoBorgDivididoVerticalBlock()
   );
   public static final RegistryObject<Block> MURO_DE_PISO_BORG_DIVIDIDO_VERTICAL = REGISTRY.register(
      "muro_de_piso_borg_dividido_vertical", () -> new MuroDePisoBorgDivididoVerticalBlock()
   );
   public static final RegistryObject<Block> BLOQUE_DE_HIERRO_REFORZADO = REGISTRY.register(
      "bloque_de_hierro_reforzado", () -> new BloqueDeHierroReforzadoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_BORG_VARIANTE_10 = REGISTRY.register("bloque_borg_variante_10", () -> new BloqueBorgVariante10Block());
   public static final RegistryObject<Block> ESCALERAS_DE_BORG_VARIANTE_10 = REGISTRY.register(
      "escaleras_de_borg_variante_10", () -> new EscalerasDeBorgVariante10Block()
   );
   public static final RegistryObject<Block> LOSA_DE_BORG_VARIANTE_10 = REGISTRY.register(
      "losa_de_borg_variante_10", () -> new LosaDeBorgVariante10Block()
   );
   public static final RegistryObject<Block> MURO_DE_BORG_VARIANTE_10 = REGISTRY.register(
      "muro_de_borg_variante_10", () -> new MuroDeBorgVariante10Block()
   );
   public static final RegistryObject<Block> BLOQUE_BORG_ANIMADO_NUCLEO_1 = REGISTRY.register(
      "bloque_borg_animado_nucleo_1", () -> new BloqueBorgAnimadoNucleo1Block()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BORG_ANIMADO_NUCLEO_1 = REGISTRY.register(
      "escaleras_de_borg_animado_nucleo_1", () -> new EscalerasDeBorgAnimadoNucleo1Block()
   );
   public static final RegistryObject<Block> LOSA_DE_BORG_ANIMADO_NUCLEO_1 = REGISTRY.register(
      "losa_de_borg_animado_nucleo_1", () -> new LosaDeBorgAnimadoNucleo1Block()
   );
   public static final RegistryObject<Block> BLOQUE_BORG_PANELES_MAGENTAS = REGISTRY.register(
      "bloque_borg_paneles_magentas", () -> new BloqueBorgPanelesMagentasBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_BLOQUE_BORG_PANELES_MAGENTAS = REGISTRY.register(
      "escaleras_de_bloque_borg_paneles_magentas", () -> new EscalerasDeBloqueBorgPanelesMagentasBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_BLOQUE_BORG_PANELES_MAGENTAS = REGISTRY.register(
      "losa_de_bloque_borg_paneles_magentas", () -> new LosaDeBloqueBorgPanelesMagentasBlock()
   );
   public static final RegistryObject<Block> MANSION = REGISTRY.register("mansion", () -> new MansionBlock());
   public static final RegistryObject<Block> PILAR_DE_WARPED = REGISTRY.register("pilar_de_warped", () -> new PilarDeWarpedBlock());
   public static final RegistryObject<Block> LOSA_DE_PILAR_DE_WARPED = REGISTRY.register("losa_de_pilar_de_warped", () -> new LosaDePilarDeWarpedBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_PILAR_DE_WARPED = REGISTRY.register(
      "escaleras_de_pilar_de_warped", () -> new EscalerasDePilarDeWarpedBlock()
   );
   public static final RegistryObject<Block> BLOQUE_BORG_VARIANTE_11 = REGISTRY.register("bloque_borg_variante_11", () -> new BloqueBorgVariante11Block());
   public static final RegistryObject<Block> BLOQUE_BORG_VARIANTE_12 = REGISTRY.register("bloque_borg_variante_12", () -> new BloqueBorgVariante12Block());
   public static final RegistryObject<Block> BLOQUE_BORG_VARIANTE_13 = REGISTRY.register("bloque_borg_variante_13", () -> new BloqueBorgVariante13Block());
   public static final RegistryObject<Block> BLOQUE_BORG_VARIANTE_14 = REGISTRY.register("bloque_borg_variante_14", () -> new BloqueBorgVariante14Block());
   public static final RegistryObject<Block> BLOQUE_BORG_VARIANTE_15 = REGISTRY.register("bloque_borg_variante_15", () -> new BloqueBorgVariante15Block());
   public static final RegistryObject<Block> BLOQUE_BORG_VARIANTE_16 = REGISTRY.register("bloque_borg_variante_16", () -> new BloqueBorgVariante16Block());
   public static final RegistryObject<Block> MURO_DE_NEGRO_DE_RING = REGISTRY.register("muro_de_negro_de_ring", () -> new MuroDeNegroDeRingBlock());
   public static final RegistryObject<Block> LADRILLO_PULIDO_DE_MANSION = REGISTRY.register(
      "ladrillo_pulido_de_mansion", () -> new LadrilloPulidoDeMansionBlock()
   );
   public static final RegistryObject<Block> LOSA_DE_LADRILLO_PULIDO_DE_MANSION = REGISTRY.register(
      "losa_de_ladrillo_pulido_de_mansion", () -> new LosaDeLadrilloPulidoDeMansionBlock()
   );
   public static final RegistryObject<Block> ESCALERAS_DE_LADRILLO_PULIDO_DE_MANSION = REGISTRY.register(
      "escaleras_de_ladrillo_pulido_de_mansion", () -> new EscalerasDeLadrilloPulidoDeMansionBlock()
   );
   public static final RegistryObject<Block> AGUA_DEL_LIMBO = REGISTRY.register("agua_del_limbo", () -> new AguaDelLimboBlock());
   public static final RegistryObject<Block> LIMBO_PORTAL = REGISTRY.register("limbo_portal", () -> new LimboPortalBlock());
   public static final RegistryObject<Block> PISCINA_DE_DILITIO_BORG = REGISTRY.register("piscina_de_dilitio_borg", () -> new PiscinaDeDilitioBorgBlock());
   public static final RegistryObject<Block> RAYO_LEVITANTE_BORG = REGISTRY.register("rayo_levitante_borg", () -> new RayoLevitanteBorgBlock());
   public static final RegistryObject<Block> AQUA_STONE = REGISTRY.register("aqua_stone", () -> new AquaStoneBlock());
   public static final RegistryObject<Block> LOSAS_DE_AQUA_STONE = REGISTRY.register("losas_de_aqua_stone", () -> new LosasDeAquaStoneBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_AQUA_STONE = REGISTRY.register("escaleras_de_aqua_stone", () -> new EscalerasDeAquaStoneBlock());
   public static final RegistryObject<Block> MURO_DE_AQUA_STONE = REGISTRY.register("muro_de_aqua_stone", () -> new MuroDeAquaStoneBlock());
   public static final RegistryObject<Block> BLUE_STONE = REGISTRY.register("blue_stone", () -> new BlueStoneBlock());
   public static final RegistryObject<Block> LOSAS_DE_BLUE_STONE = REGISTRY.register("losas_de_blue_stone", () -> new LosasDeBlueStoneBlock());
   public static final RegistryObject<Block> ESCALERAS_DE_BLUE_STONE = REGISTRY.register("escaleras_de_blue_stone", () -> new EscalerasDeBlueStoneBlock());
   public static final RegistryObject<Block> MURO_DE_BLUE_STONE = REGISTRY.register("muro_de_blue_stone", () -> new MuroDeBlueStoneBlock());
   public static final RegistryObject<Block> BLOQUE_DE_RESTAURANTR = REGISTRY.register("bloque_de_restaurantr", () -> new BloqueDeRestaurantrBlock());
   public static final RegistryObject<Block> MOUNSTROSITY_DIMENSION_PORTAL = REGISTRY.register(
      "mounstrosity_dimension_portal", () -> new MounstrosityDimensionPortalBlock()
   );
   public static final RegistryObject<Block> BLOQUE_RULETA_AZUL = REGISTRY.register("bloque_ruleta_azul", () -> new BloqueRuletaAzulBlock());
   public static final RegistryObject<Block> BLOQUE_RULETA_AMARILLO = REGISTRY.register("bloque_ruleta_amarillo", () -> new BloqueRuletaAmarilloBlock());
   public static final RegistryObject<Block> BLOQUE_RULETA_CYAN = REGISTRY.register("bloque_ruleta_cyan", () -> new BloqueRuletaCyanBlock());
   public static final RegistryObject<Block> BLOQUE_RULETA_MORADO = REGISTRY.register("bloque_ruleta_morado", () -> new BloqueRuletaMoradoBlock());
   public static final RegistryObject<Block> BLOQUE_RULETA_NARANJA = REGISTRY.register("bloque_ruleta_naranja", () -> new BloqueRuletaNaranjaBlock());
   public static final RegistryObject<Block> BLOQUE_RULETA_ROJO = REGISTRY.register("bloque_ruleta_rojo", () -> new BloqueRuletaRojoBlock());
   public static final RegistryObject<Block> BLOQUE_RULETA_VERDE = REGISTRY.register("bloque_ruleta_verde", () -> new BloqueRuletaVerdeBlock());
   public static final RegistryObject<Block> BLOQUE_RULETA_ROSA = REGISTRY.register("bloque_ruleta_rosa", () -> new BloqueRuletaRosaBlock());
   public static final RegistryObject<Block> BLOQUE_RULETA_AZUL_GLOWING = REGISTRY.register(
      "bloque_ruleta_azul_glowing", () -> new BloqueRuletaAzulGlowingBlock()
   );
   public static final RegistryObject<Block> BLOQUE_RULETA_GLOWING_AMARILLO = REGISTRY.register(
      "bloque_ruleta_glowing_amarillo", () -> new BloqueRuletaGlowingAmarilloBlock()
   );
   public static final RegistryObject<Block> BLOQUE_RULETA_GLOWING_CYAN = REGISTRY.register(
      "bloque_ruleta_glowing_cyan", () -> new BloqueRuletaGlowingCyanBlock()
   );
   public static final RegistryObject<Block> BLOQUE_RULETA_GLOWING_MORADA = REGISTRY.register(
      "bloque_ruleta_glowing_morada", () -> new BloqueRuletaGlowingMoradaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_RULETA_GLOWING_NARANJA = REGISTRY.register(
      "bloque_ruleta_glowing_naranja", () -> new BloqueRuletaGlowingNaranjaBlock()
   );
   public static final RegistryObject<Block> BLOQUE_RULETA_GLOWING_ROJO = REGISTRY.register(
      "bloque_ruleta_glowing_rojo", () -> new BloqueRuletaGlowingRojoBlock()
   );
   public static final RegistryObject<Block> BLOQUE_RULETA_GLOWING_VERDE = REGISTRY.register(
      "bloque_ruleta_glowing_verde", () -> new BloqueRuletaGlowingVerdeBlock()
   );
   public static final RegistryObject<Block> BLOQUE_RULETA_GLOWING_ROSA = REGISTRY.register(
      "bloque_ruleta_glowing_rosa", () -> new BloqueRuletaGlowingRosaBlock()
   );
   public static final RegistryObject<Block> CONO_GORDO = REGISTRY.register("cono_gordo", () -> new ConoGordoBlock());
   public static final RegistryObject<Block> BLOQUE_GULAG_FINAL = REGISTRY.register("bloque_gulag_final", () -> new BloqueGulagFinalBlock());
   public static final RegistryObject<Block> BLOQUE_GULAG_FINAL_2 = REGISTRY.register("bloque_gulag_final_2", () -> new BloqueGulagFinal2Block());
   public static final RegistryObject<Block> BLOQUE_GULAG_FINAL_3 = REGISTRY.register("bloque_gulag_final_3", () -> new BloqueGulagFinal3Block());
   public static final RegistryObject<Block> SEAT = REGISTRY.register("seat", () -> new SeatBlock());
   public static final RegistryObject<Block> RING_STAIRS = REGISTRY.register("ring_stairs", () -> new RingStairsBlock());
   public static final RegistryObject<Block> BLOQUE_ILUMINANTE_BORG_NARANJA = REGISTRY.register(
      "bloque_iluminante_borg_naranja", () -> new BloqueIluminanteBorgNaranjaBlock()
   );
   public static final RegistryObject<Block> TUBERIA_BORG = REGISTRY.register("tuberia_borg", () -> new TuberiaBorgBlock());
   public static final RegistryObject<Block> BLOQUE_BORG_CON_PATRON = REGISTRY.register("bloque_borg_con_patron", () -> new BloqueBorgConPatronBlock());
   public static final RegistryObject<Block> BLOQUE_BORG_GRIS_CON_NARANJA = REGISTRY.register(
      "bloque_borg_gris_con_naranja", () -> new BloqueBorgGrisConNaranjaBlock()
   );
}
