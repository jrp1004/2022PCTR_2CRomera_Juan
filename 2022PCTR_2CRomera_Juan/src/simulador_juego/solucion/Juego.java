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
		//Inicializar contadores
		contadoresEliminadosTipo=new Hashtable<Integer, Integer>();
		contadoresEnemigosTipo=new Hashtable<Integer, Integer>();
		for(int i=0;i<numTipos;i++) {
			contadoresEliminadosTipo.put(i, 0);
			contadoresEnemigosTipo.put(i, 0);
		}
		//Inicializar array de enemigos. No necesita ser una coleccion sincronizada ya que todos sus accesos se realizan en metodos sincronizados
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
				//Si no se cumple la precondicion capturamos el error
				comprobarAntesDeGenerar(tipoEnemigo);
				//Actualizacion de contadoress
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
		//Despues de generar el enemigo mostramos por pantalla los datos y comprobamos el invariante
		imprimirInfo(tipoEnemigo, "Generado");
		checkInvariante();
		//Despues de generar el enemigo sacamos a todos los hilos del conjunto de espera, que volveran a comprobar si pueden generar un enemigo
		notifyAll();
	}

	@Override
	public synchronized void eliminarEnemigo(int tipoEnemigo) {
		//No deberia producir error puesto que el hilo aliado encargado de eliminar al enemigo se genera a la vez que este
		//No deberia haber hilos aliados sin enemigos que terminar
		comprobarAntesDeEliminar(tipoEnemigo);
		//Actualizacion de contadores
		Integer temp=contadoresEnemigosTipo.get(tipoEnemigo);
		temp--;
		contadoresEnemigosTipo.put(tipoEnemigo, temp);
		temp=contadoresEliminadosTipo.get(tipoEnemigo);
		temp++;
		contadoresEliminadosTipo.put(tipoEnemigo, temp);
		
		//Interrumpimos el hilo enemigo
		//Accedemos al ultimo elemento del array para interrumpir el hilo y eliminarlo de la lista
		int id=enemigos.get(tipoEnemigo).size()-1;
		enemigos.get(tipoEnemigo).get(id).interrupt();
		enemigos.get(tipoEnemigo).remove(id);
		contadorEnemigosTotales--;
		
		//Mostramos por pantalla los datos y comprobamos el invariante
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
		//Suma los contadores de enemigos vivos
		Integer temp=0;
		
		Enumeration<Integer> contadores=contadoresEnemigosTipo.elements();
		while(contadores.hasMoreElements()) {
			temp+=contadores.nextElement();
		}
		
		return temp;
	}
	
	protected void checkInvariante() {
		//La suma de enemigos vivos y el contador de enemigos totales es igual
		assert sumarContadores()==contadorEnemigosTotales;
	}
	
	protected void comprobarAntesDeGenerar(int tipoEnemigo) {
		if(tipoEnemigo==0) {
			//El tipoEnemigo 0 no tiene que esperar a que comience a generarse un tipo menor
			assert contadorEnemigosTotales < MAXENEMIGOS : "No se ha llegado al limite de enemigos";
		}else {
			assert contadorEnemigosTotales < MAXENEMIGOS && (contadoresEnemigosTipo.get(tipoEnemigo-1)>MINENEMIGOS||contadoresEliminadosTipo.get(tipoEnemigo-1)>MINENEMIGOS) : "No se ha llegado al limite de enemigos y se han empezado a generar enemigos del tipo anterior";
		}
	}
	
	protected void comprobarAntesDeEliminar(int tipoEnemigo) {
		assert contadoresEnemigosTipo.get(tipoEnemigo)>MINENEMIGOS : "Hay al menos un enemigo del tipo indicado para eliminar";
	}
}
