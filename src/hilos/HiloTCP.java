package hilos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
	private String host = null;

	
	public HiloTCP(Controlador controlador, int puerto) {
		this.controlador = controlador;
		this.puerto = puerto;
	}
	
	public HiloTCP(Controlador controlador, int puerto, String host) {
		this.controlador = controlador;
		this.puerto = puerto;
		this.host  = host;
	}
	
	public void run() {
		if(controlador.isServidor()) {
			abrirModoServidor();			
		}else {
			abrirModoCliente();
		}
		crearFlujosTexto();
		enviarMensaje("Soy " + controlador.getMiIP() + "\ny estoy conectado a " + controlador.getSuIP());
		while (true) {
			String mensaje = recibirMensaje();
			if(mensaje.equals("Imagen Entregada.")) {
				controlador.setImagenEntregada();
			}else if(mensaje.equals("Encriptando entrada")) {
				controlador.setEncriptandoEntrada(true);
				enviarMensaje("Recibido encriptando");
			}else if(mensaje.equals("Desencriptando entrada")) {
				controlador.setEncriptandoEntrada(false);
			}else if(mensaje.equals("Recibido encriptando")) {
				controlador.recibidoEncriptando();
			}else{
				controlador.addMensajeAPantalla("Yo: " + mensaje);
			}
		}
	}
	
	
	
	private void abrirModoServidor() {
		try {
			sk = new ServerSocket(puerto);
			socket = sk.accept();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private void abrirModoCliente() {
		try {
			socket = new Socket(host, puerto);
		} catch (IOException e) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {}
			abrirModoCliente();
			e.printStackTrace();
		}
	}
	
	public void crearFlujosTexto() {
		controlador.setSuIP(socket.getInetAddress());
		controlador.habilitarEnvioTexto();
		try {
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osr = new OutputStreamWriter(os);
			bw = new BufferedWriter(osr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void enviarMensaje(String mensaje) {
		socket.isConnected();
		try {
			bw.write(mensaje);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
		}
	}
	
	public String recibirMensaje() {
		try {
			String mensaje = br.readLine();
			return mensaje;
		} catch (IOException e) {
//			controlador.parar();
		}
		return "";
	}
}
