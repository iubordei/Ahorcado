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
	
	public Jugador() {
		
	}
	
	public Jugador(String nombre, Socket socket, DataInputStream in, DataOutputStream out) {
		this.nombre = nombre;
		this.socket = socket;
		this.in = in;
		this.out = out;
	}
	
	@XmlElement(name = "nombre")
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@XmlTransient
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	@XmlTransient
	public DataInputStream getIn() {
		return in;
	}
	
	public void setIn(DataInputStream newIn) {
		this.in = newIn;
	}

	@XmlTransient
	public DataOutputStream getOut() {
		return out;
	}
	
	public void setOut(DataOutputStream newOut) {
		out = newOut;
	}
	
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

	public String toString() {
		return "Nombre: " + nombre;
	}
	
	public void mostrar() {
		System.out.println(this);
	}
	
	public void cerrarConexion() {
		try {
			this.socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
