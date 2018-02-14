package hilos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import controlador.Controlador;

public class HiloSaleImagen extends Thread {

	private Controlador controlador;
	private int puerto;
	private InetAddress suIP;
	private boolean entregado = true;
	private DatagramSocket ds;

	private byte[] imagen;

	public HiloSaleImagen(Controlador controlador, InetAddress suIP, int puerto) {
		this.setName("Hilo de salida de Imagen");
		this.controlador = controlador;
		this.suIP = suIP;
		this.puerto = puerto;
	}

	public void run() {
		controlador.addMensajeAPantalla("Enviando vídeo...");
		try {
			ds = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (controlador.isTransmitiendo()) {
			if (entregado) {
				try {
					controlador.saleImagen();
					DatagramPacket dp = new DatagramPacket(imagen, imagen.length, suIP, puerto);
					ds.send(dp);
					entregado = false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void setImagen(byte[] imagen) {
		this.imagen = imagen;
	}

	public void setEntregado() {
		entregado = true;
	}
	
	public void esperar() {
		entregado = false;
	}
}
