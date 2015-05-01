package Cargador;

public class Datos {

	private long tiempoLlave;
	private long tiempoRespuesta;
	private boolean estado;
	
	public Datos(){
		estado = true;
	}

	public void setLlaveSesion(long tiempoLlave) {
		this.tiempoLlave = tiempoLlave;
	}

	public void setRespuesta(long tiempoResp) {
		this.tiempoRespuesta = tiempoResp;
	}
	
	public void setEstado(boolean nestado){
		estado = nestado;
	}
	
	public boolean getEstado(){
		return estado;
	}
	
	public long getTLlave(){
		return tiempoLlave;
	}
	
	public long getTRespuesta(){
		return tiempoRespuesta;
	}
}
