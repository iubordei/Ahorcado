package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

	// Constantes
	private static final int COMANDO_MOSTRAR_MENU = 0;
	private static final int COMANDO_MOSTAR_PARTIDAS = 1;
	private static final int COMANDO_TU_TURNO = 2;
	private static final int COMANDO_ACTULIZACION_PARTIDA = 3;
	private static final int COMANDO_CREAR_PARTIDA = 4;
	private static final int COMANDO_UNIRSE_PARTIDA = 5;
	private static final int COMANDO_SALIR = 6;
	private static final int COMANDO_INTRODUCIR_LETRA = 7;
	
	private static Scanner scanner = new Scanner(System.in);

	private static final String TEXTO_MENU = "" + "Bienvenido al ahoracado %s\r\n" + "¿Qué deseas hacer?\r\n\r\n"
			+ "  - 1. Crear partida\r\n" + "  - 2. Unirse a una partida\r\n" + "  - 3. Salir\r\n";

	private static final String TEXTO_INTRODUCIR_NOMBRE = "Introduce tu nombre:";

	private static final String TEXTO_NO_HAY_PARTIDAS_ACTIVAS = "" + "No hay partidas activas. ¿Quieres crear una?\r\n"
			+ "  - 1. Sí\r\n" + "  - 2. No\r\n";

	// Variables
	private int idCliente; // atributo para asociar un cliente a un jugador.
	private int idPartida; // atributo para unir un cliente a una partida.
	private String nombre;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;

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

		try (Socket conexion = new Socket("localhost", 10000);) // Establecemos la conexion con el servidor.
		{
			Cliente cliente = new Cliente(nombre, conexion);
			cliente.waitForData();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void waitForData() throws IOException {
		int command = -1;
		while ((command = in.read()) != -1) {
			switch (command) {
			case COMANDO_MOSTRAR_MENU:
				mostrarMenu();
				break;
			case COMANDO_MOSTAR_PARTIDAS:
				unirsePartida();
				break;
			case COMANDO_TU_TURNO:
				jugarTurno();
				break;
			case COMANDO_ACTULIZACION_PARTIDA:
				actualizarPartida();
				break;
			}
		}
	}

	// Este metodo muestra el menu de opciones iniciales del cliente y retorna la
	// opcion escogida por el cliente.
	private void mostrarMenu() throws IOException {
		// Enseñar el menu y leer la opción que elige el cliente.
		int opcion = -1;
		while (opcion < 1 || opcion > 3) {
			System.out.println(String.format(TEXTO_MENU, nombre));
			opcion = scanner.nextInt();
		}
		// Retornar la opción elegida.
		switch (opcion) {
		case 1:
			opcion = COMANDO_CREAR_PARTIDA;
			break;
		case 2:
			opcion = COMANDO_UNIRSE_PARTIDA;
			break;
		case 3:
			opcion = COMANDO_SALIR;
			break;
		}

		out.writeInt(opcion);
		out.writeBytes(this.nombre + "\r\n");

		if (opcion == COMANDO_UNIRSE_PARTIDA) {
			unirsePartida();
		}
	}

	// Este metodo muestra todas las partidas activas y retorna un entero que indica
	// la partida a la que quiere unirse el cliente.
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
				out.write(COMANDO_CREAR_PARTIDA); // si quieres crear partida le envias al servidor un 1 indicando que
													// quieres crear una partida.
				break;
			default:
				mostrarMenu(); // si no quieres crear partida, vuelves al menu inicial.
			}
			return;
		}
		
		System.out.println("\nPartidas disponibles:");
		for (int i = 1; i <= numPartidas; i++) {
			System.out.println("\t" + i + ". " + in.readLine());
		}
		
		// Leer el número de partida que elige el cliente.
		System.out.println("\nIntroduce la partida a la que te quieres unir:");
		while (opcion < 1 || opcion > numPartidas) {
			opcion = scanner.nextInt();
		}
		out.writeInt(opcion);
	}

	// Este metodo obtiene la letra o palabra (String) que el cliente ha introducido
	// por
	// teclado para posteriormente enviarla a través del Socket.
	public String introducirLetra() {
		System.out.println("Introduce la letra / palabra");
		String letra = scanner.nextLine();
		return letra;
	}

	public void jugarTurno() throws IOException {
		String letra = introducirLetra();
		out.write(COMANDO_INTRODUCIR_LETRA);
		out.writeBytes(letra + "\r\n");
	}

	public void actualizarPartida() throws IOException {
		String linea = null;
		while ((linea = in.readLine()) != null) {
			System.out.println(linea);
		}
	}

	// Este metodo obtiene el id de un cliente para poder relacionar lo que hace
	// este cliente con el correspondiente jugdor.
	public int getIdCliente() {
		return idCliente;
	}

	// Este metodo obtiene el nombre del cliente.
	public String getNombre() {
		return nombre;
	}

	// Este metodo obtiene el idPartida de un cliente.
	public int getIdPartida() {
		return idPartida;
	}

	public String toString() {
		return "Nombre: " + nombre + ", partida: " + idPartida;
	}

}
