package simulador_juego.solucion;

public class SistemaLanzador {
	
	//Cantidad de enemigos por tipo. El indice indica el tipo
	//4 de tipo 0
	//3 de tipo 1
	//2 de tipo 2
	//1 de tipo 4
	private static int[] numEnemigosPorTipo= {4, 3, 2, 1};
	private static final int MAXENEMIGOS=5;

	public static void main(String[] args) {
		Juego juego=new Juego(numEnemigosPorTipo.length, MAXENEMIGOS);
		for(int i=0;i<numEnemigosPorTipo.length;i++) {
			new Thread(new GeneradorEnemigos(i, numEnemigosPorTipo[i], juego)).start();
		}
	}
	
}
