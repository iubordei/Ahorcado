package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import comun.Comando;

public class Servidor {

	private static List<Partida> partidas = new ArrayList<>();
	private static List<Partida> partidasActivas = new ArrayList<>();

	public static void main(String[] args) {
		
		try (ServerSocket server = new ServerSocket(10000)) {
			while (true) {
				actualizarPartidasActivas();
				try
				{
					// Establecemos la conexion con 1 cliente y lo clasificamos. Esta
					// acción se ejecuta en un hilo paralelo para no ocupar el servidor
					// mientras el cliente decide qué hacer.
					new ClasificarCliente(server.accept()).start();
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
	public static void crearPartida(String nombre, Socket socket, DataInputStream in, DataOutputStream out) throws IOException {
		Partida partida = new Partida();
		partida.addJugador(new Jugador(nombre, socket, in, out));
		partidas.add(partida);
		partidasActivas.add(partida);
		partida.start();
	}
	
	// PRE: nombre != null, cliente != null, partida >= 0.
	// POS: añade a la partida activa elegida por el indice "partida" a un nuevo jugador
	// POS: de nombre "nombre", junto con el socket por el que se comunica con el servidor. 
	public static void unirsePartida(String nombre, Socket socket, DataInputStream in, DataOutputStream out, int partida) {
		partidasActivas.get(partida).addJugador(new Jugador(nombre, socket, in, out));
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
	
	
	private static class ClasificarCliente extends Thread {
		private Socket cliente;
		private DataInputStream in;
		private DataOutputStream out;
		
		private String nombreJugador = null;
		
		/**
		 * Constructor de clase. Instancia una clase del tipo {@code ClasificarCliente}
		 * con los argumentos proporcionados.
		 * 
		 * @param socket del cliente a clasificar.
		 */
		public ClasificarCliente(Socket cliente) {
			this.cliente = cliente;
		}
		
		@Override
		public void run() {
			try {
				in = new DataInputStream(cliente.getInputStream());
				out = new DataOutputStream(cliente.getOutputStream());
				
				// Muestra el menú de opciones en el cliente.
				out.writeByte(Comando.COMANDO_MOSTRAR_MENU.getID()); // El servidor escribe al cliente, para que éste muestre el menu.
				
				// Lee el comando que el cliente ha escogido del menú.
				Comando comando = Comando.getComando(in.readInt());
				
				// Leer el nombre del jugador que se ha conectado.
				nombreJugador = in.readLine(); 
				
				// Procesa el comando que el cliente ha escogido.
				procesarComando(comando);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 *  Procesa el comando recibido.
		 *  
		 * @param comando Comando a procesar.
		 * 
		 * @throws IOException Si hay alguna excepción procesando el comando.
		 */
		private void procesarComando(Comando comando) throws IOException {
			// Si el comando a procesar no existe, el cliente deberá introducir un nuevo comando.
			if (comando == null) {
				// TODO: Indicar al cliente que el comando introducido no existe
				Comando nuevoComando = Comando.getComando(in.readInt());
				procesarComando(nuevoComando);
				return;
			}
				
			switch (comando) {
			case COMANDO_CREAR_PARTIDA: // Caso en el que el cliente quiere crear partida.
				crearPartida(nombreJugador, cliente, in, out);
				break;

			case COMANDO_UNIRSE_PARTIDA: // Caso en el que el cliente quiere unirse a una partida.					
				int numPartidas = partidasActivas.size();
				out.writeByte(numPartidas);
				
				if (numPartidas > 0) {
					out.writeBytes(mostrarPartidasActivas());
					int partidaElegida = in.readInt() - 1; // Seleccion del indice correspondiente a la partida elegida por el cliente.
					unirsePartida(nombreJugador, cliente, in, out, partidaElegida);
					break;
				}
				
				// Si el número de partidas es 0, el cliente debe escoger alguna otra opción.
				Comando nuevoComando = Comando.getComando(in.readInt());
				procesarComando(nuevoComando);
				break;
				
			case COMANDO_SALIR: // Caso en el que el cliente quiere salir.
				cliente.close();
				break;
			}
		}
	}
}
