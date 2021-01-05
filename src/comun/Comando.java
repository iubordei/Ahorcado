package comun;

import java.util.HashMap;
import java.util.Map;

public enum Comando {
	COMANDO_MOSTRAR_MENU(0),
	COMANDO_MOSTAR_PARTIDAS(1),
	COMANDO_TU_TURNO(2),
	COMANDO_ACTUALIZACION_PARTIDA(3),
	COMANDO_CREAR_PARTIDA(4),
	COMANDO_UNIRSE_PARTIDA(5),
	COMANDO_SALIR(6),
	COMANDO_INTRODUCIR_LETRA(7);
	
	private final int id;
	
	//  Genera un mapa que asocie IDs con comandos.
	private static final Map<Integer, Comando> lookup = new HashMap<>();
    static
    {
        for (Comando comando : Comando.values())
            lookup.put(comando.getID(), comando);
    }
	
	/**
	 * Constructor de clase. Instancia una clase del tipo {@code Comando}
	 * con los argumentos proporcionados.
	 * 
	 * @param id El identificador del comando.
	 */
	Comando(int id) {
        this.id = id;
    }
	
	/**
	 * Retorna el identificador del comando.
	 * 
	 * @return El identificador del comando.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Retorna el comando asociado al identificador proporcionado.
	 * 
	 * @param id Identificador del comando a retornar.
	 * 
	 * @return El comando asociado al identificador proporcionado o
	 *         {@code null} si no hay comandos asociados a dicho
	 *         identificador.
	 */
	public static Comando getComando(int id) {
		return lookup.get(id);
	}
}
