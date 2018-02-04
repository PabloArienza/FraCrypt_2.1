package hilos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import controlador.Controlador;

/**
 * Recibe los mensajes de texto enviados por UDP del interlocutor. Cuando recibe
 * un mensaje envía un mensaje de confirmación por el canal TCP abierto.
 * 
 * @author Pablo Arienza Carrera
 * @version 26.1.2018
 */
public class HiloRecibeTexto extends Thread{

	private Controlador controlador;
	private DatagramSocket ds;
	private int puerto;

	/**
	 * Constructor de la clase
	 * 
	 * @param controlador
	 *            el cxontrolador al que se asocia
	 * @param hiloTCP
	 *            la conexion TCP por la que se envian los mensajes de confirmacion
	 *            de recepcion
	 * @param puerto
	 *            el puerto por el que se envian los mensajes
	 */
	public HiloRecibeTexto(Controlador controlador, int puerto) {
		this.controlador = controlador;
		this.puerto = puerto;
	}// fin del constructor
	
	public void run() {
		try {
			ds = new DatagramSocket(puerto);
			controlador.addMensajeAPantalla("Preparado para recibir texto.");
			while (true) {
				byte[] bytes = new byte[1024];
				DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
				ds.receive(dp);
				String mensaje = new String(dp.getData());
				controlador.entraMensajeDeTexto(mensaje);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}// fin run
}// fin HiloRecibeTexto		

