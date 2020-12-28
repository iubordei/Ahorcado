package ahorcado.host;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Host {

	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(10000);
			while (true) {
				Socket socket = server.accept();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
