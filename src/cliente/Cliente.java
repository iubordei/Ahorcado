package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import comun.Comando;

public class Cliente {
	// Constantes.
	private static Scanner scanner = new Scanner(System.in);

	private static final String TEXTO_MENU = "" + "Bienvenido al ahoracado %s\r\n" + "¿Qué deseas hacer?\r\n\r\n"
			+ "  - 1. Crear partida\r\n" + "  - 2. Unirse a una partida\r\n" + "  - 3. Salir\r\n";

	private static final String TEXTO_INTRODUCIR_NOMBRE = "Introduce tu nombre:";

	private static final String TEXTO_NO_HAY_PARTIDAS_ACTIVAS = "" + "No hay partidas activas. ¿Quieres crear una?\r\n"
			+ "  - 1. Sí\r\n" + "  - 2. No\r\n";

	// Variables.
	private int idCliente; // atributo para asociar un cliente a un jugador.
	private int idPartida; // atributo para unir un cliente a una partida.
	private String nombre;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;

	// PRE: nombre != null, socket != null.
	// POS: crea un objeto de tipo Cliente con nombre = "nombre" y socket =
	// POS: "socket".
	public Cliente(String nombre, Socket socket) throws IOException {
		this.idCliente = (int) System.currentTimeMillis();
		this.nombre = nombre;
		this.socket = socket;
		in = new DataInputStream(socket.getInputStream()); // Leemos del servidor
		out = new DataOutputStream(socket.getOutputStream()); // Escribimos hacia el servidor
	}

