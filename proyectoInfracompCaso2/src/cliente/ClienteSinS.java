package cliente;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.x509.X509V3CertificateGenerator;

import Cargador.Datos;
import uniandes.gload.core.Task;
/**
 * 
 * @author Jairo Bautista & Santiago Beltran Caicedo
 *
 */
public class ClienteSinS extends Task{

	//------------------------------------------------
	// Constantes
	//------------------------------------------------

	/**
	 * Direccion del servidor a usar.
	 */
	private final static String SERV = "157.253.229.68";

	/**
	 * Puerto servidor con seguridad
	 */
	private final static int PORT = 8090;

	/**
	 * Puerto del servidor sin autenticaciones de seguridad
	 */
	private final static int PORTINSEGUR = 8090;

	/**
	 * Cadena de control que indica el inicio de la conversacion
	 */
	private final static String HOLA = "HOLA";

	/**
	 * Cadena de control que indica
	 */
	private final static String ALGORITMOS = "ALGORITMOS";

	/**
	 * Cadena de control que indica una conexion exitosa
	 */
	private final static String OK = "OK";

	/**
	 * Cadena de control que indica el envio del certificado del cliente
	 */
	private final static String CERCLNT = "CERCLNT";

	/**
	 * Actualizacion 1 de pocicsion
	 */
	private final static String ACT1 = "ACT1";

	/**
	 * 
	 */
	private final static String ACT2 = "ACT2";

	/**
	 * Archivo en donde se escriben los tiempos
	 */
	private final static File ARCHIVOSS= new File("docs/tiemposSeguridad_1T_400C_1.csv");


	//-------------------------------------------------
	// Atributos
	//-------------------------------------------------

	/**
	 * El socket para realizar la conexion al servidor
	 */
	private Socket socket;

	/**
	 * Writer para enviar mensajes de control al servidor
	 */
	private PrintWriter out;

	/**
	 * Reader para leer los mensajes de control enviados por el servidor
	 */
	private BufferedReader in;

	/**
	 * Llaves privada y publica del cliente
	 */
	private KeyPair llavesCliente;

	/**
	 * Mide el tiempo de establecimiento de llave de sesion
	 */
	private static long tILlaveSesion;

	/**
	 * Mide el tiempo de establecimiento de llave de sesion
	 */
	private static long tFLlaveSesion;

	/**
	 * Mide el tiempo de la transaccion
	 */
	private static long tITransaccion;

	/**
	 * Mide el tiempo de la transaccion
	 */
	private static long tFTransaccion;

	/**
	 * Indica si la transaccion fue exitosa
	 */
	private static boolean exitosa;

	/**
	 * toma de datos
	 */
	private Datos datos;


	//-----------------------------------------------
	// Constructor
	//-----------------------------------------------

	/**
	 * 
	 * @param port
	 * @throws Exception
	 */
	public ClienteSinS(int port)  {
		try{

			socket = new Socket(SERV, PORTINSEGUR);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			datos = new Datos();
		} catch(Exception e){ 
			e.printStackTrace();
		}
	}

	/**
	 * Se inicia la comunicacion con el servidor enviando la cadena de control "HOLA"
	 */
	public boolean establecerConexion() throws Exception {

		tITransaccion = System.currentTimeMillis();
		out.println(HOLA);
		in.readLine();
		return true;
	}

	/**
	 * Manda los algoritmos que seran usados al servidor
	 */
	public boolean mandarAlgoritmos(String algos, String algoa, String algod) throws Exception{

		out.println(ALGORITMOS+":"+algos+":"+algoa+":"+algod);
		String respuesta = in.readLine();
		String estado = respuesta.split(":")[1];
		if(estado.equals(OK)){return true;}
		else{return false;}

	}

	/**
	 * Envia el certificado al servidor por un flujo de bytes
	 */
	public byte[] envioCertificado(String algAsim) throws Exception {
		byte[] certByte;
		out.println(CERCLNT);
		socket.getOutputStream().write("cosa".getBytes());
		socket.getOutputStream().flush();	
		tILlaveSesion = System.currentTimeMillis();

		return "cosa".getBytes();

	}

	/**
	 * Genera la llave privada y publica del cliente
	 * @return
	 */
	private KeyPair generarLlave(String algAsim) throws Exception{		
		KeyPairGenerator generator;

		generator = KeyPairGenerator.getInstance(algAsim);
		generator.initialize(1024);
		llavesCliente = generator.generateKeyPair();
		return llavesCliente;
	}

	/**
	 * Se recibe el certificado del servidor
	 * @return
	 */
	public void recibirCertificadoServidor() throws Exception{
		in.readLine();
		in.readLine();
	}

	/**
	 * Extrae la llave simetrica enviada por el servidor
	 * @return
	 */
	public SecretKey extraerLlavesimetrica(String algSimetrico, String algAsimetrico) throws Exception{

		String[] llaveSimInit;
		llaveSimInit = in.readLine().split(":");
		tFLlaveSesion = System.currentTimeMillis();
		Cipher cipher = Cipher.getInstance(algAsimetrico);
		cipher.init(Cipher.DECRYPT_MODE, llavesCliente.getPrivate());


		byte[] llaveSimetricaCif = DatatypeConverter.parseHexBinary(llaveSimInit[1]);
		byte[] decifrado = cipher.doFinal(llaveSimetricaCif);
		String llaveSimetrica = new String(decifrado);
		SecretKey simetrica = new SecretKeySpec(decifrado,0,decifrado.length,algSimetrico);

		return simetrica;
	}

	private String encapsular(byte[] cifrado){
		char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[cifrado.length * 2];
		for ( int j = 0; j < cifrado.length; j++ ) {
			int v = cifrado[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * Envia la pocision actualizada al servidor usando la llave simetrica obtenidad anteriormente
	 * para cifrar dicha pocision.
	 */
	public boolean actualizarUbicacion(String algHMAC, String paddingSim, String ubicacion) throws Exception {

		//===================================
		// ACT1
		//===================================

		out.println(ACT1);


		//===================================
		// ACT2
		//===================================

		out.println(ACT2);
		in.readLine();
		tFTransaccion = System.currentTimeMillis();
		return true;
	}

	public Datos getDatos(){
		return datos;
	}
	public long darTiempoLlaveSesion(){
		return tFLlaveSesion - tILlaveSesion;
	}

	public long darTimepoTransaccion(){
		return tFTransaccion - tITransaccion;
	}

	public void fail() {
		System.err.println(Task.MENSAJE_FAIL);
	}

	@Override
	public void success() {
		System.out.println(Task.OK_MESSAGE);	
	}

	@Override
	public void execute() {
		ClienteSinS cli = null;
		exitosa = true;

		try{
			cli = new ClienteSinS(PORTINSEGUR);

			String algSimetrico ="RC4";
			String algAsimetrico = "RSA";
			String algHmac = "HMACSHA256";
			String paddingSim = "RC4";

			cli.establecerConexion();

			boolean algosAceptados = cli.mandarAlgoritmos(algSimetrico, algAsimetrico, algHmac);


			cli.envioCertificado(algAsimetrico);

			cli.recibirCertificadoServidor();
			cli.actualizarUbicacion(algHmac, paddingSim,  "41242028,2104418");

			System.out.println(cli.darTiempoLlaveSesion()+","+cli.darTimepoTransaccion()+","+cli.exitosa);

			PrintWriter pW = new PrintWriter(new FileWriter(ARCHIVOSS, true));
			pW.println(cli.getDatos().getTRespuesta()+ ","+cli.getDatos().getTLlave());

			pW.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
