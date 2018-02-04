package hilos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import controlador.Controlador;

public class HiloEnviaTexto extends Thread {
	
	private String mensaje;
	
	private DatagramSocket ds;
	private InetAddress suIP;
	private int puerto;
	
	public HiloEnviaTexto(String mensaje, InetAddress suIP, int puerto) {
		this.mensaje = mensaje;
		this.suIP = suIP;
		this.puerto = puerto;
	}
			
	public void run() {
		try {
			ds = new DatagramSocket();
			byte[] mensajeEnBytes = mensaje.getBytes();
			DatagramPacket dpEnviar = new DatagramPacket(mensajeEnBytes, mensajeEnBytes.length, suIP, puerto);
			ds.send(dpEnviar);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
