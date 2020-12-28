package ahorcado.host;

import java.net.Socket;
import java.util.Scanner;

public class Jugador {
	private String nombre;
	private Socket socket;
	
	public Jugador(String nombre, Socket socket) {
		this.nombre = nombre;
		this.socket = socket;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String toString() {
		return "Nombre: " + nombre;
	}

}
