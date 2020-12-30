package servidor;

import java.net.Socket;
import java.util.Scanner;

public class Jugador {
	private String nombre;
	private Socket socket;
	
	public Jugador() {
		
	}
	
	public Jugador(String nombre, Socket socket) {
		this.nombre = nombre;
		this.socket = socket;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public String jugarTurno() {
		String letraJugada = new String();

		Scanner escaner = new Scanner(System.in);
		letraJugada = escaner.nextLine();
		if (letraJugada.compareToIgnoreCase("") == 0) {
			return ("_");
		} else {
			return (letraJugada);
		}
	}

	public String toString() {
		return "Nombre: " + nombre;
	}
	
	public void mostrar() {
		System.out.println(this);
	}

}
