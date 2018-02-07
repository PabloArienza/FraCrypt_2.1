package controlador;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import fractal.TransformadorVideo;
import hilos.HiloCapturaCamara;
import hilos.HiloEnviaTexto;
import hilos.HiloRecibeImagen;
import hilos.HiloRecibeTexto;
import hilos.HiloSaleImagen;
import hilos.HiloTCP;
import vista.Ventana;
import vista.Vista;

public class Controlador implements ActionListener {

	private Vista vista;
	private HiloTCP hiloTCP;
	private HiloRecibeTexto hiloRecibeTexto;
	private HiloCapturaCamara hiloCapturaCamara;
	private HiloRecibeImagen hiloRecibeImagen;
	private HiloSaleImagen hiloSaleImagen;
	private TransformadorVideo transformadorEntrada;
	private TransformadorVideo transformadorSalida;

	private InetAddress miIP;
	private InetAddress suIP;

	private int puertoTextoEntrada = 9995;
	private int puertoTextoSalida = 9995;
	private int puertoImagenEntrada = 9997;
	private int puertoImagenSalida = 9997;

	private boolean servidor;
	private boolean camaraEncendida = false;
	private boolean transmitiendo = false;
	private boolean puedeEnviarVideo = true;
	private boolean encriptando = false;

	private String passwordParaEnviar = "UNIR mola";
	private String passwordParaRecibir = "UNIR Mola";
	
	Ventana ventanaFractalSalida;
	Ventana ventanaFractalEntrada;

	public Controlador(boolean servidor, int puertoTCP) throws UnknownHostException {
		
		
		
		JTextField passwordEnviar = new JTextField();
		JTextField passwordRecibir = new JTextField();
		Object[] message = {
		    "Contraseña para ENVIAR vídeo encriptado:", passwordEnviar,
		    "Contraseña para RECIBIR vídeo encriptado:", passwordRecibir
		};

		int option = JOptionPane.showConfirmDialog(null, message, "Contraseñas", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
		    	passwordParaEnviar = passwordEnviar.getText();
		    	passwordParaRecibir = passwordRecibir.getText();
		} else {
		    JOptionPane.showMessageDialog(null, "No hay contraseñas para encriptar.");
		}
		
		
		
		
		
		this.servidor = servidor;
		transformadorEntrada = new TransformadorVideo(this, passwordParaRecibir, false);
		transformadorSalida = new TransformadorVideo(this, passwordParaEnviar, true);
		miIP = InetAddress.getLocalHost();
		vista = new Vista(this);
		vista.pintaPanelFractal(transformadorSalida.getFractal().getFractal(), transformadorSalida.getDimensiones()[0], transformadorSalida.getDimensiones()[1]);
		vista.setVisible(true);
		vista.inicializar();
		int x = vista.getLocation().x + 896;
		int y = vista.getLocation().y;
		
		ventanaFractalSalida = new Ventana(transformadorSalida.getFractal().getFractal(), transformadorSalida.getDimensiones()[0], transformadorSalida.getDimensiones()[1], x, y, "enviar");
		ventanaFractalEntrada = new Ventana(transformadorEntrada.getFractal().getFractal(), transformadorEntrada.getDimensiones()[0], transformadorEntrada.getDimensiones()[1], x, y, "recibir");
		
		
		
		if (servidor) {
			hiloTCP = new HiloTCP(this, puertoTCP);
		} else {
			String h = (String) JOptionPane.showInputDialog(null, "Introduzca una IP para conectarse:");
			if (h.equals(""))
				h = miIP.getHostAddress();
			hiloTCP = new HiloTCP(this, puertoTCP, h);
		}
		hiloTCP.start();
		if (servidor) {
			puertoTextoEntrada++;
			puertoImagenEntrada++;
		} else {
			puertoTextoSalida++;
			puertoImagenSalida++;
		}
		hiloRecibeTexto = new HiloRecibeTexto(this, puertoTextoEntrada);
		hiloRecibeTexto.start();
		hiloRecibeImagen = new HiloRecibeImagen(this, puertoImagenEntrada);
		hiloRecibeImagen.start();
		hiloCapturaCamara = new HiloCapturaCamara(this);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case Vista.ENVIAR:
			HiloEnviaTexto saleTexto = new HiloEnviaTexto(vista.getTexto(), suIP, puertoTextoSalida);
			saleTexto.start();
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
			encriptando = !encriptando;
			break;
		}
		vista.ajustarBotones();
	}

	public boolean isServidor() {
		return servidor;
	}

	public void addMensajeAPantalla(String mensaje) {
		vista.addMensajeRecibidoTCP(mensaje);
	}

	public String getSuIP() {
		return suIP.getHostAddress();
	}

	public void setSuIP(InetAddress suIP) {
		this.suIP = suIP;
	}

	public String getMiIP() {
		return miIP.getHostAddress();
	}

	public void entraMensajeDeTexto(String mensaje) {
		vista.addMensajeRecibidoTCP("Él: " + mensaje);
		hiloTCP.enviarMensaje(mensaje);
	}

	/**
	 * Devuelve si la webcam esta encendida o no
	 * 
	 * @return webcam encendida o no
	 */
	public boolean isCamaraEncendida() {
		return camaraEncendida;
	}// fin isCamaraEncendida

	/**
	 * Enciende la webcam.
	 */
	@SuppressWarnings("deprecation")
	private void encenderCamara() {
		addMensajeAPantalla("Iniciando cámara...");
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
		camaraEncendida = false;
		transmitiendo = false;
		encriptando = false;
	}// fin apagarCamra

	/**
	 * Traslada a la interfaz grafica la imagen para repesentar la captura de la
	 * webcam
	 * 
	 * @param imagen
	 *            la imagen capturada de la webcam
	 */
	public void pintaCamara(Image imagen) {
		vista.pintaCamara(imagen);
	}// fin pintaCamara

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
		hiloSaleImagen = new HiloSaleImagen(this, suIP, puertoImagenSalida);
		hiloSaleImagen.start();
	}// fin transmitir

	/**
	 * Para la transmision de video
	 */
	public void pararTransmision() {
		transmitiendo = false;
	}// fin pararTransmision

	/**
	 * Envia una imagen de la camara tratada con el encriptador.
	 * 
	 * @return la imagen en array de bytes
	 * 
	 * @throws IOException
	 */
	public void saleImagen() throws IOException {
		byte[] imagen = hiloCapturaCamara.getImagen();
		hiloSaleImagen.setImagen(imagen);
		imagen = transformadorSalida.transformaImagen(imagen);
		vista.pintaTransmision(imagen);

	}// fin saleImagen

	public void entraImagen(byte[] imagen) throws IOException {
		imagen = transformadorEntrada.transformaImagen(imagen);
		hiloTCP.enviarMensaje("Imagen Entregada.");
		vista.pintaInterlocutor(imagen);
	}

	public void setImagenEntregada() {
		hiloSaleImagen.setEntregado();
	}

	/**
	 * Devuelve si se esta encriptando o no.
	 * 
	 * @return encriptando o no
	 */
	public boolean isEncriptando() {
		return encriptando;
	}// fin isEncriptando

	public void habilitarEnvioTexto() {
		vista.habilitarEnviarTexto();
	}
}
