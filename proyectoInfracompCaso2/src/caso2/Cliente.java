package caso2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
	
	public final static String SERV = "infracomp.virtual.uniandes.edu.co";
	public final static int PORT = 443;
	
	public final static String HOLA = "HOLA";
	public final static String INICIO ="INICIO";
	public final static String ALGORITMOS = "ALGORITMOS";
	public final static String ESTADO = "ESTADO";
	public final static String OK = "OK";
	public final static String ERROR = "ERROR";
	public final static String CERCLNT = "CERCLNT";
	public final static String CERTSRV = "CERTSRV";
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
	 * Writer par aenviar mensajes de control al servidor
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
	public Cliente() throws Exception{
		try{
			
			socket = new Socket(SERV, PORT);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			sysIn = new BufferedReader(new InputStreamReader(System.in));
			
		} catch(Exception e){ e.printStackTrace();}
	}
	
}
