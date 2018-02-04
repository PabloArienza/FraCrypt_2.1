package hilos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import controlador.Controlador;

public class HiloRecibeImagen extends Thread {

	private Controlador controlador;
	private DatagramSocket ds;
	private int puerto;
	
	public HiloRecibeImagen(Controlador controlador, int puerto) {
		this.controlador = controlador;
		this.puerto = puerto;
	}
	
	public void run() {
		try {
			ds = new DatagramSocket(puerto);
			controlador.addMensajeAPantalla("Preparado para recibir imagen.");
			while (true) {
				byte[] bytes = new byte[65536];
				DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
				ds.receive(dp);
				controlador.entraImagen(dp.getData());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
