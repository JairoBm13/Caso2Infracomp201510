/**
 * 
 */
package Servidor;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Esta clase implementa el servidor que atiende a los clientes. El servidor 
 * esta implemntado como un pool de threads. Cada vez que un cliente crea
 * una conexion al servidor, un thread se encarga de atenderlo el tiempo que
 * dure la sesion. 
 * Infraestructura Computacional Universidad de los Andes. 
 * Las tildes han sido eliminadas por cuestiones de compatibilidad.
 * 
 * @author Michael Andres Carrillo Pinzon 	-  201320.
 * @author José Miguel Suárez Lopera 		-  201510
 */
public class Servidor {

	/**
	 * Constante que especifica el tiempo máximo en milisegundos que se esperara 
	 * por la respuesta de un cliente en cada una de las partes de la comunicación
	 */
	private static final int TIME_OUT = 10000;

	/**
	 * Constante que especifica el numero de threads que se usan en el pool de conexiones.
	 */
	public static final int N_THREADS = 1;

	/**
	 * Puerto en el cual escucha el servidor.
	 */
	public static final int PUERTO = 8080;

	/**
	 * Metodo main del servidor con seguridad que inicializa un 
	 * pool de threads determinado por la constante nThreads.
	 * @param args Los argumentos del metodo main (vacios para este ejemplo).
	 * @throws IOException Si el socket no pudo ser creado.
	 */
	public static void main(String[] args) throws IOException {

		// Adiciona la libreria como un proveedor de seguridad.
		// Necesario para crear llaves.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		
		new Servidor().iniciarCom();
	}

	/**
	 * Metodo que atiende a los usuarios.
	 */
	public void iniciarCom() {
		final ExecutorService pool = Executors.newFixedThreadPool(N_THREADS);

		Runnable serverRun = new Runnable(){

			@Override
			public void run() {
				ServerSocket servSock = null;
				try{
					servSock = new ServerSocket(PUERTO);
					System.out.println("Listo para recibir conexiones");
					while(true){
						Socket cliente = servSock.accept();
						
						cliente.setSoTimeout(TIME_OUT);
						pool.execute(new Protocolo(cliente));
					}
				}catch(Exception e){
					System.err.println("Ocurrio un error");
					e.printStackTrace();
				}finally{
					try{
					servSock.close();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		};
		Thread serverT = new Thread(serverRun);
		serverT.start();
	}

}
