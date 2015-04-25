package Cargador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import caso2.Cliente;
import uniandes.gload.core.Task;

public class TaskClienteUnidad extends Task {

	/**
	 * Cliente que realizara 
	 */
	private Cliente cliente;
	
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
			cliente = new Cliente(8080);
		}catch(Exception e){
			e.printStackTrace();
			cliente.setExitosa(false);
		}

		//Manejo de diferentes casos de algoritmos

		String algSimetrico ="";
		String algAsimetrico = "";
		String algHmac = "";
		String paddingSim = "";

		try{
			BufferedReader br = new BufferedReader(new FileReader("data/RC4_RSA_HMACSHA256"));

			algSimetrico = br.readLine().split(":")[1];
			algAsimetrico = br.readLine().split(":")[1];
			algHmac = br.readLine().split(":")[1];
			paddingSim = br.readLine().split(":")[1];
			br.close();

		} catch(Exception e){ 
			e.printStackTrace();
			cliente.setExitosa(false);
			}

		//comienzo de la comunicacion cliente a servidor
		
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
	}
}
