package controlador;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import fractal.TransformadorVideo;
import hilos.HiloCapturaCamara;
import hilos.HiloEntraImagen;
import hilos.HiloEntraTexto;
import hilos.HiloSaleImagen;
import hilos.HiloSaleTexto;
import hilos.HiloTCP;
import vista.Vista;

/**
 * Controla la comunicación entre los hilos de envio y recepcion de mensajes e
 * imagenes con la interfaz grafica.
 * 
 * @author Pablo Arienza Carrera
 * @version 26.1.2018
 */
public class Controlador implements ActionListener {

	private HiloTCP hiloTCP;
	private HiloEntraTexto hiloEntraTexto;
	private HiloSaleTexto hiloSaleTexto;
	private HiloEntraImagen hiloEntraImagen;
	private HiloSaleImagen hiloSaleImagen;
	private HiloCapturaCamara hiloCapturaCamara;

	private Vista vista;
	private TransformadorVideo transformador;

	private InetAddress miIP;
	private InetAddress suIP;

	// private int puertoTCP;
	private int puertoEntradaTexto = 9996;
	private int puertoSalidaTexto = 9996;
	private int puertoEntradaVideo = 9998;
	private int puertoSalidaVideo = 9998;

	private boolean servidor = false;

	private boolean conectado = false;
	private boolean camaraEncendida = false;
	private boolean transmitiendo = false;
	private boolean puedeEnviarVideo = true;
	private boolean encriptando = false;

	/**
	 * Constructor de la clase.
	 * 
	 * @param esServidor
	 *            define si el controlador funciona como servidor o como cliente
	 * @param puertoTCP
	 *            el puerto que se utiliza para las comunicaciones por TCP
	 * 
	 * @throws UnknownHostException
	 */
	public Controlador(boolean esServidor, int puertoTCP) throws UnknownHostException {
		this.servidor = esServidor;
		// this.puertoTCP = puertoTCP;
		this.miIP = InetAddress.getLocalHost();
		vista = new Vista(this, servidor);
		if (servidor) {
			puertoSalidaTexto++;
			puertoSalidaVideo++;
			vista.setNombreVentana("Servidor");
			hiloTCP = new HiloTCP(this, puertoTCP, null);
			vista.addMensajeAPantalla(
					"Iniciando como servidor. IP:\n" + miIP.getHostAddress() + "\nAbriendo puerto " + puertoTCP);
		} else {
			puertoEntradaTexto++;
			puertoEntradaVideo++;
			vista.setNombreVentana("Cliente");
			String h = (String) JOptionPane.showInputDialog(null, "Introduzca una IP para conectarse:");
			suIP = InetAddress.getByName(h);
			vista.addMensajeAPantalla("Iniciando como cliente.");
			hiloTCP = new HiloTCP(this, puertoTCP, h);
			vista.addMensajeAPantalla("conectando al servidor en:\n" + h + "\nPor el puerto " + puertoTCP);
		}
		hiloTCP.start();
		vista.inicializar();		
		vista.setVisible(true);
		vista.addMensajeAPantalla("Abriendo puertos de escucha..." + "\nEntrada de texto: " + puertoEntradaTexto
				+ "\nSalida de texto: " + puertoSalidaTexto + "\nEntrada de vídeo: " + puertoEntradaVideo
				+ "\nSalida de vídeo: " + puertoSalidaVideo);
		hiloEntraTexto = new HiloEntraTexto(this, hiloTCP, puertoEntradaTexto);
		hiloEntraTexto.start();
		hiloEntraImagen = new HiloEntraImagen(this, hiloTCP, puertoEntradaVideo);
		hiloEntraImagen.start();
		hiloCapturaCamara = new HiloCapturaCamara(this);
		hiloSaleImagen = new HiloSaleImagen(this, suIP, puertoSalidaVideo);
	}// fin del constructor

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case Vista.ENVIAR:
			hiloSaleTexto = new HiloSaleTexto(this, suIP, puertoSalidaTexto);
			hiloSaleTexto.start();
			break;
		case Vista.CAMARA:
			if (!camaraEncendida) {
				encenderCamara();
			} else {
				apagarCamara();
			}
			break;
		case Vista.TRANSMITIR:
			if (!transmitiendo) {
				transmitir();
			} else {
				pararTransmision();
			}
			break;
		case Vista.ENCRIPTAR:
//***************************************************************************
			
