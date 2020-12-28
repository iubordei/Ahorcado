package ahorcado.cliente;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import ahorcado.host.Palabra;

public class Cliente {
	private String nombre;
	private int idPartida;
	private Socket socket;
	
	public Cliente(String nombre, Socket socket) {
		this.nombre = nombre;
		this.socket = socket;
	}
	
	public static void main(String[] args) {
		System.out.println("Introduce el nombre tu jugador");
		Scanner scanner = new Scanner(System.in);
		String nombre = scanner.nextLine();
		scanner.close();
		try {
			Socket socket = new Socket("localhost", 10000);
			Cliente cliente = new Cliente(nombre, socket);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String introducirLetra() {
		System.out.println("Introduce la letra");
		Scanner scanner = new Scanner(System.in);
		String letra = scanner.nextLine();
		scanner.close();
		return letra;
	}
	
	public String introducirSolucion() {
		System.out.println("Introduce la palabra");
		Scanner scanner = new Scanner(System.in);
		String palabra = scanner.nextLine();
		scanner.close();
		return palabra;
	}
	
	public String toString() {
		return "Nombre: " + nombre + ", partida: " + idPartida;
	}
	
	//muestra todas las partidas y las 2 opciones disponibles para el cliente
	//crear partida: 
	//unirse a partida:
	public void menu() {
		
	}

}
