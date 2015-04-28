package Cargador;

public class Datos {

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
