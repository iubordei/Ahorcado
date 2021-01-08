package comun;

import java.util.HashMap;
import java.util.Map;

public enum Comando {
	COMANDO_MOSTRAR_MENU(0), COMANDO_MOSTAR_PARTIDAS(1), COMANDO_TU_TURNO(2), COMANDO_ACTUALIZACION_PARTIDA(3),
	COMANDO_CREAR_PARTIDA(4), COMANDO_UNIRSE_PARTIDA(5), COMANDO_SALIR(6), COMANDO_INTRODUCIR_LETRA(7);

	private final int id;

	// Genera un mapa donde se asocian los IDs con los diferentes comandos.
	private static final Map<Integer, Comando> lookup = new HashMap<>();
	static {
		for (Comando comando : Comando.values())
			lookup.put(comando.getID(), comando);
	}

	// PRE:
	// POS: crea un objeto de tipo Comando con id = "id".
	Comando(int id) {
		this.id = id;
	}

	// PRE:
	// POS: devuelve el identificado id del objeto Comando actual.
	public int getID() {
		return id;
	}

	// PRE:
	// POS: devuelve el Comando asociado al identificador id, null si no hay
	// coincidencias.
	public static Comando getComando(int id) {
		return lookup.get(id);
	}
}
