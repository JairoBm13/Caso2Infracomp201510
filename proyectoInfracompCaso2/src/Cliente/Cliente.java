package Cliente;

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
public class Cliente extends Task implements ICliente{

	//------------------------------------------------
	// Constantes
	//------------------------------------------------

	/**
	 * Direccion del servidor a usar.
	 */
	private final static String SERV = "192.168.0.11";

	/**
	 * Puerto servidor con seguridad
	 */
	private final static int PORT = 8080;

	/**
	 * Puerto del servidor sin autenticaciones de seguridad
	 */
	private final static int PORTINSEGUR = 80;

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
	private final static File ARCHIVOS= new File("docs/Seguridad/Carga400/1Thread/medicion10.csv");

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
	private long tILlaveSesion;

	/**
	 * Mide el tiempo de establecimiento de llave de sesion
	 */
	private long tFLlaveSesion;

	/**
	 * Mide el tiempo de la transaccion
	 */
	private long tITransaccion;

	/**
	 * Mide el tiempo de la transaccion
	 */
	private long tFTransaccion;

	/**
	 * Indica si la transaccion fue exitosa
	 */
	private boolean exitosa;

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
	public Cliente(int port) {
		try{

			socket = new Socket(SERV, port);
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

		//			System.out.println("Cliente: " + HOLA);
		out.println(HOLA);
		this.tITransaccion = System.currentTimeMillis();
		in.readLine();
		//			System.out.println("Servidor: " + in.readLine());
		return true;
	}

	/**
	 * Manda los algoritmos que seran usados al servidor
	 */
	public boolean mandarAlgoritmos(String algos, String algoa, String algod) throws Exception{


		//			System.out.println("Cliente: " + ALGORITMOS+":"+algos+":"+algoa+":"+algod);
		out.println(ALGORITMOS+":"+algos+":"+algoa+":"+algod);

		String respuesta = in.readLine();

		//			System.out.println("Servidor: " + respuesta);
		String estado = respuesta.split(":")[1];

		if(estado.equals(OK)){return true;}
		else{return false;}


	}

	/**
	 * Envia el certificado al servidor por un flujo de bytes
	 */
	public byte[] envioCertificado(String algAsim) throws Exception{
		X509Certificate certificado = crearCertificado(algAsim);
		byte[] certByte;

		certByte = certificado.getEncoded();
		out.println(CERCLNT);
		socket.getOutputStream().write(certByte);
		socket.getOutputStream().flush();	
		this.tILlaveSesion = System.currentTimeMillis();

		return certByte;

	}

	/**
	 * Crea el certificado digital del cliente
	 * @return
	 */
	private X509Certificate crearCertificado(String algAsim) throws Exception{

		Date startDate = new Date(System.currentTimeMillis());                
		Date expiryDate = new Date(System.currentTimeMillis() + 30L * 365L * 24L * 60L * 60L * 1000L);
		BigInteger serialNumber = new BigInteger("26");       

		KeyPair llavesCliente = generarLlave(algAsim);
		PrivateKey caKey = llavesCliente.getPrivate();              
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		X500Principal  subjectName = new X500Principal("CN=Test V3 Certificate"); 
		certGen.setSerialNumber(serialNumber);
		certGen.setIssuerDN(subjectName);
		certGen.setNotBefore(startDate);
		certGen.setNotAfter(expiryDate);
		certGen.setSubjectDN(subjectName);
		certGen.setPublicKey(llavesCliente.getPublic());
		certGen.setSignatureAlgorithm("SHA256WithRSAEncryption"); 

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());


		X509Certificate cert = certGen.generate(caKey, "BC"); 

		//			System.out.println("Cliente: "+CERCLNT);
		//			System.out.println("------------------------------------------------------------------");
		//			System.out.println(cert.toString());
		//			System.out.println("------------------------------------------------------------------");

		return cert;
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
	public PublicKey recibirCertificadoServidor() throws Exception{
		//			System.out.println("Servidor: " +  in.readLine());
		in.readLine();
		CertificateFactory  cf = CertificateFactory.getInstance("X.509");
		Certificate certificate = cf.generateCertificate(socket.getInputStream());getClass();

		//			System.out.println("------------------------------------------------------------------");
		//			System.out.println(certificate.toString());
		//			System.out.println("------------------------------------------------------------------");
		//
		//			System.out.println("Llave publica servidor: " + certificate.getPublicKey());
		//			System.out.println();
		return certificate.getPublicKey();


	}

	/**
	 * Extrae la llave simetrica enviada por el servidor
	 * @return
	 */
	public SecretKey extraerLlavesimetrica(String algSimetrico, String algAsimetrico) throws Exception{


		String[] llaveSimInit;
		llaveSimInit = in.readLine().split(":");
		this.tFLlaveSesion = System.currentTimeMillis();

		//				System.out.println("Servidor: " + llaveSimInit[0] + ": " + llaveSimInit[1]);

		Cipher cipher = Cipher.getInstance(algAsimetrico);
		cipher.init(Cipher.DECRYPT_MODE, llavesCliente.getPrivate());


		byte[] llaveSimetricaCif = DatatypeConverter.parseHexBinary(llaveSimInit[1]);
		byte[] decifrado = cipher.doFinal(llaveSimetricaCif);
		String llaveSimetrica = new String(decifrado);

		//				System.out.println("Llave simetrica: " + llaveSimetrica);
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
	public boolean actualizarUbicacion(String algHMAC, String paddingSim, SecretKey llaveSim ,PublicKey llavePubServidor, String ubicacion) throws Exception {



		//===================================
		// ACT1
		//===================================

		//			System.out.println("Posicion Actual: " + ubicacion);

		Cipher cipher = Cipher.getInstance(paddingSim);
		cipher.init(Cipher.ENCRYPT_MODE, llaveSim);

		byte[] ubicacionCifrada = cipher.doFinal(ubicacion.getBytes());
		out.println(ACT1+":"+encapsular(ubicacionCifrada));

		//			System.out.println("Cliente: "+ACT1+":"+ encapsular(ubicacionCifrada));

		//===================================
		// ACT2
		//===================================

		Mac mac = Mac.getInstance(algHMAC);
		SecretKey secret = new SecretKeySpec(llaveSim.getEncoded(), algHMAC);
		mac.init(secret);
		mac.update(ubicacion.getBytes());
		byte[] macCoord = mac.doFinal();

		cipher = Cipher.getInstance(llavePubServidor.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, llavePubServidor);
		byte[] integridad =cipher.doFinal(macCoord);

		out.println(ACT2+":"+encapsular(integridad));

		in.readLine();
		this.tFTransaccion = System.currentTimeMillis();
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
		Cliente cli = null;
		exitosa = true;

		try{
			cli = new Cliente(PORT);
			exitosa = false;
			String algSimetrico ="RC4";
			String algAsimetrico = "RSA";
			String algHmac = "HMACSHA256";
			String paddingSim = "RC4";
			cli.establecerConexion();
			boolean algosAceptados = cli.mandarAlgoritmos(algSimetrico, algAsimetrico, algHmac);

			cli.envioCertificado(algAsimetrico);

			PublicKey llavePublicaServidor = cli.recibirCertificadoServidor();

			SecretKey llaveSimetrica = cli.extraerLlavesimetrica(algSimetrico, algAsimetrico);

			cli.actualizarUbicacion(algHmac, paddingSim, llaveSimetrica, llavePublicaServidor,  "41242028,2104418");

			cli.getDatos().setLlaveSesion(cli.darTiempoLlaveSesion());
			cli.getDatos().setRespuesta(cli.darTimepoTransaccion());

			System.out.println(cli.getDatos().getTRespuesta()+ ","+cli.getDatos().getTLlave());

			PrintWriter pW = new PrintWriter(new FileWriter(ARCHIVOS, true));
			pW.println(cli.getDatos().getTRespuesta()+ ","+cli.getDatos().getTLlave());

			pW.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}



	}
}
