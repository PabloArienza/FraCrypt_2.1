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
public class HiloEntraTexto extends Thread {

	private Controlador controlador;
	private HiloTCP hiloTCP;
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
	public HiloEntraTexto(Controlador controlador, HiloTCP hiloTCP, int puerto) {
		this.controlador = controlador;
		this.hiloTCP = hiloTCP;
		this.puerto = puerto;
	}// fin del constructor

	public void run() {
		try {
			ds = new DatagramSocket(puerto);
			while (controlador.isConectado()) {
				controlador.entraMensajeDeTexto("Recepción de texto preparada.");
				byte[] bytes = new byte[1024];
				DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
				ds.receive(dp);
				String mensaje = new String(dp.getData());
				controlador.entraMensajeDeTexto(mensaje);
				hiloTCP.enviarMensajeDeControl("Mensaje recibido.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}// fin run
}// fin HiloEntraTexto
