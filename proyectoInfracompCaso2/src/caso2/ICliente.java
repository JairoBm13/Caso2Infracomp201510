package caso2;

import java.io.IOException;
import java.security.PublicKey;

import javax.crypto.SecretKey;

public interface ICliente {

	public boolean establecerConexion() throws IOException;
	
	public boolean mandarAlgoritmos(String algos, String algoa, String algod) throws IOException;
	
	public byte[] envioCertificado(String algAsim);
	
	public boolean actualizarUbicacion(String paddindSim, SecretKey llaveSim, PublicKey llavePubServ , String ubicacion);
}
