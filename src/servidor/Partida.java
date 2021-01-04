package servidor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import comun.Comando;

public class Partida implements Runnable {
	private List<Jugador> jugadores;
	private List<Jugador> nuevos;
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
	

	// PRE:
	// POS: crea una nueva partida con una palabra aleatoria elegida del fichero de palabras.
	public Partida() {
		this.palabra = Palabra.generarPalabra();
		this.jugadores = new ArrayList<>();
		this.nuevos = new ArrayList<>();
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

	// PRE: la partida debe haber sido inicializada.
	// POS: añade al Jugador jugador a la partida actual.
	public void addJugador(Jugador jugador) {
		if (jugadores.isEmpty()) {
			jugadores.add(jugador);
		} else {
			nuevos.add(jugador);
		}
	}

	
	// PRE: la partida debe haber sido inicializada.
	// POS: devuelve un array formado por lo caracteres que componen la palabra jugada.
	public char[] convertir() {
		return (palabra.toCharArray());
	}

	// PRE: la partida debe haber sido inicializada.
	// POS: devuelve TRUE si la letra introducida está en la palabra jugada;
	// POS: FALSE en caso contrario. Actualiza el valor del número de errores, y
	// POS: pone el estado de la partida a finalizado si se ha alcanzado el número máximo de errores.
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

	// PRE: la partida debe haber sido inicializada.
	// POS: devuelve TRUE si la letra introducida ya ha sido escrita,
	// POS: y FALSE en caso contrario.
	public boolean yaEscrita(String s) {
		for (int i = 0; i < palabra.length(); i++) {
			if (vectorSolucion[i] == s.charAt(0)) {
				return (true);
			}
		}

		return (false);
	}

	// PRE: la partida debe haber sido inicializada.
	// POS: si s es una palabra, la compara con la palabra jugada y determina si es correcta
	// POS: o errónea (así como actualizar los atributos de número de errores / aciertos según proceda).
	// POS: si s es un sólo caracter, comprueba que pertenezca a la solución o si ya ha sido escrita,
	// POS: y si sí pertenece o no ha sido ya escrito, lo añade
	// POS: al vector de la solución (actualizando el número de aciertos).
	public void jugarLetra(String s, Jugador jugador) {
		if (s.length() > 1) {
			if (s.compareToIgnoreCase(palabra) == 0) {
				this.vectorSolucion = this.vectorPalabraInicio;
				this.letrasResueltas = s.length();

			} else {
				errores++;
				actualizarPartida("El jugador " + jugador.getNombre() + " intenta resolver con " + s + " pero no es la solucion.");
			}

		} else {
			if (yaEscrita(s)) {
				errores++;
				actualizarPartida("El jugador " + jugador.getNombre() + " introduce '" + s + "' pero ya se había dicho.");
			} else {
				if (comprobar(s)) {
					for (int i = 0; i < palabra.length(); i++) {
						if (vectorPalabraInicio[i] == s.charAt(0)) {
							vectorSolucion[i] = s.charAt(0);
							letrasResueltas++;
						}
					}
					actualizarPartida("El jugador " + jugador.getNombre() + " introduce '" + s + "' y acierta.");
				} else {
					actualizarPartida("El jugador " + jugador.getNombre() + " introduce '" + s + "' y falla.");
				}
			}
		}

		if (letrasResueltas == palabra.length()) {
			solucionado = true;
			acabado = true;
			actualizarPartida("El jugador " + jugador.getNombre() + " introduce '" + s + "' y resuelve la palabra.");
		}
	}

	private void actualizarPartida(String texto) {
		for (Jugador j : jugadores) {
			try {
				StringBuilder sb = new StringBuilder();
				sb.append(errores + " errores\n");
				sb.append(Dibujo.dibujo(errores));
				sb.append("\n");
				for (char c : vectorSolucion) {
					sb.append(c + " ");
				}	
				sb.append("\n\n");
				sb.append(texto);
				byte[] data = new byte[sb.length() + 2];
				data[0] = (byte) Comando.COMANDO_ACTUALIZACION_PARTIDA.getID();
				data[1] = (byte) sb.length();
				System.arraycopy(sb.toString().getBytes(), 0, data, 2, sb.length());
				j.getOut().write(data);
				j.getOut().flush();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	// PRE: la partida debe haber sido inicializada.
	// POS: lleva a cabo una partida del juego del ahorcado, respetando los turnos
	// POS: de los jugadores, mostrando el estado de la partida y calculando el tiempo de partida.
	public void run() {
		String letra;
		while (!acabado) {
			try {
				for (Jugador j : jugadores) {
					if (errores < 6 && !acabado) {
						dibujar();
						j.mostrar();
						letra = j.jugarTurno(vectorSolucion, errores);
						jugarLetra(letra, j);
					} else {
						duracionPartida = System.currentTimeMillis() - tiempoInicio;
					}
				}
				
				if (!nuevos.isEmpty()) {
					jugadores.addAll(nuevos);
					nuevos.clear();
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}

		mostrar();
		dibujar();
		
		for (Jugador j : jugadores) {
			j.cerrarConexion();
		}
	}

	// PRE: la partida debe haber sido inicializada.
	// POS: en función de la cantidad de errores de la partida, muestra el gráfico correspondiente por pantalla.	
	public void dibujar() {
		System.out.println(Dibujo.dibujo(errores));
		for (char c : vectorSolucion) {
			System.out.print(c + " ");
		}
		System.out.println("\n");
	}
	
	public boolean partidaAcabada() {
		return (this.acabado);
	}
	
	public String estadoPartida() {
		String estado = "";
		estado += "Errores: " + errores + "; ";
		estado += "Estado de la resolución: ";
		for (int i = 0; i < palabra.length(); i++) {
			estado += vectorSolucion[i] + " ";
		}
		
		return (estado);
	}

	// PRE: la partida debe haber sido inicializada.
	// POS: muestra por pantalla el estado final de la partida.
	public void mostrar() {
		System.out.println(this);
	}

	// PRE: la partida debe haber sido inicializada.
	// POS: devuelve una String que representa el estado final de una partida del ahorcado.
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
