package servidor;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import comun.Comando;

@XmlRootElement(name = "partida")
public class Partida implements Serializable {
	private List<Jugador> jugadores;
	private List<Jugador> nuevos;
	private String palabra;
	private char[] vectorPalabraInicio;
	private char[] vectorSolucion;
	private boolean acabado;
	private boolean solucionado;
	private int errores;
	private int letrasResueltas;

	// PRE:
	// POS: crea una nueva partida con una palabra aleatoria elegida del fichero de
	// palabras.
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
		solucionado = false;
		vectorPalabraInicio = convertir();
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
	// POS: devuelve un array formado por lo caracteres que componen la palabra
	// jugada.
	public char[] convertir() {
		return (palabra.toCharArray());
	}

	// PRE: la partida debe haber sido inicializada.
	// POS: devuelve TRUE si la letra introducida está en la palabra jugada;
	// POS: FALSE en caso contrario. Actualiza el valor del número de errores, y
	// POS: pone el estado de la partida a finalizado si se ha alcanzado el número
	// máximo de errores.
	public boolean comprobar(String s) {
		for (char c : vectorPalabraInicio) {
			if (c == s.charAt(0)) {
				return (true);
			}
		}

		errores++;
		if (errores == 6) {
			acabado = true;
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
	// POS: si s es una palabra, la compara con la palabra jugada y determina si es
	// correcta
	// POS: o errónea (así como actualizar los atributos de número de errores /
	// aciertos según proceda).
	// POS: si s es un sólo caracter, comprueba que pertenezca a la solución o si ya
	// ha sido escrita,
	// POS: y si sí pertenece o no ha sido ya escrito, lo añade
	// POS: al vector de la solución (actualizando el número de aciertos).
	public void jugarLetra(String s, Jugador jugador) {
		if (s.length() > 1) {
			if (s.compareToIgnoreCase(palabra) == 0) {
				this.vectorSolucion = this.vectorPalabraInicio;
				this.letrasResueltas = s.length();

			} else {
				errores++;
				actualizarPartida(
						"El jugador " + jugador.getNombre() + " intenta resolver con " + s + " pero no es la solucion.",
						true);
			}

		} else {
			if (yaEscrita(s)) {
				errores++;
				actualizarPartida(
						"El jugador " + jugador.getNombre() + " introduce '" + s + "' pero ya se había dicho.", true);
			} else {
				if (comprobar(s)) {
					for (int i = 0; i < palabra.length(); i++) {
						if (vectorPalabraInicio[i] == s.charAt(0)) {
							vectorSolucion[i] = s.charAt(0);
							letrasResueltas++;
						}
					}
					actualizarPartida("El jugador " + jugador.getNombre() + " introduce '" + s + "' y acierta.", true);
				} else {
					actualizarPartida("El jugador " + jugador.getNombre() + " introduce '" + s + "' y falla.", true);
				}
			}
		}

		if (letrasResueltas == palabra.length()) {
			solucionado = true;
			acabado = true;
			actualizarPartida("El jugador " + jugador.getNombre() + " introduce '" + s + "' y resuelve la palabra.",
					true);
		}
	}

	private void actualizarPartida(String texto, boolean mandarErrores) {
		synchronized (jugadores) {
			for (Jugador j : jugadores) {
				try {
					StringBuilder sb = new StringBuilder();
					if (mandarErrores) {
						sb.append(errores + " errores\n");
						sb.append(Dibujo.dibujo(errores));
						sb.append("\n");
						for (char c : vectorSolucion) {
							sb.append(c + " ");
						}
						sb.append("\n\n");
					}
					sb.append(texto);
					byte[] data = new byte[sb.length() + 2];
					data[0] = (byte) Comando.COMANDO_ACTUALIZACION_PARTIDA.getID();
					data[1] = (byte) (sb.length() & 0xFF);
					System.arraycopy(sb.toString().getBytes(), 0, data, 2, sb.length());
					j.getOut().write(data);
					j.getOut().flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// PRE: la partida debe haber sido inicializada.
	// POS: lleva a cabo una partida del juego del ahorcado, respetando los turnos
	// POS: de los jugadores, mostrando el estado de la partida y calculando el
	// tiempo de partida.
	public void jugar() {
		String letra;
		while (!acabado) {
			List<Jugador> desconectados = new ArrayList<Jugador>();
			synchronized (jugadores) {
				for (Jugador j : jugadores) {
					try {
						if (errores < 6 && !acabado) {
							actualizarPartida("-----------------------\nJugador " + j.getNombre()
									+ ", es tu turno. Estado de la partida:", false);
							letra = j.jugarTurno(vectorSolucion, errores);
							jugarLetra(letra, j);
						}
					} catch (IOException e) {
						desconectados.add(j);
					}
				}
			}

			for (Jugador jugador : desconectados) {
				jugador.cerrarConexion();
				jugadores.remove(jugador);
			}

			for (Jugador jugador : desconectados)
				actualizarPartida("El jugador " + jugador.getNombre() + " se ha desconectado de la partida.", false);

			if (!nuevos.isEmpty()) {
				actualizarPartida(
						"Se ha unido a la partida " + nuevos.size() + " jugador/es: " + getNombresJugadores(nuevos),
						false);
				for (Jugador nuevoJugador : nuevos) {
					jugadores.add(0, nuevoJugador);
				}
				nuevos.clear();
			}

			if (jugadores.isEmpty())
				break;
		}
		actualizarPartida("LA PARTIDA HA FINALIZADO", false);

		for (Jugador j : jugadores) {
			j.cerrarConexion();
		}
	}

	// PRE: la partida debe haber sido inicializada.
	// POS: en función de la cantidad de errores de la partida, muestra el gráfico
	// correspondiente por pantalla.
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

	public String getNombresJugadores(List<Jugador> jugadores) {
		String nombres = "";

		for (Jugador jugador : jugadores) {
			nombres += "\n  - " + jugador.getNombre();
		}
		return nombres;
	}

	public String estadoPartida() {
		String estado = "";
		estado += "Lider de la partida: " + jugadores.get(0).getNombre() + " - ";
		estado += "Número de jugadores: " + jugadores.size() + " - ";
		estado += "Errores: " + errores;
		return (estado);
	}

	// PRE: la partida debe haber sido inicializada.
	// POS: muestra por pantalla el estado final de la partida.
	public void mostrar() {
		System.out.println(this);
	}

	// PRE: la partida debe haber sido inicializada.
	// POS: devuelve una String que representa el estado final de una partida del
	// ahorcado.
	public String toString() {
		String partida = new String("\n\n");
		partida += "Palabra inicial: " + palabra + "\n";
		partida += "Jugadores: \n";
		for (Jugador j : jugadores) {
			partida += ("\t" + j + "\n");
		}

		partida += (solucionado ? "Se ha completado" : "No se ha completado") + "\n";
		partida += (solucionado ? "Errores: " + errores + "\n" : "");

		return (partida);
	}

	@XmlElementWrapper(name = "jugadores")
	@XmlElement(name = "jugador")
	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: devuelve una lista de objetos Jugador que han participado en el turno de
	// POS: juego actual.
	public List<Jugador> getJugadores() {
		return jugadores;
	}

	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: jugadores = "jugadores".
	public void setJugadores(List<Jugador> jugadores) {
		this.jugadores = jugadores;
	}

	@XmlTransient
	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: devuelve una lista de objetos Jugador que aún no han formado parte en la
	// POS: Partida porque son nuevas incorporaciones.
	public List<Jugador> getNuevos() {
		return nuevos;
	}

	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: nuevos = "nuevos".
	public void setNuevos(List<Jugador> nuevos) {
		this.nuevos = nuevos;
	}

	@XmlElement(name = "palabra")
	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: devuelve la String que representa la palabra jugada en la Partida
	// POS: actual.
	public String getPalabra() {
		return palabra;
	}

	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: palabra = "palabra".
	public void setPalabra(String palabra) {
		this.palabra = palabra;
	}

	@XmlTransient
	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: devuelve un vector de caracteres que almacena de forma ordenada las
	// POS: letras que conforman la palabra jugada en la Partida actual.
	public char[] getVectorPalabraInicio() {
		return vectorPalabraInicio;
	}

	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: vectorPalabraInicio = "vectorPalabraInicio".
	public void setVectorPalabraInicio(char[] vectorPalabraInicio) {
		this.vectorPalabraInicio = vectorPalabraInicio;
	}

	@XmlTransient
	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: devuelve un vector de caracteres que almacena de forma ordenada las
	// POS: las letras adivinadas de la palabra jugada en la Partida actual.
	public char[] getVectorSolucion() {
		return vectorSolucion;
	}

	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: vectorSolucion = "vectorSolucion".
	public void setVectorSolucion(char[] vectorSolucion) {
		this.vectorSolucion = vectorSolucion;
	}

	@XmlElement(name = "acabado")
	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: devuelve un booleano que indica si la Partida actual ha sido finalizada.
	public boolean isAcabado() {
		return acabado;
	}

	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: acabado = "acabado".
	public void setAcabado(boolean acabado) {
		this.acabado = acabado;
	}

	@XmlElement(name = "solucionado")
	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: devuelve un booleano que inidica si la Partida actual ha sido superada.
	public boolean isSolucionado() {
		return solucionado;
	}

	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: solucionado = "solucionado".
	public void setSolucionado(boolean solucionado) {
		this.solucionado = solucionado;
	}

	@XmlElement(name = "numErrores")
	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: devuelve un número de tipo entero que representa la cantidad de errores
	// POS: cometidos en la Partida actual.
	public int getErrores() {
		return errores;
	}

	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: errores = "errores".
	public void setErrores(int errores) {
		this.errores = errores;
	}

	@XmlTransient
	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: devuelve un número de tipo entero que representa la cantidad de letras
	// POS: que han sido solucionadas de la Partida actual.
	public int getLetrasResueltas() {
		return letrasResueltas;
	}

	// PRE: el objeto de tipo Partida debe haber sido inicializado previamente.
	// POS: letrasResueltas = "letrasResueltas".
	public void setLetrasResueltas(int letrasResueltas) {
		this.letrasResueltas = letrasResueltas;
	}
}
