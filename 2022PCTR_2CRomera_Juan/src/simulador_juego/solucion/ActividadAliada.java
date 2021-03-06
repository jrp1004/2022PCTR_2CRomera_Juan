package simulador_juego.solucion;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ActividadAliada implements Runnable {

	private IJuego juego;
	private int tipoEnemigo;
	
	private static Random generadorAleatorios=new Random();
	
	public ActividadAliada(int tipoEnemigo, IJuego juego) {
		this.tipoEnemigo=tipoEnemigo;
		this.juego=juego;
	}
	
	@Override
	public void run() {
		boolean enemigoEliminado=false;
		
		while(!enemigoEliminado) {
			//Generamos un booleano aleatorio. Si es verdadero eliminamos al enemigo si no dormimos el hilo un tiempo aleatorio
			if(generadorAleatorios.nextBoolean()) {
				juego.eliminarEnemigo(tipoEnemigo);
				enemigoEliminado=true;
			}else {
				try {
					//Dormimos un tiempo aleatorio hasta 3 segundos antes de volver a intentar eliminar al enemigo
					TimeUnit.MILLISECONDS.sleep(generadorAleatorios.nextInt(3000));
				}catch(InterruptedException e) {
					return;
				}
			}
		}
		//Una vez elimina un enemigo del tipo indicado el hilo termina
		return;
	}

}
