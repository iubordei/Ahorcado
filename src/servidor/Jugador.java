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
	
	// PRE:
	// POS: construye un objeto de tipo Jugado.
	public Jugador() {
		
	}
	
	// PRE:
	// POS: construye un objeto de tipo Jugado.
	public Jugador(String nombre, Socket socket, DataInputStream in, DataOutputStream out) {
		this.nombre = nombre;
		this.socket = socket;
		this.in = in;
		this.out = out;
	}
	
	// PRE: El jugador debe haber sido inicializado
	// POS: devuelve el nombre del jugador.
	@XmlElement(name = "nombre")
	public String getNombre() {
		return nombre;
	}
	
	// PRE: El jugador debe haber sido inicializado
	// POS: establece el nombre del jugador.
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	// PRE: El jugador debe haber sido inicializado
	// POS: devuelve el socket del jugador.
	@XmlTransient
	public Socket getSocket() {
		return socket;
	}

	// PRE: El jugador debe haber sido inicializado
	// POS: establece el socket del jugador.
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	// PRE: El jugador debe haber sido inicializado
	// POS: devuelve el DataInputStream del jugador.
	@XmlTransient
	public DataInputStream getIn() {
		return in;
	}
	
	// PRE: El jugador debe haber sido inicializado
	// POS: establece el DataInputStream del jugador.
	public void setIn(DataInputStream newIn) {
		this.in = newIn;
	}

	// PRE: El jugador debe haber sido inicializado
	// POS: devuelve el DataOutputStream del jugador.
	@XmlTransient
	public DataOutputStream getOut() {
		return out;
	}
	
	// PRE: El jugador debe haber sido inicializado
	// POS: establece el DataOutputStream del jugador.
	public void setOut(DataOutputStream newOut) {
		out = newOut;
	}
	
	//  PRE: El jugador debe haber sido inicializado y esta en una partida
	//  POS: el jugador juega su turno durante la partida.
	public String jugarTurno(char[] vectorSolucion, int errores) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(errores + " errores\n");
		sb.append(Dibujo.dibujo(errores));
		sb.append("\n");
		for (char c : vectorSolucion) {
			sb.append(c + " ");
		}
		byte[] data = new byte[sb.length() + 2];
		data[0] = (byte)Comando.COMANDO_TU_TURNO.getID();
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

	//  PRE: El jugador debe haber sido inicializado
	//  POS: devuelve una String que representa al jugador
	public String toString() {
		return "Nombre: " + nombre;
	}
	
	//  PRE: El jugador debe haber sido inicializado
	//  POS: muestra por pantalla al jugador
	public void mostrar() {
		System.out.println(this);
	}
	
	//  PRE: El jugador debe haber sido inicializado
	//  POS: cierra el socket del jugador.
	public void cerrarConexion() {
		try {
			this.socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
