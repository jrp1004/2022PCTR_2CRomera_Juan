package simulador_juego.solucion;

public class SistemaLanzador {
	
	private static int[] numEnemigosPorTipo= {4, 3, 2, 1};
	private static final int MAXENEMIGOS=5;

	public static void main(String[] args) {
		Juego juego=new Juego(numEnemigosPorTipo.length, MAXENEMIGOS);
	}
	
}
