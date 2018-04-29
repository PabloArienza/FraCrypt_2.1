package client;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class VentanaClient {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaClient window = new VentanaClient();
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
	public VentanaClient() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Soy el cliente");
		frame.setResizable(false);
		frame.setBounds(645, 50, 644, 760);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);

		lblTransmision = new JLabel();
		lblTransmision.setBounds(0, 50, 640, 480);
		lblTransmision.setOpaque(true);
		lblTransmision.setBackground(Color.WHITE);
		frame.getContentPane().add(lblTransmision);

		pnlFractal = new JPanel();
		pnlFractal.setBounds(440, 530, 200, 200);
		frame.getContentPane().add(pnlFractal);

		taLog = new JTextArea();
		taLog.setEnabled(false);
		taLog.setEditable(false);
		JScrollPane scroll = new JScrollPane(taLog);
		scroll.setBounds(0, 530, 200, 200);
		frame.getContentPane().add(scroll);

		HiloTCP.start();
	}

	JLabel lblTransmision;
	JPanel pnlFractal;
	JTextArea taLog;

	Socket socket;

	Thread HiloTCP = new Thread() {

		@Override
		public void run() {
			try {
				taLog.append("Conectado a servidor...\n");
				socket = new Socket("localhost", 9999);
				taLog.append("Conectado.\n");
				fractal = new ArrayList<ConjuntoDePuntos>();
				calculaLosConjuntos();
				pintaFractal();
				InputStream in = socket.getInputStream();
				DataInputStream dis = new DataInputStream(in);

				while (true) {
					int len = dis.readInt();
					byte[] data = new byte[len];
					dis.readFully(data);
					data = desencriptar(data);
					Image im = ImageIO.read(new ByteArrayInputStream(data));
					lblTransmision.setIcon(new ImageIcon(im.getScaledInstance(640, 480, Image.SCALE_FAST)));
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			// La función de escape es x^2 + y^2 < limite
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
	
	public byte[] desencriptar(byte[] imagenEnBytes) {
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
			p.setX((p.getCoordenadas()[0] + Math.abs(imagenSalida[i])) % dimensiones[0]);
			p.setY((p.getCoordenadas()[1] - Math.abs(imagenSalida[i])) % dimensiones[1]);
			g.setColor(color);
			g.fillRect(p.getCoordenadas()[0], p.getCoordenadas()[1], 1, 1);
//			g.dispose();
			conjuntoActual = (conjuntoActual + Math.abs(imagenSalida[i])) % fractal.size();
			puntoActual = (puntoActual + Math.abs(imagenSalida[i])) % fractal.get(conjuntoActual).getSize();
			fractal.get(conjuntoActual).getPunto(puntoActual);
		}
		
		return imagenSalida;		
	}
}
