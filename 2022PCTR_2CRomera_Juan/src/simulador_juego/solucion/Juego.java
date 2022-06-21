package simulador_juego.solucion;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

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
