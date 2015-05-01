package cliente;

import java.io.IOException;
import java.security.PublicKey;

import javax.crypto.SecretKey;

public interface ICliente {

	public boolean establecerConexion() throws Exception;
	
	public boolean mandarAlgoritmos(String algos, String algoa, String algod) throws Exception;
	
	public byte[] envioCertificado(String algAsim) throws Exception;
	
	public boolean actualizarUbicacion(String algHmac, String paddindSim, SecretKey llaveSim, PublicKey llavePubServ , String ubicacion) throws Exception;
}
