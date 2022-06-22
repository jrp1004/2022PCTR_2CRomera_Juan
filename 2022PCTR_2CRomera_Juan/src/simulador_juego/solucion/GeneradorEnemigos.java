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
	
	//El hilo recibe el tipo de enemigo a generar y la cantidad de este
	public GeneradorEnemigos(int tipoEnemigo, int numEnemigos, IJuego juego) {
		this.juego=juego;
		this.tipoEnemigo=tipoEnemigo;
		this.numEnemigos=numEnemigos;
	}
	
	@Override
	public void run() {
		//Bucle en el que se crea la cantidad necesaria de hilos
		for(int i=0;i<numEnemigos;i++) {
			try {
				//Dormimos un tiempo aleatorio hasta 3 segundos antes de crear el siguiente enemigo
				TimeUnit.MILLISECONDS.sleep(generadorAleatorios.nextInt(3000));
				juego.generarEnemigo(tipoEnemigo);
			}catch(InterruptedException e) {
				return;
			}
		}
		//Cuando genera todos los enemgios el hilo termina
		return;
	}

}
