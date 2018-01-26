package hilos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import controlador.Controlador;

/**
 * Envia un mensaje de texto por UDP y espera la confirmacion de recepcion desde
 * el controlador.
 * 
 * @author Pablo Arienza Carrera
 * @version 26.1.2018
 */
public class HiloSaleTexto extends Thread {

	private Controlador controlador;
	private int puerto;
	private InetAddress suIP;
	private boolean entregado = false;
	private DatagramSocket ds;

	/**
	 * Constructor de la clase.
	 * 
	 * @param controlador
	 *            el controlador al que se asocia
	 * @param suIP
	 *            la direccion del interlocutor
	 * @param puerto
	 *            el puerto por el que se envian el mensaje de texto
	 */
	public HiloSaleTexto(Controlador controlador, InetAddress suIP, int puerto) {
		this.controlador = controlador;
		this.suIP = suIP;
		this.puerto = puerto;
	}// fin del constructor

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
	}// fin run

	/**
	 * Cambia el estado de no entreado a entregado.
	 */
	public void setEntregado() {
		this.entregado = true;
	}// fin setEntregado
}// fin HiloSaleTexto