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
	private static List<Partida> partidasJugables = new ArrayList<>();

	public static void main(String[] args) {
		int opcionRecibida;
		try (ServerSocket server = new ServerSocket(10000)) {
			ExecutorService pool = Executors.newCachedThreadPool();

			while (true) {
				actualizarPartidasJugables();
				
				try (Socket cliente = server.accept(); // Establecemos la conexion con 1 cliente.
						DataInputStream in = new DataInputStream(cliente.getInputStream());
						DataOutputStream out = new DataOutputStream(cliente.getOutputStream())) {

					out.writeByte(COMANDO_MOSTRAR_MENU); // El servidor escribe al cliente, para que éste muestre el menu.
					opcionRecibida = in.read();
					switch (opcionRecibida) {
					case COMANDO_CREAR_PARTIDA: // Caso en el que el cliente quiere crear partida.
						crearPartida(in, cliente);
						break;

					case COMANDO_UNIRSE_PARTIDA: // Caso en el que el cliente quiere unirse a una partida.
						int numPartidas = partidasJugables.size();
						out.writeByte(numPartidas);
						if (numPartidas > 0) {
							out.writeBytes(mostrarPartidasJugables());
						}

						System.out.println(in.readLine());
//						int partidaElegida = Integer.parseInt();
//						partidasJugables.get(partidaElegida - 1).addJugador(new Jugador("segundo", cliente));
//						partidasJugables.get(partidaElegida - 1).mostrar();
						
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
	
	public static void crearPartida(DataInputStream in, Socket cliente) throws IOException {
		Partida partida = new Partida();
		partida.addJugador(new Jugador(in.readLine(), cliente));
		partidas.add(partida);
		partidasJugables.add(partida);
	}
	
	public static String mostrarPartidasJugables() {
		String listadoPartidas = "";
		for (int i = 1; i <= partidasJugables.size(); i++) {
			listadoPartidas += i + ". " + partidasJugables.get(i - 1).estadoPartida() + "\n";
		}
		
		return (listadoPartidas + "\r\n");
	}
	
	public static void actualizarPartidasJugables() {
		for (Partida p : partidas) {
			if (p.partidaAcabada()) {
				partidasJugables.remove(p);
			}
		}
	}
}
