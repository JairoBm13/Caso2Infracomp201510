package Cargador;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Cliente.Cliente;
//import Cargador.TaskClienteUnidad.Datos;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;
import jxl.write.Number;

public class Cargador {
	
	public final static int PUERTOSEGURIDAD = 8080;

	/**
	 * 
	 */
	private LoadGenerator cargdor;

	/**
	 * 
	 */
	public Cargador(){

		Task tarea = new Cliente(PUERTOSEGURIDAD);
		int numberOfTasks = 400;
		int gapBetweenTask = 20;
		cargdor = new LoadGenerator("Carga del servidor desde el cliente", numberOfTasks, tarea, gapBetweenTask);
		cargdor.generate();

	}

	/**
	 * 
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Cargador carga = new Cargador();
	}

}
