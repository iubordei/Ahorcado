package servidor;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class GuardarPartida extends TimerTask {
	// Variable que almacena todas las partidas jugadas.
	private Partidas registro;

	// PRE: registro != null.
	// POS: crea un nuevo objeto de tipo GuardarPartida.
	public GuardarPartida(Partidas registro) {
		this.registro = registro;
	}

	// PRE: el objeto de tipo GuardarPartida debe haber sido inicializado
	// PRE: previamente.
	// POS: ejecuta como hilo la función de guardar como XML las partidas
	// POS: finalizadas del "Juego del Ahorcado".
	@Override
	public void run() {
		Servidor.actualizarPartidas();
		try {
			JAXBContext context = JAXBContext.newInstance(Partidas.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(registro, new File("Registro de Partidas.xml"));

		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

}
