package net.ruleta.client;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.ruleta.RuletaMod;
import net.ruleta.client.ui.Texto;

/** Las 8 casillas de la ruleta, en orden horario empezando por la de arriba. */
public enum Casilla {
	REVIIL("rojo", "MOMENTO REVIIL", 0xE0203C, "reviil", true),
	NUTRIA("rosa", "MOMENTO NUTRIA", 0xD02CD0, "nutria", true),
	SANCION("morado", "SANCIÓN", 0x9A2CD8, "sancion", true),
	EVENTO("azul", "Evento Especial", 0x3030C8, "evento", false),
	MISION("cian", "MISIÓN DEL DÍA", 0x3CE0CE, "mision", true),
	ANUNCIO("verde", "ANUNCIO", 0x34E048, "anuncio", true),
	NUEVO_MOB("amarillo", "NUEVO MOB", 0xE6E63C, "mob", true),
	CAMBIO("naranja", "CAMBIO DE DIFICULTAD", 0xEEA632, "cambio", true);

	public final String color;
	public final String nombre;
	public final int rgb;
	public final String id;
	public final boolean fuenteRotulo;
	public final Identifier textura;
	public final Identifier destellos;
	public final Identifier icono;

	Casilla(String color, String nombre, int rgb, String id, boolean fuenteRotulo) {
		this.color = color;
		this.nombre = nombre;
		this.rgb = rgb;
		this.id = id;
		this.fuenteRotulo = fuenteRotulo;
		this.textura = RuletaMod.id("textures/ruleta/casilla_" + color + ".png");
		this.destellos = RuletaMod.id("textures/ruleta/destellos_" + color + ".png");
		this.icono = RuletaMod.id("textures/gui/icono_ruleta_" + color + ".png");
	}

	public Texto rotulo() {
		return fuenteRotulo ? Texto.rotulo(nombre, rgb) : Texto.de(Component.literal(nombre).withColor(rgb));
	}

	public static Casilla porId(String id) {
		for (Casilla c : values()) {
			if (c.id.equals(id)) {
				return c;
			}
		}

		return ANUNCIO;
	}
}
