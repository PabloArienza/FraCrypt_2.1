package hilos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import controlador.Controlador;

public class HiloEntraTexto extends Thread {

	private Controlador controlador;
	private HiloTCP hiloTCP;
	private DatagramSocket ds;
	private int puerto;

	public HiloEntraTexto(Controlador controlador, HiloTCP hiloTCP, int puerto) {
		this.controlador = controlador;
		this.hiloTCP = hiloTCP;
		this.puerto = puerto;
	}

	public void run() {
		try {
			ds = new DatagramSocket(puerto);
			while (true) {
				if (controlador.isConectado()) {
					controlador.entraMensajeDeTexto("Recepción de texto preparada.");
					byte[] bytes = new byte[1024];
					DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
					ds.receive(dp);
					String mensaje = new String(dp.getData());
					controlador.entraMensajeDeTexto(mensaje);
					hiloTCP.enviarMensajeDeControl("Mensaje recibido.");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
