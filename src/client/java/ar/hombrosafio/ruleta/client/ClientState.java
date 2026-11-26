package ar.hombrosafio.ruleta.client;

import java.util.ArrayList;
import java.util.List;

/** Datos del servidor que el cliente recuerda. */
final class ClientState {
	static boolean canEdit;
	static List<String> activeChanges = new ArrayList<>();

	static void reset() {
		canEdit = false;
		activeChanges = new ArrayList<>();
	}

	private ClientState() {
	}
}
