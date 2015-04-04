package caso2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;

/**
 * 
 * @author Jairo Bautista & Santiago Beltran Caicedo
 *
 */
public class Cliente implements ICliente {

	//------------------------------------------------
	// Constantes
	//------------------------------------------------
	
	/**
	 * Direccion del servidor a usar.
	 */
	private final static String SERV = "infracomp.virtual.uniandes.edu.co";
	
	/**
	 * Puerto servidor con seguridad
	 */
	private final static int PORT = 443;

	/**
	 * Puerto del servidor sin autenticaciones de seguridad
	 */
	private final static int PORTINSEGUR = 80;

	/**
	 * Cadena de control que indica el inicio de la conversacion
	 */
	private final static String HOLA = "HOLA";
	/**
	 * Cadena de control que indica el inicio de la conversacion por parte del
	 * servidor
	 */
	private final static String INICIO = "INICIO";

	/**
	 * Cadena de control que indica
	 */
	private final static String ALGORITMOS = "ALGORITMOS";

	/**
	 * Cadena de control que indica el estado de la conexion
	 */
	private final static String ESTADO = "ESTADO";

	/**
	 * Cadena de control que indica una conexion exitosa
	 */
	private final static String OK = "OK";

	/**
	 * Cadena de control que indica una conexion fallida
	 */
	private final static String ERROR = "ERROR";

	/**
	 * Cadena de control que indica el envio del certificado del cliente
	 */
	private final static String CERCLNT = "CERCLNT";

	/**
	 * Cadena de control que indica el envio del certificado del servidor
	 */
	private final static String CERTSRV = "CERTSRV";

	/**
	 * Cadena de control que indica el inicio de seguridad.
	 */
	private final static String INIT = "INIT";

	/* Algoritmos para tareas de cifrado*/

	//--------------------------------------------
	// Simetricos
	//--------------------------------------------
	
	/**
	 * 
	 */
	private final static String DES = "DES";
	
	/**
	 * 
	 */
	private final static String AES = "AES";
	
	/**
	 * 
	 */
	private final static String Blowfish = "Blowsfish";

	//-------------------------------------------------
	// Asimetrico
	//-------------------------------------------------
	
	/**
	 * 
	 */
	private final static String RSA = "RSA";

	//-------------------------------------------------
	// DOHASH
	//-------------------------------------------------
	
	/**
	 * 
	 */
	private final static String HMACMD5 = "HMACMD5";
	
	/**
	 * 
	 */
	private final static String HMACSHA1 = "HMACSHA1";
	
	/**
	 * 
	 */
	private final static String HMACSHA256 = "HMACSHA256";

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
	 * Reader para visualizar como se lleva a cabo el protocolo
	 */
	private BufferedReader sysIn;

	//-----------------------------------------------
	// Constructor
	//-----------------------------------------------
	
	/**
	 * 
	 * @param port
	 * @throws Exception
	 */
	public Cliente(int port) throws Exception{
		try{

			socket = new Socket(SERV, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			sysIn = new BufferedReader(new InputStreamReader(System.in));

		} catch(Exception e){ e.printStackTrace();}
	}

	/**
	 * Se inicia la comunicacion con el servidor enviando la cadena de control "HOLA"
	 */
	public boolean establecerConexion() {
		try {
			System.out.println("Cliente: " + HOLA);
			out.println(HOLA);
			System.out.println("Servidor: " + in.readLine());

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 
	 */
	public boolean mandarAlgoritmos(String algos, String algoa, String algod) throws IOException {

		System.out.println("Cliente: " + ALGORITMOS+":"+algos+":"+RSA+":"+algod);
		out.println(ALGORITMOS+":"+algos+":"+RSA+":"+algod);

		String respuesta = in.readLine();

		System.out.println("Servidor: " + respuesta);
		String estado = respuesta.split(":")[1];

		if(estado.equals(OK)){return true;}
		else{return false;}
	}

	/**
	 * 
	 */
	public byte[] envioCertificado() {
		X509Certificate certificado = crearCertificado();
		byte[] certByte;
		try {
			certByte = certificado.getEncoded();
			out.println(CERCLNT);
			System.out.println("Cliente: "+CERCLNT);
			System.out.println(certByte);
			socket.getOutputStream().write(certByte);
			socket.getOutputStream().flush();	
			return certByte;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private X509Certificate crearCertificado(){
		Date startDate = new Date(System.currentTimeMillis());                
		Date expiryDate = new Date(System.currentTimeMillis() + 30L * 365L * 24L * 60L * 60L * 1000L);
		BigInteger serialNumber = new BigInteger("26");       
		
		KeyPair parDeLlaves = generarLlave();
		PrivateKey caKey = parDeLlaves.getPrivate();              
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		X500Principal  subjectName = new X500Principal("CN=Test V3 Certificate"); 
		certGen.setSerialNumber(serialNumber);
		certGen.setIssuerDN(subjectName);
		certGen.setNotBefore(startDate);
		certGen.setNotAfter(expiryDate);
		certGen.setSubjectDN(subjectName);
		certGen.setPublicKey(parDeLlaves.getPublic());
		certGen.setSignatureAlgorithm("RSA"); 
		try {
			X509Certificate cert = certGen.generate(caKey, "BC"); 
			return cert;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}   
	}
	
	/**
	 * 
	 * @return
	 */
	private KeyPair generarLlave(){		
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance(RSA);
			generator.initialize(1024);
			KeyPair keyPair = generator.generateKeyPair();
			return keyPair;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 */
	public boolean actualizarUbicacion() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		Cliente cli = null;
		try{
			cli = new Cliente(PORT);
		}catch(Exception e){
			e.printStackTrace();
		}
		cli.establecerConexion();
		try {
			cli.mandarAlgoritmos(DES, RSA, HMACMD5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cli.envioCertificado();
	}
}
