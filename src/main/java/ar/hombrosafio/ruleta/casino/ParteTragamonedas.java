package ar.hombrosafio.ruleta.casino;

import net.minecraft.util.StringRepresentable;

public enum ParteTragamonedas implements StringRepresentable {
	LOWER("lower"),
	MIDDLE("middle"),
	UPPER("upper");

	private final String nombre;

	ParteTragamonedas(String nombre) {
		this.nombre = nombre;
	}

	public int altura() {
		return this.ordinal();
	}

	@Override
	public String getSerializedName() {
		return this.nombre;
	}
}
