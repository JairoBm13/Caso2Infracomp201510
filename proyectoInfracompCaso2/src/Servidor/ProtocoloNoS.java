package Servidor;

import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.Semaphore;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.security.cert.CertificateNotYetValidException;

/**
 * Esta clase implementa el protocolo que se realiza al recibir una conexión de un cliente.
 * Infraestructura Computacional Universidad de los Andes. 
 * Las tildes han sido eliminadas por cuestiones de compatibilidad.
 * 
 * @author Michael Andres Carrillo Pinzon 	-  201320.
 * @author José Miguel Suárez Lopera 		-  201510
 */
public class ProtocoloNoS extends Thread {

	// ----------------------------------------------------
	// CONSTANTES DE CONTROL DE IMPRESION EN CONSOLA
	// ----------------------------------------------------
	public static final boolean SHOW_ERROR = true;
	public static final boolean SHOW_S_TRACE = false;
	public static final boolean SHOW_IN = false;
	public static final boolean SHOW_OUT = false;
	// ----------------------------------------------------
	// CONSTANTES PARA LA DEFINICION DEL PROTOCOLO
	// ----------------------------------------------------
	public static final String STATUS = "ESTADO";
	public static final String ACK = "INICIO";
	public static final String OK = "OK";
	public static final String ALGORITMOS = "ALGORITMOS";
	public static final String DES = "DES";
	public static final String AES = "AES";
	public static final String BLOWFISH = "Blowfish";
	public static final String RSA = "RSA";
	public static final String RC4 = "RC4";
	public static final String HMACMD5 = "HMACMD5";
	public static final String HMACSHA1 = "HMACSHA1";
	public static final String HMACSHA256 = "HMACSHA256";
	public static final String CERTSRV = "CERTSRV";
	public static final String CERCLNT = "CERCLNT";
	public static final String SEPARADOR = ":";
	public static final String HOLA = "HOLA";
	public static final String INIT = "INIT";
	public static final String ACT1 = "ACT1";
	public static final String ACT2 = "ACT2";
	public static final String RTA = "RTA";
	public static final String INFO = "INFO";
	public static final String ERROR = "ERROR";
	public static final String ERROR_FORMATO = "Error en el formato. Cerrando conexion";
	
	private final Socket sockCliente;
	
	private InputStream in;
	
	private OutputStream out;
	
	public ProtocoloNoS(Socket s){
		this.sockCliente = s;
	}
	/**
	 * Metodo que se encarga de imprimir en consola todos los errores que se 
	 * producen durante la ejecuación del protocolo. 
	 * Ayuda a controlar de forma rapida el cambio entre imprimir y no imprimir este tipo de mensaje
	 */
	private void printError(Exception e) {
		if(SHOW_ERROR)		System.out.println(e.getMessage());
		if(SHOW_S_TRACE) 	e.printStackTrace();	
	}

	/**
	 * Metodo que se encarga de leer los datos que envia el cliente.
	 *  Ayuda a controlar de forma rapida el cambio entre imprimir y no imprimir este tipo de mensaje
	 */
	private String read(BufferedReader reader) throws IOException {
		String linea = reader.readLine();
		if(SHOW_IN)			System.out.println("<<CLNT: " + linea);
		return linea;
	}

	/**
	 * Metodo que se encarga de escribir los datos que el servidor envia el cliente.
	 *  Ayuda a controlar de forma rapida el cambio entre imprimir y no imprimir este tipo de mensaje
	 */
	private void write(PrintWriter writer, String msg) {
		writer.println(msg);
		if(SHOW_OUT)		System.out.println(">>SERV: " + msg);
	}

