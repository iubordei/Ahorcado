package servidor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Partida implements Runnable {
	private List<Jugador> jugadores;
	private String palabra;
	private String pista;
	private char[] vectorPalabraInicio;
	private char[] vectorSolucion;
	private boolean acabado;
	private boolean solucionado;
	private int errores;
	private int letrasResueltas;
	private long tiempoInicio;
	private long duracionPartida;
	private static DecimalFormat df = new DecimalFormat("0.00");
	
	public Partida(String palabra, String pista) {
		this.palabra = palabra;
		this.pista = pista;
		this.jugadores = new ArrayList<>();
		this.vectorSolucion = new char[palabra.length()];
		for (int i = 0; i < palabra.length(); i++) {
			vectorSolucion[i] = '_';
		}

		errores = 0;
		letrasResueltas = 0;
		acabado = false;
		vectorPalabraInicio = convertir();
		tiempoInicio = System.currentTimeMillis();
	}

	public void addJugador(Jugador jugador) {
		jugadores.add(jugador);
	}

	public char[] convertir() {
		return (palabra.toCharArray());
	}

	public boolean comprobar(String s) {
		for (char c : vectorPalabraInicio) {
			if (c == s.charAt(0)) {
				return (true);
			}
		}

		errores++;
		if (errores == 6) {
			acabado = true;
			solucionado = false;
		}
		return (false);
	}

	public boolean yaEscrita(String s) {
		for (int i = 0; i < palabra.length(); i++) {
			if (vectorSolucion[i] == s.charAt(0)) {
				return (true);
			}
		}

		return (false);
	}

	public void escribirLetra(String s) {
		if (yaEscrita(s)) {
			errores++;
			
		} else {
			for (int i = 0; i < palabra.length(); i++) {
				if (vectorPalabraInicio[i] == s.charAt(0)) {
					vectorSolucion[i] = s.charAt(0);
					letrasResueltas++;
				}
			}
		}

		if (letrasResueltas == palabra.length()) {
			solucionado = true;
			acabado = true;
		}
	}

	public void run() {
		String letra;
		while (!acabado) {
			for (Jugador j : jugadores) {
				if (errores < 6 && !acabado) {
					dibujar();
					j.mostrar();
					letra = j.jugarTurno();
					if (comprobar(letra)) {
						escribirLetra(letra);
					}
					
				} else {
					duracionPartida = System.currentTimeMillis() - tiempoInicio;
				}
			}
		}

		mostrar();
		dibujar();
	}

	public void dibujar() {
		System.out.println(Dibujo.dibujo(errores));
		for (char c : vectorSolucion) {
			System.out.print(c + " ");
		}
		System.out.println("\n");
	}

	public void mostrar() {
		System.out.println(this);
	}

	public String toString() {
		String partida = new String("\n\n");
		partida += "Palabra inicial: " + palabra + "\n";
		partida += (pista != null) ? ("Pista: " + pista + "\n") : "";
		partida += "Jugadores: \n";
		for (Jugador j : jugadores) {
			partida += ("\t" + j + "\n");
		}

		partida += (solucionado ? "Se ha completado" : "No se ha completado") + "\n";
		partida += (solucionado ? "Errores: " + errores + "\n" : "");
		partida += "Tiempo jugado: " + df.format((double) (duracionPartida / 1000)) + "s";

		return (partida);
	}
}
