package hilos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import controlador.Controlador;

public class HiloSaleImagen extends Thread {

	private Controlador controlador;
	private int puerto;
	private InetAddress suIP;
	private boolean entregado = false;
	private DatagramSocket ds;

	public HiloSaleImagen(Controlador controlador, InetAddress suIP, int puerto) {
		this.controlador = controlador;
		this.suIP = suIP;
		this.puerto = puerto;
	}

	public void run() {
		try {
			ds = new DatagramSocket();
			while (controlador.isConectado() && controlador.isTransmitiendo()) {
				if (controlador.isPuedeEnviarVideo()) {
					byte[] imagenParaEnviar = controlador.getImagenParaEnviar();
					DatagramPacket dp = new DatagramPacket(imagenParaEnviar, imagenParaEnviar.length, suIP, puerto);
					ds.send(dp);
					while (!entregado) {
						Thread.sleep(10);
					}
				}
			}
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void setEntregado(boolean entregado) {
		this.entregado = true;
	}
}
