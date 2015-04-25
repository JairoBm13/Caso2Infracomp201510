package Cargador;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Cargador {
	
	/**
	 * 
	 */
	private LoadGenerator cargdor;
	
	/**
	 * 
	 */
	public Cargador(){
		Task tarea = createTask();
		int numberOfTasks = 200;
		int gapBetweenTask = 20;
		cargdor = new LoadGenerator("Carga del servidor desde el cliente", numberOfTasks, tarea, gapBetweenTask);
		cargdor.generate();
	}

	/**
	 * 
	 */
	private Task createTask(){
		return new TaskClienteUnidad();
	}
	
	/**
	 * 
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Cargador carga = new Cargador();
	}
}
