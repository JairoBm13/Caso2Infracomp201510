package Cargador;

import java.security.PublicKey;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import cliente.Cliente;
import uniandes.gload.core.Task;

public class TaskClienteUnidad extends Task {

	/**
	 * Cliente que realizara 
	 */
	private Cliente cliente;

	private ArrayList<Datos> datos; 



	public TaskClienteUnidad(ArrayList<Datos> datos) {
		this.datos = datos;
		cliente = null;
	}

	@Override
	public void fail() {
		System.err.println(Task.MENSAJE_FAIL);
	}

	@Override
	public void success() {
		System.out.println(Task.OK_MESSAGE);	
	}

	@Override
	public void execute() {

		try{
			cliente = new Cliente(8080, datos);
		}catch(Exception e){
			//					cliente.setExitosa(false);
			e.printStackTrace();
		}

		//Manejo de diferentes casos de algoritmos

		String algSimetrico ="RC4";
		String algAsimetrico = "RSA";
		String algHmac = "HMACSHA256";
		String paddingSim = "RC4";

		cliente.establecerConexion();

		boolean algosAceptados = cliente.mandarAlgoritmos(algSimetrico, algAsimetrico, algHmac);

		if(!algosAceptados){
			System.out.println("No se aceptaron los algoritmos");
			cliente.setExitosa(false);
		}
		else{
			cliente.envioCertificado(algAsimetrico);

			PublicKey llavePublicaServidor = cliente.recibirCertificadoServidor();

			SecretKey llaveSimetrica = cliente.extraerLlavesimetrica(algSimetrico, algAsimetrico);

			cliente.actualizarUbicacion(algHmac, paddingSim, llaveSimetrica, llavePublicaServidor,  "41242028,2104418");
		}
		Datos data = null;
		boolean exito = cliente.darTransaccionExitosa();
		if(exito){
			data = new Datos(cliente.darTiempoLlaveSesion(), cliente.darTimepoTransaccion(), exito);
		}
		else{
			data = new Datos(cliente.darTiempoFalloLlave(), cliente.darTiempoFallo(), exito);
		}
		datos.add(data);



	}


}
