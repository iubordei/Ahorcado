package ahorcado;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Palabra {
	private static List<String> palabra = null;
	
	public static String getPalabra() {
		if (palabra == null) {
			palabra = new ArrayList<>();

			DataInputStream in = null;
			try {
				File file = new File("files/palabras.txt");
				in = new DataInputStream(new FileInputStream(file));
				String linea = in.readLine();
				while (linea != null) {
					palabra.add(linea);
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

		Collections.shuffle(palabra);
		return(palabra.get(0));
	}
}
