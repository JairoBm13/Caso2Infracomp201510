package Cargador;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Cliente.Cliente;
import Cliente.ClienteSinS;
//import Cargador.TaskClienteUnidad.Datos;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;
import jxl.write.Number;

public class CargadorSinS {

	public final static int PUERTOSEGURIDAD = 8090;
	/**
	 * 
	 */
	private LoadGenerator cargdor;

	/**
	 * 
	 */
	public CargadorSinS(){

		Task tarea = new ClienteSinS(8090);
		int numberOfTasks = 80;
		int gapBetweenTask = 100;
		cargdor = new LoadGenerator("Carga del servidor desde el cliente", numberOfTasks, tarea, gapBetweenTask);
		cargdor.generate();
	}

	/**
	 * 
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		CargadorSinS carga = new CargadorSinS();
	}
}
