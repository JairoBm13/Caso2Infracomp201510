package caso2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.crypto.macs.HMac;
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
	 * Manda los algoritmos que seran usados al servidor
	 */
	public boolean mandarAlgoritmos(String algos, String algoa, String algod){

		try{
			System.out.println("Cliente: " + ALGORITMOS+":"+algos+":"+algoa+":"+algod);
			out.println(ALGORITMOS+":"+algos+":"+algoa+":"+algod);

			String respuesta = in.readLine();

			System.out.println("Servidor: " + respuesta);
			String estado = respuesta.split(":")[1];

			if(estado.equals(OK)){return true;}
			else{return false;}
		}

		catch(Exception e){ 
			e.printStackTrace();
			return false; 
		}
	}

	/**
	 * Envia el certificado al servidor por un flujo de bytes
	 */
	public byte[] envioCertificado(String algAsim) {
		X509Certificate certificado = crearCertificado(algAsim);
		byte[] certByte;
		try {

			certByte = certificado.getEncoded();
			out.println(CERCLNT);
			socket.getOutputStream().write(certByte);
			socket.getOutputStream().flush();	

			return certByte;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Crea el certificado digital del cliente
	 * @return
	 */
	private X509Certificate crearCertificado(String algAsim){
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
		certGen.setSignatureAlgorithm("MD5withRSA"); 

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		try {
			X509Certificate cert = certGen.generate(caKey, "BC"); 

			System.out.println("Cliente: "+CERCLNT);
			System.out.println("------------------------------------------------------------------");
			System.out.println(cert.toString());
			System.out.println("------------------------------------------------------------------");

			return cert;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}   
	}

	/**
	 * Genera la llave privada y publica del cliente
	 * @return
	 */
	private KeyPair generarLlave(String algAsim){		
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance(algAsim);
			generator.initialize(1024);
			llavesCliente = generator.generateKeyPair();
			return llavesCliente;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Se recibe el certificado del servidor
	 * @return
	 */
	public PublicKey recibirCertificadoServidor(){
		try {
			System.out.println("Servidor: " +  in.readLine());
			CertificateFactory  cf = CertificateFactory.getInstance("X.509");
			Certificate certificate = cf.generateCertificate(socket.getInputStream());getClass();
			
			System.out.println("------------------------------------------------------------------");
			System.out.println(certificate.toString());
			System.out.println("------------------------------------------------------------------");

			System.out.println("Llave publica servidor: " + certificate.getPublicKey());
			System.out.println();
			return certificate.getPublicKey();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Extrae la llave simetrica enviada por el servidor
	 * @return
	 */
	public SecretKey extraerLlavesimetrica(String algSimetrico, String algAsimetrico){

		try{

			String[] llaveSimInit = in.readLine().split(":");
			System.out.println("Servidor: " + llaveSimInit[0] + ": " + llaveSimInit[1]);

			Cipher cipher = Cipher.getInstance(algAsimetrico);
			cipher.init(Cipher.DECRYPT_MODE, llavesCliente.getPrivate());

			byte[] llaveSimetricaCif = DatatypeConverter.parseHexBinary(llaveSimInit[1]);
			byte[] decifrado = cipher.doFinal(llaveSimetricaCif);
			String llaveSimetrica = new String(decifrado);

			System.out.println("Llave simetrica: " + llaveSimetrica);
			SecretKey simetrica = new SecretKeySpec(decifrado,0,decifrado.length,algSimetrico);

			return simetrica;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Envia la pocision actualizada al servidor usando la llave simetrica obtenidad anteriormente
	 * para cifrar dicha pocision.
	 */
	public boolean actualizarUbicacion(String algHMAC, String paddingSim, SecretKey llaveSim ,PublicKey llavePubServidor, String ubicacion) {

		try {

			//===================================
			// ACT1
			//===================================
			
			Cipher cipher = Cipher.getInstance(paddingSim);
			cipher.init(Cipher.ENCRYPT_MODE, llaveSim);

			byte[] ubicacionCifrada = cipher.doFinal(ubicacion.getBytes());

			out.print(ACT1+":");
			socket.getOutputStream().write(ubicacionCifrada);
			socket.getOutputStream().flush();
			System.out.print("Cliente: "+ACT1+":");

			for(int i =0; i<ubicacionCifrada.length;i++){
				System.out.print(ubicacionCifrada[i]);
			}
			System.out.println();

			//===================================
			// ACT2
			//===================================
			
//			HMac mac = new HMac(new Digest
			Mac mac = Mac.getInstance(algHMAC);
			mac.init(llaveSim);
			byte[] macCoord = mac.doFinal(ubicacion.getBytes());
			
			cipher = Cipher.getInstance(llavePubServidor.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, llavePubServidor);
			byte[] integridad =cipher.doFinal(macCoord);
			
			
			out.print(ACT2+":");
			socket.getOutputStream().write(integridad);
			socket.getOutputStream().flush();
			
//			System.out.print("Cliente: "+ACT2+":");
//
//			for(int i =0; i<macCoord.length;i++){
//				System.out.print(macCoord[i]);
//			}

			
			System.out.println("Servidor: "+in.readLine());
			
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args){
		Cliente cli = null;
		try{
			cli = new Cliente(PORT);
		}catch(Exception e){
			e.printStackTrace();
		}

		//Manejo de diferentes casos de algoritmos

		String algSimetrico ="";
		String algAsimetrico = "";
		String algHmac = "";
		String paddingSim = "";

		try{
			BufferedReader br = new BufferedReader(new FileReader("data/RC4_RSA_HMACMD5"));

			algSimetrico = br.readLine().split(":")[1];
			algAsimetrico = br.readLine().split(":")[1];
			algHmac = br.readLine().split(":")[1];
			paddingSim = br.readLine().split(":")[1];

		} catch(Exception e){ e.printStackTrace();}

		//comienzo de la comunicacion cliente a servidor

		cli.establecerConexion();

		boolean algosAceptados = cli.mandarAlgoritmos(algSimetrico, algAsimetrico, algHmac);

		if(!algosAceptados)
			System.out.println("No se aceptaron los algoritmos");

		else{
			cli.envioCertificado(algAsimetrico);

			PublicKey llavePublicaServidor = cli.recibirCertificadoServidor();

			SecretKey llaveSimetrica = cli.extraerLlavesimetrica(algSimetrico, algAsimetrico);

			cli.actualizarUbicacion(algHmac, paddingSim, llaveSimetrica, llavePublicaServidor,  "41 24.2028 2 10.4418");
		}
	}
}
