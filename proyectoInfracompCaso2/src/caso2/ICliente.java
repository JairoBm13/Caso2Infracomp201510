package caso2;

import java.io.IOException;

public interface ICliente {

	public boolean establecerConexion() throws IOException;
	
	public boolean mandarAlgoritmos(String algos, String algoa, String algod) throws IOException;
	
	public byte[] envioCertificado();
	
	public boolean actualizarUbicacion();
}
