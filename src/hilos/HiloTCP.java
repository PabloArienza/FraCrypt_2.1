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

public class HiloTCP extends Thread {
	
	private Controlador controlador;
	
	private int puerto;

	private ServerSocket sk;

	private Socket socket;

	private BufferedReader br;

	private BufferedWriter bw;

	private String host;
	
	public HiloTCP(Controlador controlador, int puerto, String host) {
		this.controlador = controlador;
		this.puerto = puerto;
		this.host = host;
	}
	
	public void abrirModoServidor() {
		try {
			sk = new ServerSocket(puerto);
			socket = sk.accept();
			controlador.setConectado();			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void abrirModoCliente() {
		try {
			socket = new Socket(host, puerto);
			controlador.setConectado();		
		} catch (IOException e) {
			try {
				Thread.sleep(1000);
				abrirModoCliente(); 
			}catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void run() {
		if(controlador.isServidor()) {
			abrirModoServidor();
		}else {
			abrirModoCliente();
		}
		while(controlador.isConectado()) {
			String mensaje = entraMensajeDeControl();
			if(mensaje.equals("Mensaje recibido.")) {
				controlador.mensajeEntregado();
			}else if(mensaje.equals("Imagen recibida.")) {
				controlador.imagenEntregada();
			}
		}
	}
	
	public String entraMensajeDeControl() {
		try {
			String mensaje = br.readLine();
			return mensaje;
		} catch (IOException e) {
//			controlador.parar();
		}		
		return "";
	}
	
	public InetAddress getIPCliente() {
		return socket.getInetAddress();
	}
	
	public void crearFlujosDeControl() throws IOException {
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		br = new BufferedReader(isr);
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osr = new OutputStreamWriter(os);
		bw = new BufferedWriter(osr);
	}

	public void enviarMensajeDeControl(String mensaje) throws IOException {
		socket.isConnected();
		bw.write(mensaje);
		bw.newLine();
		bw.flush();
	}
}
