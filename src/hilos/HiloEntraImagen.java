package hilos;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import controlador.Controlador;

/**
 * Recibe las imagenes enviadas por UDP del interlocutor. Cuando recibe una
 * imagen envía un mensaje de confirmación por el canal TCP abierto.
 * 
 * @author Pablo Arienza Carrera
 * @version 26.1.2018
 */
public class HiloEntraImagen extends Thread {

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
	 *            el puerto por el que se envian las imagenes
	 */
	public HiloEntraImagen(Controlador controlador, HiloTCP hiloTCP, int puerto) {
		this.controlador = controlador;
		this.hiloTCP = hiloTCP;
		this.puerto = puerto;
	}// fin del constructor

	public void run() {
		try {
			ds = new DatagramSocket(puerto);
			while (true) {
				if (controlador.isConectado()) {
					controlador.entraMensajeDeTexto("Recepción de vídeo preparada.");
					byte[] b1 = new byte[65536];
					DatagramPacket dp = new DatagramPacket(b1, b1.length);
					ds.receive(dp);
					controlador.entraImagen(dp.getData());
					hiloTCP.enviarMensajeDeControl("Imagen recibida.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// fin run
}// fin HiloEntraImagen
