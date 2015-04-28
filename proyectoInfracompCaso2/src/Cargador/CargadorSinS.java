package Cargador;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class CargadorSinS {
	
	/**
	 * 
	 */
	private LoadGenerator cargdor;
	
	private File archivoExcelSeguro = new File("./docs/analisisConSeguridad.xlsx");

	private File archivoExcelInseguro = new File("./docs/analisisSinSeguridad.xlsx");
	
	/**
	 *  
	 */
	private WritableWorkbook excelAnalisis;

	private WritableWorkbook excelAnalisisInseguro;
	
	/**
	 * 
	 */
	public CargadorSinS(){
		Task tarea = createTask();
		int numberOfTasks = 200;
		int gapBetweenTask = 40;
		cargdor = new LoadGenerator("Carga del servidor desde el cliente", numberOfTasks, tarea, gapBetweenTask);
		cargdor.generate();
//		try {
//			excelAnalisis = Workbook.createWorkbook(archivoExcelSeguro);
//			tarea.                  
//			excelAnalisis.createSheet("1-400-20", 0);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

	}

	/**
	 * 
	 */
	private Task createTask(){
		return new TaskClienteUnidadSinS();
	}
	
	/**
	 * 
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		CargadorSinS carga = new CargadorSinS();
	}
}