			// proceso de encriptacion
			
			
//***************************************************************************			
			break;
		}
		vista.ajustarBotones();
	}// fin actionPerformed

	/**
	 * Devuelve si es servidor o cliente.
	 *
	 * @return servidor o cliente
	 */
	public boolean isServidor() {
		return servidor;
	}// fin isServidor

	/**
	 * Devuelve si esta conectado a su interlocutor.
	 * 
	 * @return conectado a su interlocutor o no
	 */
	public boolean isConectado() {
		return conectado;
	}// fin isConectado

	/**
	 * Crea los flujos de mensajeria de control por TCP
	 * 
	 * @throws IOException
	 */
	public void setConectado() throws IOException {
		this.conectado = true;
		String soc = null;
		if (servidor) {
			soc = "Cliente";
			suIP = hiloTCP.getIPCliente();
		} else {
			soc = "Servidor";
		}
		vista.addMensajeAPantalla(soc + " conectado desde:\n" + suIP.getHostAddress());
		hiloTCP.crearFlujosDeControl();
		vista.habilitarEnviar();
		hiloTCP.enviarMensajeDeControl("hola");
		// hiloTCP.start();
	}// fin setConectado

	/**
	 * Traslada a la interfaz grafica un mensaje del interlocutor
	 * 
	 * @param mensaje
	 *            el mensaje del interlocutor
	 */
	public void entraMensajeDeTexto(String mensaje) {
		vista.addMensajeAPantalla("Él: " + mensaje);
	}// fin entraMensajeDeTexto

	/**
	 * Se envia un mensaje de la interfaz grafica.
	 * 
	 * @return el mensaje para enviar
	 */
	public String saleMensaje() {
		vista.deshabilitarEnviar();
		String mensaje = vista.enviaMensaje();
		vista.addMensajeAPantalla("Yo: " + mensaje);
		return mensaje;
	}// fin saleMensaje

	/**
	 * Cambia el estado del proceso de envio para que se termine.
	 */
	public void mensajeEntregado() {
		hiloSaleTexto.setEntregado();
		vista.habilitarEnviar();
	}// fin mensajeEntregado

	/**
	 * Traslada a la interfaz grafica una imagen recibida
	 * 
	 * @param bytes
	 *            la imagen en array de bytes
	 * 
	 * @throws IOException
	 */
	public void entraImagen(byte[] bytes) throws IOException {
		BufferedImage img = null;
		img = ImageIO.read(new ByteArrayInputStream(bytes));
		vista.pintaInterlocutor(img);
	}// fin entraImagen

	/**
	 * Envia una imagen de la camara tratada con el encriptador.
	 * 
	 * @return la imagen en array de bytes
	 * 
	 * @throws IOException
	 */
	public byte[] saleImagen() throws IOException {
		byte[] imagen = hiloCapturaCamara.getImagen();
		imagen = transformador.transformaImagen(imagen);
		vista.pintaTransmision(imagen);
		return imagen;
	}// fin saleImagen

	/**
	 * Cambia el estado del proceso de envio para que se termine.
	 */
	public void imagenEntregada() {
		hiloSaleImagen.setEntregado();
	}// fin imagenEntregada

	/**
	 * Enciende la webcam.
	 */
	@SuppressWarnings("deprecation")
	private void encenderCamara() {
		if (!hiloCapturaCamara.isAlive()) {
			hiloCapturaCamara.start();
		} else {
			hiloCapturaCamara.resume();
		}
		camaraEncendida = true;
	}// fin encenderCamara

	/**
	 * Apaga la webcam.
	 */
	@SuppressWarnings("deprecation")
	private void apagarCamara() {
		hiloCapturaCamara.suspend();
		vista.pintaCamara(null);
		vista.pintaTransmision(null);
		camaraEncendida = false;
		transmitiendo = false;
		encriptando = false;
	}// fin apagarCamra

	/**
	 * Devuelve si la webcam esta encendida o no
	 * 
	 * @return webcam encendida o no
	 */
	public boolean isCamaraEncendida() {
		return camaraEncendida;
	}// fin isCamaraEncendida

	/**
	 * Traslada a la interfaz grafica la imagen para repesentar la captura de la
	 * webcam
	 * 
	 * @param imagen
	 *            la imagen capturada de la webcam
	 */
	public void pintaCamara(Image imagen) {
		vista.pintaCamara(imagen);
	}// fin pintaCamra

	/**
	 * Devuelve si se esta transmitiendo o no
	 * 
	 * @return transmitiendo o no
	 */
	public boolean isTransmitiendo() {
		return transmitiendo;
	}// fin isTransmitiendo

	/**
	 * Devuelve si se puede enviar la siguiente imagen o no.
	 * 
	 * @return se puede enviar o no
	 */
	public boolean isPuedeEnviarVideo() {
		return puedeEnviarVideo;
	}// fin isPuedeEnviarVideo

	/**
	 * Inicia la transmision de video.
	 */
	public void transmitir() {
		transmitiendo = true;
		hiloSaleImagen.start();
	}// fin transmitir

	/**
	 * Para la transmision de video
	 */
	public void pararTransmision() {
		transmitiendo = false;
	}// fin pararTransmision

	/**
	 * Devuelve si se esta encriptando o no.
	 * 
	 * @return encriptando o no
	 */
	public boolean isEncriptando() {
		return encriptando;
	}// fin isEncriptando
}// fin Controlador