	public static void main(String[] args) {
		System.out.println(TEXTO_INTRODUCIR_NOMBRE);
		String nombre = scanner.nextLine();

		try (Socket conexion = new Socket("localhost", 10000)) // Establecemos la conexion con el servidor.
		{
			Cliente cliente = new Cliente(nombre, conexion);
			cliente.waitForData();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// PRE: el objeto de tipo Cliente debe haber sido inicializado previamente.
	// POS: el objeto de tipo Cliente espera a que le llegue un comando.
	public void waitForData() throws IOException {
		int commandID = -1;

		while ((commandID = in.read()) != -1) {
			Comando comando = Comando.getComando(commandID);
			if (comando == null)
				return;

			switch (comando) {
			case COMANDO_MOSTRAR_MENU:
				mostrarMenu();
				break;
			case COMANDO_MOSTAR_PARTIDAS:
				unirsePartida();
				break;
			case COMANDO_TU_TURNO:
				jugarTurno();
				break;
			case COMANDO_ACTUALIZACION_PARTIDA:
				actualizarPartida();
				break;
			}
		}
	}

	// PRE: el objeto de tipo Cliente debe haber sido inicializado previamente.
	// POS: muestra el menú de opciones iniciales del cliente y retorna
	// POS: la opcion elegida por el cliente.
	private void mostrarMenu() throws IOException {
		// Enseñar el menu y leer la opción que elige el cliente.
		int opcion = -1;
		System.out.println(String.format(TEXTO_MENU, nombre));
		while (opcion < 1 || opcion > 3) {
			try {
				opcion = Integer.parseInt(scanner.nextLine());
				
			} catch (NumberFormatException e) {
				System.out.println("\nOpción incorrecta... Introduce nuevamente la opción deseada:");
			}
		}
		// Retornar la opción elegida.
		switch (opcion) {
		case 1:
			opcion = Comando.COMANDO_CREAR_PARTIDA.getID();
			break;

		case 2:
			opcion = Comando.COMANDO_UNIRSE_PARTIDA.getID();
			break;

		case 3:
			opcion = Comando.COMANDO_SALIR.getID();
			break;
		}

		out.writeInt(opcion);
		out.writeBytes(this.nombre + "\r\n");
		out.flush();

		if (opcion == Comando.COMANDO_UNIRSE_PARTIDA.getID()) {
			unirsePartida();
		}
	}

	// PRE: el objeto de tipo Cliente debe haber sido inicializado previamente.
	// POS: muestra todas las partidas activas y retorna un número entero que indica
	// POS: la partida a la que quiere unirse el cliente.
	private void unirsePartida() throws IOException {
		// Enseñar todas las partidas.
		int numPartidas = 0;
		numPartidas = in.read();
		int opcion = -1;
		if (numPartidas == 0) { // No hay partidas activas.
			System.out.print(TEXTO_NO_HAY_PARTIDAS_ACTIVAS);
			opcion = scanner.nextInt();
			switch (opcion) {
			case 1:
				out.writeInt(Comando.COMANDO_CREAR_PARTIDA.getID()); // si quieres crear partida le envias al servidor
																		// un 1 indicando que
				// quieres crear una partida.
				out.flush();
				break;

			default:
				mostrarMenu(); // si no quieres crear partida, vuelves al menú inicial.
			}
			return;
		}

		System.out.println("\nPartidas disponibles:");
		for (int i = 1; i <= numPartidas; i++) {
			System.out.println("\t" + i + ". " + in.readLine());
		}

		// Leer el número de partida que elige el cliente.
		while (opcion < 1 || opcion > numPartidas) {
			try {
				System.out.println("\nIntroduce la partida a la que te quieres unir:");
				opcion = Integer.parseInt(scanner.nextLine());

			} catch (NumberFormatException e) {
				System.out.println("Opción incorrecta... Introduce nuevamente la opción deseada:");
			}
		}
		out.writeInt(opcion);
		out.flush();
		System.out.println("Partida en curso, espera tu turno");
	}

	// PRE: el objeto de tipo Cliente debe haber sido inicializado previamente.
	// POS: obtiene la letra o palabra (String) que el cliente ha introducido
	// POS: por teclado para posteriormente enviarla a través del Socket.
	public String introducirLetra() {
		Scanner escaner = new Scanner(System.in);
		String letra = "";
		while (letra.length() == 0) {
			System.out.println("Introduce la letra / palabra");
			letra = escaner.nextLine();
		}
		return letra;
	}

	// PRE: el objeto de tipo Cliente debe haber sido inicializado previamente.
	// POS: actualiza la visión de la Partida al cliente y recoge la letra o
	// POS: palabra jugada en este turno por el Cliente. Comunica esta información
	// POS: con la Partida en la que está jugando.
	public void jugarTurno() throws IOException {
		actualizarPartida();
		System.out.println("Es tu turno!");
		String letra = introducirLetra();
		out.writeBytes(letra + "\r\n");
		out.flush();
	}

	// PRE: el objeto de tipo Cliente debe haber sido inicializado previamente.
	// POS: muestra por pantalla al Cliente una actualización de la Partida de
	// POS: la que forma parte.
	public void actualizarPartida() throws IOException {
		int length = in.readByte() & 0xFF;
		byte[] buffer = new byte[1024];
		int totalRead = 0;
		int readBytes = 0;
		while (totalRead < length) {
			readBytes = in.read(buffer, totalRead, length - totalRead);
			totalRead += readBytes;
		}
		System.out.println(new String(buffer));
		System.out.println();
		System.out.println();
	}

	// PRE: el objeto de tipo Cliente debe haber sido inicializado previamente.
	// POS: obtiene el id de un cliente para poder relacionar lo que hace
	// POS: este cliente con su correspondiente jugdor.
	public int getIdCliente() {
		return idCliente;
	}

	// PRE: el objeto de tipo Cliente debe haber sido inicializado previamente.
	// POS: obtiene el nombre del cliente.
	public String getNombre() {
		return nombre;
	}

	// PRE: el objeto de tipo Cliente debe haber sido inicializado previamente.
	// POS: obtiene el idPartida de un cliente.
	public int getIdPartida() {
		return idPartida;
	}

	// PRE: el objeto de tipo Cliente debe haber sido inicializado previamente.
	// POS: devuelve una String que representa al objeto Cliente actual.
	public String toString() {
		return ("Nombre: " + nombre + ", partida: " + idPartida);
	}
}
