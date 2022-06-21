package simulador_juego.solucion;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ActividadEnemiga implements Runnable {
	
	private int tipoEnemigo;
	private IJuego juego;
	
	private static Random generadorAleatorios=new Random();
	
	public ActividadEnemiga() {
		this.tipoEnemigo=tipoEnemigo;
		this.juego=juego;
	}

	@Override
	public void run() {
		while(true) {
			try {
				//Simulamos el comportamiento del enemigo durmiendo un timpo hasta 5 segundos
				TimeUnit.MILLISECONDS.sleep(generadorAleatorios.nextInt(5000));
			}catch(InterruptedException e) {
				//Una vez el hilo es interrumpido termina
				return;
			}
		}
	}

}