	public void atenderCliente(){
		try{
			out = sockCliente.getOutputStream();
			in = sockCliente.getInputStream();
			PrintWriter writer = new PrintWriter(out, true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			// ////////////////////////////////////////////////////////////////////////
			// Recibe HOLA.
			// En caso de error de formato, cierra la conexion.
			// ////////////////////////////////////////////////////////////////////////

			String linea = read(reader);
			if (!linea.equals(HOLA)) {
				write(writer, ERROR_FORMATO);

				throw new FontFormatException(linea);
			}

			// ////////////////////////////////////////////////////////////////////////
			// Envia el status del servidor
			// ////////////////////////////////////////////////////////////////////////
			write(writer, ACK);

			linea = read(reader);
			if (!(linea.contains(SEPARADOR) && linea.split(SEPARADOR)[0].equals(ALGORITMOS))) {
				write(writer, ERROR_FORMATO);
				throw new FontFormatException(linea);
			}

			// Verificar los algoritmos enviados
			String[] algoritmos = linea.split(SEPARADOR);
			// Comprueba y genera la llave simetrica para comunicarse con el
			// servidor.
			if (!algoritmos[1].equals(DES) && !algoritmos[1].equals(AES) && !algoritmos[1].equals(BLOWFISH)
					&& !algoritmos[1].equals(RC4)) {

				write(writer, "ERROR:Algoritmo no soportado o no reconocido: " + algoritmos[1] + ". Cerrando conexion");
				throw new NoSuchAlgorithmException();
			}

			// Comprueba que el algoritmo asimetrico sea RSA.
			if (!algoritmos[2].equals(RSA)) {
				write(writer, "ERROR:Algoritmo no soportado o no reconocido: " + algoritmos[2] + ". Cerrando conexion");
				throw new NoSuchAlgorithmException();
			}
			// Comprueba que el algoritmo HMAC sea valido.
			if (!(algoritmos[3].equals(HMACMD5) || algoritmos[3].equals(HMACSHA1) || algoritmos[3]
					.equals(HMACSHA256))) {
				write(writer, "Algoritmo no soportado o no reconocido: " + algoritmos[3] + ". Cerrando conexion");
				throw new NoSuchAlgorithmException();
			}

			// Confirmando al cliente que los algoritmos son soportados.
			write(writer, STATUS + SEPARADOR + OK);

			// ////////////////////////////////////////////////////////////////////////
			// Recibiendo el certificado del cliente
			// ////////////////////////////////////////////////////////////////////////
			// byte[] receiver = new byte[7];
			// int read = s.getInputStream().read(receiver, 0, 7);

			linea = read(reader);
			if (!linea.equals(CERCLNT)) {
				write(writer, ERROR_FORMATO + ":" + linea);
				throw new FontFormatException(CERCLNT);
			}

			byte[] certificadoServidorBytes = new byte[520];
			sockCliente.getInputStream().read(certificadoServidorBytes);	

			// ////////////////////////////////////////////////////////////////////////
			// Enviando el certificado del servidor.
			// ////////////////////////////////////////////////////////////////////////
			write(writer, CERTSRV);
			out.write("CertificadoServ".getBytes());
			out.flush();
			

			// ////////////////////////////////////////////////////////////////////////
			// Genera llave simetrica y la envia al cliente
			// ////////////////////////////////////////////////////////////////////////

			// Transforma la llave simertrica y la envia
			write(writer, INIT);

			// ////////////////////////////////////////////////////////////////////////
			// Recibe la posicion del usuario.
			// ////////////////////////////////////////////////////////////////////////
			
			linea = read(reader);
			
			if (!(linea.equals(ACT1))) {
				write(writer, ERROR_FORMATO);
				throw new FontFormatException(linea);
			}

			linea = read(reader);
			if (!(linea.equals(ACT2))) {
				write(writer, ERROR_FORMATO);
				throw new FontFormatException(linea);
			}

			// ////////////////////////////////////////////////////////////////////////
			// Recibe el resultado de la transaccion y termina la conexion.
			// ////////////////////////////////////////////////////////////////////////
			write(writer, RTA + SEPARADOR + OK);

			System.out.println("Termino requerimientos del cliente en perfectas condiciones.");
		} catch (NullPointerException e) {
			// Probablemente la conexion fue interrumpida.
			printError(e);
		} catch (IOException e) {
			// Error en la conexion con el cliente.
			printError(e);
		} catch (FontFormatException e) {
			// Si hubo errores en el protocolo por parte del cliente.
			printError(e);
		} catch (NoSuchAlgorithmException e) {
			// Si los algoritmos enviados no son soportados por el servidor.
			printError(e);
		} catch (IllegalStateException e) {
			// El certificado no se pudo generar.
			// No deberia alcanzarce en condiciones normales de ejecuci��n.
			printError(e);
		} catch (Exception e) {
			// El cliente reporto que la informacion fue infructuosa.
			printError(e);
		} finally {
			try{
				in.close();
				out.close();
				sockCliente.close();
			} catch (Exception e) {
				// DO NOTHING
			}
		}
	}

	public void run(){
		atenderCliente();
	}

}
