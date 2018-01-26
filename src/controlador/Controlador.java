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

public class Controlador implements ActionListener{
	
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
	
//	private int puertoTCP;
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
	
	public Controlador(boolean esServidor, int puertoTCP) throws UnknownHostException {
		this.servidor = esServidor;
//		this.puertoTCP = puertoTCP;
		this.miIP = InetAddress.getLocalHost();
		vista = new Vista(servidor);
		if(servidor) {
			puertoSalidaTexto++;
			puertoSalidaVideo++;
			vista.setNombreVentana("Servidor");
			vista.inicializar();
			vista.setVisible(true);
			hiloTCP = new HiloTCP(this, puertoTCP, null);			
			vista.addMensajeAPantalla(
					"Iniciando como servidor. IP:\n" + miIP.getHostAddress() + "\nAbriendo puerto " + puertoTCP);			
		}else {
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
		vista.addMensajeAPantalla("Abriendo puertos de escucha..." + 
									"\nEntrada de texto: " + puertoEntradaTexto + 
									"\nSalida de texto: " + puertoSalidaTexto + 
									"\nEntrada de vídeo: " + puertoEntradaVideo + 
									"\nSalida de vídeo: " + puertoSalidaVideo);
		hiloEntraTexto = new HiloEntraTexto(this, hiloTCP,  puertoEntradaTexto);
		hiloEntraTexto.start();
		hiloEntraImagen = new HiloEntraImagen(this, hiloTCP, puertoEntradaVideo);
		hiloEntraImagen.start();
		hiloCapturaCamara = new HiloCapturaCamara(this);
		hiloSaleImagen = new HiloSaleImagen(this, suIP, puertoSalidaVideo);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case Vista.ENVIAR:
			hiloSaleTexto = new HiloSaleTexto(this, suIP, puertoSalidaTexto);
			hiloSaleTexto.start();
			break;
		case Vista.CAMARA:
			if(!camaraEncendida) {
				encenderCamara();
			}else {
				apagarCamara();
			}
			break;
		case Vista.TRANSMITIR:
			if(!transmitiendo) {
				transmitir();
			}else {
				pararTransmision();
			}
			break;
		case Vista.ENCRIPTAR:
			
			
			break;
		}
		vista.ajustarBotones();
	}
	
	public boolean isServidor() {
		return servidor;
	}
	
	public boolean isConectado() {
		return conectado;
	}
	
	public void setConectado() throws IOException {
		this.conectado = true;		
		String soc = null;
		if(servidor) {
			soc = "Cliente";
			suIP = hiloTCP.getIPCliente();
		}else {
			soc = "Servidor";
		}
		vista.addMensajeAPantalla(soc + " conectado desde:\n" + suIP.getHostAddress());
		hiloTCP.crearFlujosDeControl();
		vista.habilitarEnviar();
		hiloTCP.enviarMensajeDeControl("hola");
//		hiloTCP.start();			
	}
	
	public void entraMensajeDeTexto(String mensaje) {
		vista.addMensajeAPantalla("Él: " + mensaje);		
	}
	
	public String saleMensaje() {
		vista.deshabilitarEnviar();
		String mensaje = vista.enviaMensaje();
		vista.addMensajeAPantalla("Yo: " + mensaje);
		return mensaje;
	}
	
	public void mensajeEntregado() {
		hiloSaleTexto.setEntregado(true);
		vista.habilitarEnviar();
	}

	public void entraImagen(byte[] bytes) throws IOException {
		BufferedImage img = null;
		img = ImageIO.read(new ByteArrayInputStream(bytes));
		vista.pintaInterlocutor(img);		
	}
	
	public byte[] saleImagen() throws IOException {
		byte[] imagen = hiloCapturaCamara.getImagen();
		imagen = transformador.transformaImagen(imagen);
		vista.pintaTransmision(imagen);
		return imagen;
	}
	
	public void imagenEntregada() {
		hiloSaleImagen.setEntregado(true);
	}
	
	@SuppressWarnings("deprecation")
	private void encenderCamara() {
		if(!hiloCapturaCamara.isAlive()) {
			hiloCapturaCamara.start();
		}else {
			hiloCapturaCamara.resume();
		}
		camaraEncendida = true;
	}
	
	@SuppressWarnings("deprecation")
	private void apagarCamara() {
		hiloCapturaCamara.suspend();
		vista.pintaCamara(null);
		vista.pintaTransmision(null);
		camaraEncendida = false;
		transmitiendo = false;
		encriptando = false;
	}

	public boolean isCamaraEncendida() {
		return camaraEncendida;
	}

	public void pintaCamara(Image imagen) {
		vista.pintaCamara(imagen);
	}
	
	public boolean isTransmitiendo() {
		return transmitiendo;
	}

	public boolean isPuedeEnviarVideo() {
		return puedeEnviarVideo;
	}
	
	public byte[] getImagenParaEnviar() throws IOException {
		byte[] imagen = hiloCapturaCamara.getImagen();
		imagen = transformador.transformaImagen(imagen);
		vista.pintaTransmision(imagen);
		return imagen;
	}
	
	public void transmitir() {
		transmitiendo = true;
		hiloSaleImagen.start();
	}
	
	public void pararTransmision() {
		transmitiendo = false;
	}

	public boolean isEncriptando() {
		return encriptando;
	}
}
