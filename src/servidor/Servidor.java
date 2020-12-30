package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
	// Constants
	private static final int COMANDO_MOSTRAR_MENU = 0;
	private static final int COMANDO_MOSTAR_PARTIDAS = 1;
	private static final int COMANDO_TU_TURNO = 2;
	private static final int COMANDO_ACTULIZACION_PARTIDA = 3;
	private static final int COMANDO_CREAR_PARTIDA = 4;
	private static final int COMANDO_UNIRSE_PARTIDA = 5;
	private static final int COMANDO_SALIR = 6;
	private static final int COMANDO_INTRODUCIR_LETRA = 7;
	private static final int COMANDO_INTRODUCIR_PALABRA = 8;

	private static List<Partida> partidas = new ArrayList<>();

	public static void main(String[] args) {
		int opcionRecibida;
		try (ServerSocket server = new ServerSocket(10000)) {

			// partidaPrueba sin sockets

			ExecutorService pool = Executors.newCachedThreadPool();
			Jugador j1 = new Jugador("paco", null);
			Jugador j2 = new Jugador("pepe", null);

			Partida partida = new Partida(Palabra.generarPalabra(), null);
			partida.addJugador(j1);
			partida.addJugador(j2);

			partidas.add(partida);
			pool.submit(partida);

			pool.shutdown();

			// Fin partidaPrueba sin sockets

			try (Socket cliente = server.accept(); // Establecemos la conexion con 1 cliente.
					DataInputStream in = new DataInputStream(cliente.getInputStream());
					DataOutputStream out = new DataOutputStream(cliente.getOutputStream())) {

				out.writeByte(COMANDO_MOSTRAR_MENU); // El servidor escribe al cliente, para que éste muestre el
														// menu.
				opcionRecibida = in.read();
				switch (opcionRecibida) {
				case COMANDO_CREAR_PARTIDA: // Caso en el que el cliente quiere crear partida.

					/*
					 * INCOMPLETO
					 */

					break;
				case COMANDO_UNIRSE_PARTIDA: // Caso en el que el cliente quiere unirse a una partida.
					// Enviar al cliente un numero indicando el numero de partidas, seguido de todas
					// las partidas con saltos de linea.
					// para que el cliente elija el numero de la partida

					/*
					 * INCOMPLETO
					 */

					break;
				case COMANDO_SALIR: // Caso en el que el cliente quiere salir.

					break;
				/*
				 * INCOMPLETO
				 */
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
