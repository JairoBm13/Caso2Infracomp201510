package Cargador;

import java.util.ArrayList;

import caso2.ClienteSinS;
import uniandes.gload.core.Task;

public class TaskClienteUnidadSinS extends Task {

	/**
	 * Cliente que realizara 
	 */
	private ClienteSinS cliente;

	private ArrayList<Datos> datos; 


	
	public TaskClienteUnidadSinS() {
		datos = new ArrayList<>();
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
					cliente = new ClienteSinS(90);
				
				//Manejo de diferentes casos de algoritmos
				
				String algSimetrico ="RC4";
				String algAsimetrico = "RSA";
				String algHmac = "HMACSHA256";
				String paddingSim = "RC4";
				
				cliente.establecerConexion();

				boolean algosAceptados = cliente.mandarAlgoritmos(algSimetrico, algAsimetrico, algHmac);

				if(!algosAceptados){
					System.out.println("No se aceptaron los algoritmos");
				}
				else{
					cliente.envioCertificado(algAsimetrico);

					cliente.actualizarUbicacion("41242028,2104418");
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
				}catch(Exception e){
//					cliente.setExitosa(false);
					e.printStackTrace();
				}	
	}
	
	public class Datos{
		
		private long tiempoLlave;
		private long tiempoRespuesta;
		private boolean estado;
		
		public Datos(long nTLlave, long nTRespuesta, boolean nEstado){
			tiempoLlave = nTLlave;
			tiempoRespuesta = nTRespuesta;
			estado = nEstado;
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
	}
}
