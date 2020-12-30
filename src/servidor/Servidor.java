package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {

	private static List<Partida> partidas = new ArrayList<>();

	public static void main(String[] args) {
		try {

			// partidaPrueba sin sockets

			ExecutorService pool = Executors.newCachedThreadPool();
			ServerSocket server = new ServerSocket(10000);
			Jugador j1 = new Jugador("paco", null);
			Jugador j2 = new Jugador("pepe", null);

			Partida partida = new Partida(Palabra.generarPalabra(), null);
			partida.addJugador(j1);
			partida.addJugador(j2);

			partidas.add(partida);
			pool.submit(partida);
			
			pool.shutdown();

			// Fin partidaPrueba sin sockets

			while (true) {

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
