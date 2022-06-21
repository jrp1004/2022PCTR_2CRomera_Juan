package simulador_juego.solucion;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Clase encargada de generar hilos de un tipo enemigo concreto en intervalos de tiempo aleatorios
 * @author romer
 *
 */
public class GeneradorEnemigos implements Runnable {

	private int tipoEnemigo;
	private int numEnemigos;
	private IJuego juego;
	
	private static Random generadorAleatorios=new Random();
	
	public GeneradorEnemigos(int tipoEnemigo, int numEnemigos, IJuego juego) {
		this.juego=juego;
		this.tipoEnemigo=tipoEnemigo;
		this.numEnemigos=numEnemigos;
	}
	
	@Override
	public void run() {
		for(int i=0;i<numEnemigos;i++) {
			try {
				TimeUnit.MILLISECONDS.sleep(generadorAleatorios.nextInt(3000));
				juego.generarEnemigo(tipoEnemigo);
			}catch(InterruptedException e) {
				return;
			}
		}
		return;
	}

}
