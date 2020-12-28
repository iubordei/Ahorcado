package ahorcado.host;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Palabra {
	private String palabra;
	private static List<String> palabras = null;
	
	
	//De esta forma hay 1 palabra por partida, si lo hago todo estatico sera una palabra para todas las partidas.
	public static String generarPalabra() {
		if (palabras == null) {
			palabras = new ArrayList<>();

			DataInputStream in = null;
			try {
				File file = new File("files/palabras.txt");
				in = new DataInputStream(new FileInputStream(file));
				String linea = in.readLine();
				while (linea != null) {
					palabras.add(linea);
					linea = in.readLine();
				}				

			} catch (IOException e) {
				e.printStackTrace();

			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		Collections.shuffle(palabras);
		return palabras.get(0);
	}
	
	public Palabra() {
		palabra = generarPalabra();
	}
	
	public boolean tieneLetra(String letra) {
		if (palabra.contains(letra))
			return true;
		return false;
	}
	
	public boolean matches(String palabra) {
		return this.palabra.equalsIgnoreCase(palabra);
	}
}
