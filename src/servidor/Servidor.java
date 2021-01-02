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

	private static byte[] buff = new byte[1024 * 32];
	private static int leidos;

	private static List<Partida> partidas = new ArrayList<>();
	private static List<Partida> partidasActivas = new ArrayList<>();

	public static void main(String[] args) {
		int opcionRecibida;
		try (ServerSocket server = new ServerSocket(10000)) {
			ExecutorService pool = Executors.newCachedThreadPool();

			while (true) {
				actualizarPartidasActivas();
				
				try (Socket cliente = server.accept(); // Establecemos la conexion con 1 cliente.
						DataInputStream in = new DataInputStream(cliente.getInputStream());
						DataOutputStream out = new DataOutputStream(cliente.getOutputStream())) {
					
					out.writeByte(COMANDO_MOSTRAR_MENU); // El servidor escribe al cliente, para que éste muestre el menu.
					opcionRecibida = in.readInt();
					String nombreJugador = in.readLine(); // Leer el nombre del jugador que se ha conectado.
					
					switch (opcionRecibida) {
					case COMANDO_CREAR_PARTIDA: // Caso en el que el cliente quiere crear partida.
						crearPartida(nombreJugador, cliente);
						break;

					case COMANDO_UNIRSE_PARTIDA: // Caso en el que el cliente quiere unirse a una partida.					
						int numPartidas = partidasActivas.size();
						out.writeByte(numPartidas);
						if (numPartidas > 0) {
							out.writeBytes(mostrarPartidasActivas());
						}
						
						int partidaElegida = in.readInt() - 1; // Seleccion del indice correspondiente a la partida elegida por el cliente.
						unirsePartida(nombreJugador, cliente, partidaElegida);
						
						break;

					case COMANDO_SALIR: // Caso en el que el cliente quiere salir.

						break;
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// PRE: nombre != null, cliente != null, partidas != null, partidasActivas != null.
	// POS: crea una nueva partida del juego del ahorcado. Crea un nuevo jugador de nombre
	// POS: "nombre" y le asigna el socket por el que se comunica con el servidor.
	// POS: añade la partida a la lista de partidas y a la lista de partidas activas.
	public static void crearPartida(String nombre, Socket cliente) throws IOException {
		Partida partida = new Partida();
		partida.addJugador(new Jugador(nombre, cliente));
		partidas.add(partida);
		partidasActivas.add(partida);
	}
	
	// PRE: nombre != null, cliente != null, partida >= 0.
	// POS: añade a la partida activa elegida por el indice "partida" a un nuevo jugador
	// POS: de nombre "nombre", junto con el socket por el que se comunica con el servidor. 
	public static void unirsePartida(String nombre, Socket cliente, int partida) {
		partidasActivas.get(partida).addJugador(new Jugador(nombre, cliente));
		partidasActivas.get(partida).mostrar();
	}
	
	// PRE:
	// POS: devuelve una String que contiene el estado de las partidas activas.
	// POS: Cada partida activa se muestra en una linea diferente.
	public static String mostrarPartidasActivas() {
		String listadoPartidas = "";
		for (Partida p : partidasActivas) {
			listadoPartidas += p.estadoPartida() + "\n";
		}
		
		return (listadoPartidas + "\r\n");
	}
	
	// PRE: 
	// POS: elimina de la lista de partidas activas aquellas que ya hayan finalizado.
	public static void actualizarPartidasActivas() {
		for (Partida p : partidas) {
			if (p.partidaAcabada()) {
				partidasActivas.remove(p);
			}
		}
	}
}
