package servidor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "registro")
public class Partidas implements Serializable {
	private List<Partida> partidas = new ArrayList<>();
	private List<Partida> partidasActivas = new ArrayList<>();
	private List<Partida> partidasFinalizadas = new ArrayList<>();

	// PRE:
	// POS: crea un nuevo objeto de tipo Partidas.
	public Partidas() {

	}

	@XmlTransient
	// PRE: el objeto de tipo Partidas debe haber sido inicializado previamente.
	// POS: devuelve una lista de objetos Partida donde se almacenan todas las
	// POS: partidas que se han creado en el servidor.
	public List<Partida> getPartidas() {
		return partidas;
	}

	// PRE: el objeto de tipo Partidas debe haber sido inicializado previamente.
	// POS: partidas = "partidas".
	public void setPartidas(List<Partida> partidas) {
		this.partidas = partidas;
	}

	@XmlTransient
	// PRE: el objeto de tipo Partidas debe haber sido inicializado previamente.
	// POS: devuelve una lista de objetos Partida donde se almacenan todas las
	// POS: partidas que están siendo jugadas en este momento.
	public List<Partida> getPartidasActivas() {
		return partidasActivas;
	}

	// PRE: el objeto de tipo Partidas debe haber sido inicializado previamente.
	// POS: partidasActivas = "partidasActivas".
	public void setPartidasActivas(List<Partida> partidasActivas) {
		this.partidasActivas = partidasActivas;
	}

	@XmlElementWrapper(name = "partidas")
	@XmlElement(name = "partida")
	// PRE: el objeto de tipo Partidas debe haber sido inicializado previamente.
	// POS: devuelve una lista de objetos Partida donde se almacenan todas las 
	// POS: partidas que ya han sido finalizadas.
	public List<Partida> getPartidasFinalizadas() {
		return partidasFinalizadas;
	}

	// PRE: el objeto de tipo Partidas debe haber sido inicializado previamente.
	// POS: partidasFinalizadas = "partidasFinalizadas".
	public void setPartidasFinalizadas(List<Partida> partidasFinalizadas) {
		this.partidasFinalizadas = partidasFinalizadas;
	}
}
