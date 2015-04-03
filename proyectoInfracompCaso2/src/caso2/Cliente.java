package caso2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente implements ICliente{

	//Constantes
	/**
	 * Direccion del servidor a usar.
	 */
	public final static String SERV = "infracomp.virtual.uniandes.edu.co";
	/**
	 * Puerto servidor con seguridad
	 */
	public final static int PORT = 443;

	/**
	 * Puerto del servidor sin autenticaciones de seguridad
	 */
	public final static int PORTINSEGUR = 80;

	/**
	 * Cadena de control que indica el inicio de la conversacion
	 */
	public final static String HOLA = "HOLA";
	/**
	 * Cadena de control que indica el inicio de la conversacion por parte del servidor
	 */
	public final static String INICIO ="INICIO";

	/**
	 * Cadena de control que indica 
	 */
	public final static String ALGORITMOS = "ALGORITMOS";

	/**
	 * Cadena de control que indica el estado de la conexion
	 */
	public final static String ESTADO = "ESTADO";

	/**
	 * Cadena de control que indica una conexion exitosa
	 */
	public final static String OK = "OK";

	/**
	 * Cadena de control que indica una conexion fallida
	 */
	public final static String ERROR = "ERROR";

	/**
	 * Cadena de control que indica el envio del certificado del cliente
	 */
	public final static String CERCLNT = "CERCLNT";

	/**
	 * Cadena de control que indica el envio del certificado del servidor
	 */
	public final static String CERTSRV = "CERTSRV";

	/**
	 * Cadena de control que indica el inicio de seguridad.
	 */
	public final static String INIT = "INIT";

	//Algoritmos para tareas de cifrado

	//Simetricos
	private final static String DES = "DES";
	private final static String AES = "AES";
	private final static String Blowfish = "Blowsfish";

	//Asimetrico
	private final static String RSA = "RSA";

	//DOHASH
	private final static String HMACMD5 = "HMACMD5";
	private final static String HMACSHA1 = "HMACSHA1";
	private final static String HMACSHA256 = "HMACSHA256";

	//Atributos

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
	
	//Constructor
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
	
	@Override
	public boolean mandarAlgoritmos() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public byte[] envioCertificado() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean actualizarUbicacion() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args){
		Cliente cli = null;
		try{
		cli = new Cliente(PORT);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		cli.establecerConexion();
	}
}
