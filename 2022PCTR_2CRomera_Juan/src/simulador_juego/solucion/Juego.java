package simulador_juego.solucion;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Juego implements IJuego{
	
	private int contadorEnemigosTotales;
	private Hashtable<Integer, Integer> contadoresEnemigosTipo;
	private Hashtable<Integer, Integer> contadoresEliminadosTipo;
	private final int MAXENEMIGOS;
	private static final int MINENEMIGOS=0;
	
	//Almacenamos los hilos enemigos para poder hacer interrupt
	private List<List<Thread>> enemigos;
	
	public Juego(int numTipos, int max) {
		this.MAXENEMIGOS=max;
		contadoresEliminadosTipo=new Hashtable<Integer, Integer>();
		contadoresEnemigosTipo=new Hashtable<Integer, Integer>();
		for(int i=0;i<numTipos;i++) {
			contadoresEliminadosTipo.put(i, 0);
			contadoresEnemigosTipo.put(i, 0);
		}
		enemigos=new ArrayList<List<Thread>>(numTipos);
		for(int i=0;i<numTipos;i++) {
			enemigos.add(new ArrayList<Thread>());
		}
	}

	@Override
	public synchronized void generarEnemigo(int tipoEnemigo) {
		boolean enemigoGenerado=false;
		while(!enemigoGenerado) {
			try {
				comprobarAntesDeGenerar(tipoEnemigo);
				Integer cont=contadoresEnemigosTipo.get(tipoEnemigo);
				cont++;
				contadoresEnemigosTipo.put(tipoEnemigo, cont);
				contadorEnemigosTotales++;
				
				//Generamos el hilo enemigo y su correspondiente hilo aliado
				Thread temp=new Thread(new ActividadEnemiga(tipoEnemigo, this));
				enemigos.get(tipoEnemigo).add(temp);
				temp.start();
				new Thread(new ActividadAliada(tipoEnemigo, this)).start();
				enemigoGenerado=true;
			}catch(AssertionError e) {
				try {
					//Si no cumplimos la precondicion o se ha llegado al limite de enemigos simultaneos esperamos para generar
					//Logger.getGlobal().log(Level.INFO, "Esperando para generar "+tipoEnemigo);
					wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		imprimirInfo(tipoEnemigo, "Generado");
		checkInvariante();
		//Despues de generar el enemigo sacamos a todos los hilos del conjunto de espera, que volveran a comprobar si pueden generar un enemigo
		notifyAll();
	}

	@Override
	public synchronized void eliminarEnemigo(int tipoEnemigo) {
		comprobarAntesDeEliminar(tipoEnemigo);
		Integer temp=contadoresEnemigosTipo.get(tipoEnemigo);
		temp--;
		contadoresEnemigosTipo.put(tipoEnemigo, temp);
		temp=contadoresEliminadosTipo.get(tipoEnemigo);
		temp++;
		contadoresEliminadosTipo.put(tipoEnemigo, temp);
		
		//Interrumpimos el hilo enemigo
		int id=enemigos.get(tipoEnemigo).size()-1;
		enemigos.get(tipoEnemigo).get(id).interrupt();
		enemigos.get(tipoEnemigo).remove(id);
		contadorEnemigosTotales--;
		
		imprimirInfo(tipoEnemigo, "Eliminar");
		
		checkInvariante();
		//Despues de eliminar un enemigo notificamos a los hilos por si hay alguno en espera por haber llegado al limite de enemigos simultaneos
		notifyAll();
	}
	
	public void imprimirInfo(int tipo, String accion) {
		System.out.println(accion+" enemigo de tipo "+tipo);
		System.out.println("--> Enemigos totales: "+contadorEnemigosTotales);
		
		Enumeration<Integer> ids=contadoresEnemigosTipo.keys();
		int id;
		while(ids.hasMoreElements()) {
			id=ids.nextElement();
			System.out.println("----> Enemigos tipo "+id+": "+contadoresEnemigosTipo.get(id)+"------ [Eliminados: "+contadoresEliminadosTipo.get(id)+"]");
		}
	}
	
	public int sumarContadores() {
		Integer temp=0;
		
		Enumeration<Integer> contadores=contadoresEnemigosTipo.elements();
		while(contadores.hasMoreElements()) {
			temp+=contadores.nextElement();
		}
		
		return temp;
	}
	
	protected void checkInvariante() {
		assert sumarContadores()==contadorEnemigosTotales;
	}
	
	protected void comprobarAntesDeGenerar(int tipoEnemigo) {
		if(tipoEnemigo==0) {
			assert contadorEnemigosTotales < MAXENEMIGOS : "No se ha llegado al limite de enemigos";
		}else {
			assert contadorEnemigosTotales < MAXENEMIGOS && (contadoresEnemigosTipo.get(tipoEnemigo-1)>MINENEMIGOS||contadoresEliminadosTipo.get(tipoEnemigo-1)>MINENEMIGOS) : "No se ha llegado al limite de enemigos y se han empezado a generar enemigos del tipo anterior";
		}
	}
	
	protected void comprobarAntesDeEliminar(int tipoEnemigo) {
		assert contadoresEnemigosTipo.get(tipoEnemigo)>MINENEMIGOS : "Hay al menos un enemigo del tipo indicado para eliminar";
	}
}
