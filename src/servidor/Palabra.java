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

	// PRE:
	// POS: construye un objeto de tipo Palabra siguiendo el patrón Singleton.
	// POS: inicializa el atributo palabras (lista de String) donde se almacenan las palabras
	// POS: que se emplean para jugar al juego del ahorado a partir de un fichero de texto
	// POS: almacenado en el disco duro.
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

	// PRE:
	// POS: devuelve una palabra aleatoria de la lista de String que contiene las palabras para el juego.
	public static String generarPalabra() {
		if (palabra == null) {
			palabra = new Palabra();
		}
		
		Collections.shuffle(palabras);
		return palabras.get(0);
	}
}
