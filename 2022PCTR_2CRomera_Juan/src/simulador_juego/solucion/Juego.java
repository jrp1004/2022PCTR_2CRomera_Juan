package simulador_juego.solucion;

import java.util.Hashtable;

public class Juego implements IJuego{
	
	private int contadorEnemigosTotales;
	private Hashtable<Integer, Integer> contadoresEnemigosTipo;
	private Hashtable<Integer, Integer> contadoresEliminadosTipo;
	private final int MAXENEMIGOS;
	private static final int MINENEMIGOS=0;
	
	public Juego(int numTipos, int max) {
		this.MAXENEMIGOS=max;
		contadoresEliminadosTipo=new Hashtable<Integer, Integer>();
		contadoresEnemigosTipo=new Hashtable<Integer, Integer>();
		for(int i=0;i<numTipos;i++) {
			contadoresEliminadosTipo.put(i, 0);
			contadoresEnemigosTipo.put(i, 0);
		}
	}

	@Override
	public void generarEnemigo(int tipoEnemigo) {
		
	}

	@Override
	public void eliminarEnemigo(int tipoEnemigo) {
		
	}
	
}
