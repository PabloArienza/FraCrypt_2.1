package hilos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import controlador.Controlador;

public class HiloSaleTexto extends Thread {

	private Controlador controlador;
	private int puerto;
	private InetAddress suIP;
	private boolean entregado = false;
	private DatagramSocket ds;

	public HiloSaleTexto(Controlador controlador, InetAddress suIP, int puerto) {
		this.controlador = controlador;
		this.suIP = suIP;
		this.puerto = puerto;
	}

	public void run() {
		try {
			ds = new DatagramSocket();
			byte[] mensaje = controlador.saleMensaje().getBytes();
			DatagramPacket dpEnviar = new DatagramPacket(mensaje, mensaje.length, suIP, puerto);
			ds.send(dpEnviar);
			while (!entregado) {
				Thread.sleep(10);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setEntregado(boolean entregado) {
		this.entregado = true;
	}
}