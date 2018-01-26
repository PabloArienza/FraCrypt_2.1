package hilos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import controlador.Controlador;

/**
 * Gestiona la conexion entre los interlocutores a traves de la que se envian
 * los mensajes de control y recepcion de mensajes de texto e imagenes.
 * 
 * @author Pablo Arienza Carrera
 * @version 26.1.2018
 */
public class HiloTCP extends Thread {

	private Controlador controlador;
	private int puerto;
	private ServerSocket sk;
	private Socket socket;
	private BufferedReader br;
	private BufferedWriter bw;
	private String host;

	/**
	 * Constructor de la clase.
	 * 
	 * @param controlador
	 *            el controlador al que se asocia
	 * @param puerto
	 *            el puerto que se usa para la conexion TCP
	 * @param host
	 *            IP del servidor cuando se inicia en modo cliente
	 */
	public HiloTCP(Controlador controlador, int puerto, String host) {
		this.controlador = controlador;
		this.puerto = puerto;
		this.host = host;
	}// fin del constructor

	/**
	 * Inicia la conexion en modo servidor.
	 */
	public void abrirModoServidor() {
		try {
			sk = new ServerSocket(puerto);
			socket = sk.accept();
			controlador.setConectado();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}// fin abrirModoServidor

	/**
	 * Inicia la conexion en modo cliente.
	 */
	public void abrirModoCliente() {
		try {
			socket = new Socket(host, puerto);
			controlador.setConectado();
		} catch (IOException e) {
			try {
				Thread.sleep(1000);
				abrirModoCliente();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}// fin abrirModoCliente

	public void run() {
		if (controlador.isServidor()) {
			abrirModoServidor();
		} else {
			abrirModoCliente();
		}
		while (controlador.isConectado()) {
			String mensaje = entraMensajeDeControl();
			if (mensaje.equals("Mensaje recibido.")) {
				controlador.mensajeEntregado();
			} else if (mensaje.equals("Imagen recibida.")) {
				controlador.imagenEntregada();
			} else if (mensaje.equals("hola")) {
				controlador.entraMensajeDeTexto(mensaje);
			}
		}
	}// fin run

	/**
	 * Lee el mensaje que ha entrado en el buffer.
	 * 
	 * @return el mensaje enviado por el interlocutor
	 */
	public String entraMensajeDeControl() {
		try {
			String mensaje = br.readLine();
			return mensaje;
		} catch (IOException e) {
			// controlador.parar();
		}
		return "";
	}// fin entraMensajeDeControl

	/**
	 * Devuelve la direccion del interlocutor
	 * 
	 * @return la direccion del interlocutor
	 */
	public InetAddress getIPCliente() {
		return socket.getInetAddress();
	}// fin getIPCliente

	/**
	 * Crea los buffers con los que se leen los mensajes recibidos y se envian
	 * cuando es necesario.
	 * 
	 * @throws IOException
	 */
	public void crearFlujosDeControl() throws IOException {
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		br = new BufferedReader(isr);
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osr = new OutputStreamWriter(os);
		bw = new BufferedWriter(osr);
	}// fin crearFlujosDeControl

	/**
	 * Envia un mensaje por TCP.
	 * 
	 * @param mensaje
	 *            el mensaje que se envia
	 * 
	 * @throws IOException
	 */
	public void enviarMensajeDeControl(String mensaje) throws IOException {
		socket.isConnected();
		bw.write(mensaje);
		bw.newLine();
		bw.flush();
	}// fin enviarMensajeDeControl
}// fin HiloTCP
