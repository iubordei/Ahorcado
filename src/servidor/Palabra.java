package servidor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Palabra {
	private static Palabra palabra = null;
	private static List<String> palabras;

	private Palabra() {
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

	public static String generarPalabra() {
		if (palabra == null) {
			palabra = new Palabra();
		}
		
		Collections.shuffle(palabras);
		return palabras.get(0);
	}

	public boolean tieneLetra(String actual, String letra) {
		return (actual.contains(letra));
	}

	public boolean matches(String actual, String palabra) {
		return (actual.equalsIgnoreCase(palabra));
	}
}
