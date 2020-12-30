package servidor;

public class Dibujo {

	// PRE:
	// POS: devuelve una String que representa la cantidad de errores cometidos en una partida del ahorcado.
	public static String dibujo(int errores) {
		switch (errores) {
			case 1:
				return (" ________\n" + " |/      |\n" + " | \n" + " | \n" + " | \n" + " | \n" + "----------");
	
			case 2:
				return (" ________\n" + " |/      |\n" + " |       O\n" + " | \n" + " | \n" + " | \n" + "----------");
	
			case 3:
				return (" ________\n" + " |/      |\n"  + " |       O\n" + " |      /|\n" + " |       |\n" + " | \n" + "----------");
	
			case 4:
				return (" ________\n" + " |/      |\n"  + " |       O\n" + " |      /|\\\n" + " |       |\n" + " | \n" + "----------");
	
			case 5:
				return (" ________\n" + " |/      |\n"  + " |       O\n" + " |      /|\\\n" + " |       |\n" + " |      / \n" + "----------");
				
			case 6:
				return (" ________\n" + " |/      |\n"  + " |       O\n" + " |      /|\\\n" + " |       |\n" + " |      / \\\n" + "----------");
	
			default:
				return ("");
		}
	}
}
