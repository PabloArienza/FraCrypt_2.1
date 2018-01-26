package hilos;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import controlador.Controlador;

public class HiloEntraImagen extends Thread {

	private Controlador controlador;
	private HiloTCP hiloTCP;
	private DatagramSocket ds;
	private int puerto;

	public HiloEntraImagen(Controlador controlador, HiloTCP hiloTCP, int puerto) {
		this.controlador = controlador;
		this.hiloTCP = hiloTCP;
		this.puerto = puerto;
	}

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
	}
}
