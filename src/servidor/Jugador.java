package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import comun.Comando;

public class Jugador implements Serializable {
	private String nombre;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;

	// PRE: nombre != null, socket != null, in != null, out != null.
	// POS: construye un objeto de tipo Jugador.
	public Jugador() {

	}

	// PRE: nombre != null, socket != null, in != null, out != null.
	// POS: construye un objeto de tipo Jugador.
	public Jugador(String nombre, Socket socket, DataInputStream in, DataOutputStream out) {
		this.nombre = nombre;
		this.socket = socket;
		this.in = in;
		this.out = out;
	}

	// PRE: el objeto de tipo Jugador debe haber sido inicializado previamante.
	// POS: devuelve el nombre del Jugador.
	@XmlElement(name = "nombre")
	public String getNombre() {
		return nombre;
	}

	// PRE: el objeto de tipo Jugador debe haber sido inicializado previamante.
	// POS: establece el nombre del jugador.
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	// PRE: el objeto de tipo Jugador debe haber sido inicializado previamante.
	// POS: devuelve el socket del Jugador.
	@XmlTransient
	public Socket getSocket() {
		return socket;
	}

	// PRE: el objeto de tipo Jugador debe haber sido inicializado previamante.
	// POS: establece el socket del Jugador.
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	// PRE: el objeto de tipo Jugador debe haber sido inicializado previamante.
	// POS: devuelve el DataInputStream del Jugador.
	@XmlTransient
	public DataInputStream getIn() {
		return in;
	}

	// PRE: el objeto de tipo Jugador debe haber sido inicializado previamante.
	// POS: establece el DataInputStream del Jugador.
	public void setIn(DataInputStream newIn) {
		this.in = newIn;
	}

	// PRE: el objeto de tipo Jugador debe haber sido inicializado previamante.
	// POS: devuelve el DataOutputStream del Jugador.
	@XmlTransient
	public DataOutputStream getOut() {
		return out;
	}

	// PRE: el objeto de tipo Jugador debe haber sido inicializado previamante.
	// POS: establece el DataOutputStream del Jugador.
	public void setOut(DataOutputStream newOut) {
		out = newOut;
	}

	// PRE: el objeto de tipo Jugador debe haber sido inicializado previamante y
	// PRE: pertener a una partida.
	// POS: el jugador juega su turno durante la partida.
	public String jugarTurno(char[] vectorSolucion, int errores) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(errores + " errores\n");
		sb.append(Dibujo.dibujo(errores));
		sb.append("\n");
		for (char c : vectorSolucion) {
			sb.append(c + " ");
		}
		byte[] data = new byte[sb.length() + 2];
		data[0] = (byte) Comando.COMANDO_TU_TURNO.getID();
		data[1] = (byte) (sb.length() & 0xFF);
		System.arraycopy(sb.toString().getBytes(), 0, data, 2, sb.length());
		out.write(data);
		out.flush();
		String turno = in.readLine();
		if (turno.compareToIgnoreCase("") == 0) {
			return ("_");
		} else {
			return (turno);
		}
	}

	// PRE: el objeto de tipo Jugador debe haber sido inicializado previamante.
	// POS: devuelve una String que representa al Jugador.
	public String toString() {
		return "Nombre: " + nombre;
	}

	// PRE: el objeto de tipo Jugador debe haber sido inicializado previamante.
	// POS: muestra por pantalla al Jugador.
	public void mostrar() {
		System.out.println(this);
	}

	// PRE: el objeto de tipo Jugador debe haber sido inicializado previamante.
	// POS: cierra el socket del Jugador.
	public void cerrarConexion() {
		try {
			this.socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
