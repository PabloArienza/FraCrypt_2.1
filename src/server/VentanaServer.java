package server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.github.sarxos.webcam.Webcam;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class VentanaServer {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaServer window = new VentanaServer();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VentanaServer() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Soy el servidor");
		frame.setBounds(50, 50, 644, 760);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);

		btnTransmitir = new JButton("Transmitir");
		btnTransmitir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				HiloTransmision.start();

			}
		});
		btnTransmitir.setBounds(0, 0, 640, 50);
		btnTransmitir.setEnabled(false);
		frame.getContentPane().add(btnTransmitir);

		lblTransmision = new JLabel();
		lblTransmision.setBounds(0, 50, 640, 480);
		lblTransmision.setOpaque(true);
		lblTransmision.setBackground(Color.WHITE);
		frame.getContentPane().add(lblTransmision);

		taLog = new JTextArea();
		taLog.setEditable(false);
		taLog.setEnabled(false);
		JScrollPane scroll = new JScrollPane(taLog);
		scroll.setBounds(0, 530, 200, 200);
		frame.getContentPane().add(scroll);

		pnlFractal = new JPanel();
		pnlFractal.setBackground(Color.WHITE);
		pnlFractal.setBounds(440, 530, 200, 200);
		frame.getContentPane().add(pnlFractal);

		lblCamara = new JLabel("");
		lblCamara.setOpaque(true);
		lblCamara.setBackground(Color.WHITE);
		lblCamara.setBounds(200, 530, 240, 200);
		frame.getContentPane().add(lblCamara);

		
		camara = Webcam.getDefault();
		camara.setViewSize(new Dimension(640, 480));
		HiloCamara.start();

	}

	JLabel lblTransmision;
	JButton btnTransmitir;
	JTextArea taLog;
	JPanel pnlFractal;
	JLabel lblCamara;

	Webcam camara;
	Image fotograma;

	ServerSocket sk;
	Socket socket;

	Thread HiloCamara = new Thread() {

		@Override
		public void run() {
			camara.open();
			HiloTCP.start();

			while (true) {
				lblCamara.setIcon(new ImageIcon(camara.getImage().getScaledInstance(240, 200, Image.SCALE_FAST)));
			}
		}
	};

	Thread HiloTCP = new Thread() {

		@Override
		public void run() {
			try {
				taLog.append("Iniciando conexión.\n");
				sk = new ServerSocket(9999);
				taLog.append("Esperando cliente...\n");
				socket = sk.accept();
				taLog.append("Cliente conectado.\n");
				fractal = new ArrayList<ConjuntoDePuntos>();
				calculaLosConjuntos();
				pintaFractal();
				btnTransmitir.setEnabled(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Thread HiloTransmision = new Thread() {
		@Override
		public void run() {
			OutputStream out;
			try {
				out = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(out);
				while (true) {
					fotograma = camara.getImage();

					BufferedImage bufImg = new BufferedImage(fotograma.getWidth(null), fotograma.getHeight(null),
							BufferedImage.TYPE_INT_ARGB);
					Graphics2D bGr = bufImg.createGraphics();
					bGr.drawImage(fotograma, 0, 0, null);
					bGr.dispose();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(bufImg, "jpg", baos);
					byte[] imagenEnBytes = baos.toByteArray();
					imagenEnBytes = encriptar(imagenEnBytes);
					lblTransmision.setIcon(new ImageIcon(fotograma.getScaledInstance(640, 480, Image.SCALE_FAST)));
					dos.writeInt(imagenEnBytes.length);
					dos.write(imagenEnBytes, 0, imagenEnBytes.length);
				}
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
	};
	
	
	ArrayList<ConjuntoDePuntos> fractal;
	private int iteraciones = 200, limite = 100, escala = 90;
	private int[] dimensiones = {200, 200}, coordenadasCentro = {140, 100};
	private int conjuntoActual = 0, puntoActual = 0;
	
	public void calculaLosConjuntos() {
		for(int i = 0; i < dimensiones[0]; i++) {
			for(int j = 0; j < dimensiones[1]; j++) {
				calculaElConjuntoDelPunto(i, j);
			}
		}
		taLog.append("Fractal generado.\n");
		
	}
	
	public void calculaElConjuntoDelPunto(int x, int y) {
		float xC = ((float) x - (float) coordenadasCentro[0]) / (float) escala;
		float yC = ((float) coordenadasCentro[1] - (float) y) / (float) escala;
		float zX = 0;
		float zY = 0;
		// para el punto c = a + b*i
		// f(z) = z^2 + c
		for (int i = 0; i < iteraciones; i++) {
			float nX = zX * zX - zY * zY + xC;
			float nY = 2 * zX * zY + yC;
			zX = nX;
			zY = nY;
			// La funciÃ³n de escape es x^2 + y^2 < limite
			if (zX * zX + zY * zY > (float) limite) {
				Punto punto = new Punto(x, y);
				addPuntoASuConjunto(punto, i);
				break;
			}
		}
	}
	
	public void addPuntoASuConjunto(Punto punto, int iteracion) {
		boolean anotado = false;
		for (ConjuntoDePuntos c : fractal) {
			if (c.getID() == iteracion) {
				c.addPunto(punto);
				anotado = true;
				break;
			}
		}
		if (!anotado) {
			ConjuntoDePuntos nuevo = new ConjuntoDePuntos(iteracion);
			nuevo.addPunto(punto);
			fractal.add(nuevo);
		}
	}
	
	public void pintaFractal() {
		Graphics g = pnlFractal.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 200, 200);
		for(ConjuntoDePuntos c : fractal) {
			g.setColor(c.getColor());
			for(int i = 0; i < c.getSize(); i++) {
				g.fillRect(c.getPunto(i).getCoordenadas()[0], c.getPunto(i).getCoordenadas()[1], 1, 1);
			}
		}
		g.dispose();
	}
	
	public byte[] encriptar(byte[] imagenEnBytes) {
		byte[] imagenSalida = null;
		imagenSalida = new byte[imagenEnBytes.length];
		Punto p = fractal.get(conjuntoActual).getPunto(puntoActual);
		Graphics g = pnlFractal.getGraphics();
		for (int i = 0; i < imagenEnBytes.length; i++) {
			Color color = fractal.get(conjuntoActual).getColor();			
			g.setColor(Color.WHITE);
			g.fillRect(p.getCoordenadas()[0], p.getCoordenadas()[1], 1, 1);
			int operadorXOR = p.getCoordenadas()[0] + p.getCoordenadas()[1];
			imagenSalida[i] = (byte) (imagenEnBytes[i] ^ ((operadorXOR) % 256));
			p.setX((p.getCoordenadas()[0] +  Math.abs(imagenEnBytes[i])) % dimensiones[0]);
			p.setY((p.getCoordenadas()[1] -  Math.abs(imagenEnBytes[i])) % dimensiones[1]);
			g.setColor(color);
			g.fillRect(p.getCoordenadas()[0], p.getCoordenadas()[1], 1, 1);
			
			conjuntoActual = (conjuntoActual + Math.abs(imagenEnBytes[i])) % fractal.size();
			puntoActual = (puntoActual + Math.abs(imagenEnBytes[i])) % fractal.get(conjuntoActual).getSize();
			fractal.get(conjuntoActual).getPunto(puntoActual);
		}
		g.dispose();
		return imagenSalida;		
	}
}
