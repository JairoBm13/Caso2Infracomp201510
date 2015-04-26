package Cargador;

import java.security.PublicKey;
import java.util.ArrayList;
import javax.crypto.SecretKey;
import caso2.Cliente;
import uniandes.gload.core.Task;

public class TaskClienteUnidad extends Task {

	/**
	 * Cliente que realizara 
	 */
	private Cliente cliente;


	
	private ArrayList<Datos> datos; 


	
	public TaskClienteUnidad(ArrayList<Datos> datos) {
		this.datos = datos;
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
					cliente = new Cliente(90);
				}catch(Exception e){
					cliente.setExitosa(false);
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
	
	public class Datos{
		
		private long tiempoLlave;
		private long tiempoRespuesta;
		private boolean estado;
		
		public Datos(long nTLlave, long nTRespuesta, boolean nEstado){
			tiempoLlave = nTLlave;
			tiempoRespuesta = nTRespuesta;
			setEstado(nEstado);
		}

		/**
		 * @return the tiempoLlave
		 */
		public long getTiempoLlave() {
			return tiempoLlave;
		}

		/**
		 * @param tiempoLlave the tiempoLlave to set
		 */
		public void setTiempoLlave(long tiempoLlave) {
			this.tiempoLlave = tiempoLlave;
		}

		/**
		 * @return the tiempoRespuesta
		 */
		public long getTiempoRespuesta() {
			return tiempoRespuesta;
		}

		/**
		 * @param tiempoRespuesta the tiempoRespuesta to set
		 */
		public void setTiempoRespuesta(long tiempoRespuesta) {
			this.tiempoRespuesta = tiempoRespuesta;
		}

		public boolean isEstado() {
			return estado;
		}

		public void setEstado(boolean estado) {
			this.estado = estado;
		}
	}
}
