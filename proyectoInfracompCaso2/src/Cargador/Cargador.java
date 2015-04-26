package Cargador;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Cargador.TaskClienteUnidad.Datos;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;
import jxl.write.Number;

public class Cargador {

	/**
	 * 
	 */
	private LoadGenerator cargdor;

	private File archivoExcelSeguro = new File("./docs/analisisConSeguridad.xls");

	private File archivoExcelInseguro = new File("./docs/analisisSinSeguridad.xlsx");

	/**
	 *  
	 */
	private WritableWorkbook excelAnalisis;

	private WritableWorkbook excelAnalisisInseguro;

	/**
	 * 
	 */
	public Cargador(){

		ArrayList<Datos> datos = new ArrayList<Datos>(); 
		Task tarea = createTask(datos);
		int numberOfTasks = 5;
		int gapBetweenTask = 20;
		cargdor = new LoadGenerator("Carga del servidor desde el cliente", numberOfTasks, tarea, gapBetweenTask);
		cargdor.generate();
		if(!archivoExcelSeguro.exists()){
			try {               
				archivoExcelSeguro.createNewFile();
				excelAnalisis = Workbook.createWorkbook(archivoExcelSeguro);
				WritableSheet sheet = excelAnalisis.createSheet("1-400-20", 0);
				int fallos = 0;
				for (int i = 0; i < datos.size(); i++) {
					Datos actual = datos.get(i);
					Number llave = new Number(0,i+1,actual.getTiempoLlave());
					sheet.addCell(llave);
					Number ejecucion = new Number(1,i+1,actual.getTiempoRespuesta());
					sheet.addCell(ejecucion);
					boolean exito = actual.isEstado();
					if(exito){
						Label labExito = new Label(2,i+1, "Exito");						
						sheet.addCell(labExito);
					}
					else{
						Label labExito = new Label(2,i+1, "Fallo");
						sheet.addCell(labExito);
						fallos++;
					}
				}
				Number numFallos = new Number(3, 1, fallos);
				sheet.addCell(numFallos);
				excelAnalisis.write();
				excelAnalisis.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		else{
			try {
				excelAnalisis = Workbook.createWorkbook(archivoExcelSeguro);
				excelAnalisis.createSheet("1-400-20", 0);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * 
	 */
	private Task createTask(ArrayList<Datos> datos){
		return new TaskClienteUnidad(datos);
	}

	/**
	 * 
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Cargador carga = new Cargador();
	}
}
