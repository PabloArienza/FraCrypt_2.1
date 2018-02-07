package vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import controlador.Controlador;
import fractal.ConjuntoDePuntos;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Vista extends JFrame implements Acciones {
	
	private Controlador controlador;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Vista(Controlador controlador) {

		this.controlador = controlador;
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		setBounds(100, 100, 896, 629);
		setResizable(false);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		if (controlador.isServidor()) {
			this.setTitle("Servidor");
		} else {
			this.setTitle("Cliente");
		}
		
		taMensajes = new JTextArea();
		taMensajes.setBackground(Color.LIGHT_GRAY);
		JScrollPane scroll = new JScrollPane(taMensajes);
		scroll.setBounds(0, 0, 250, 520);
		getContentPane().add(scroll);
		
		txtMensaje = new JTextField();
		txtMensaje.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtMensaje.setBounds(0, 520, 250, 40);
		getContentPane().add(txtMensaje);
		txtMensaje.setColumns(10);
		
		btnEnviar = new JButton("Enviar");
		btnEnviar.setBounds(0, 560, 250, 40);
		getContentPane().add(btnEnviar);
		
		lblMiCamara = new JLabel("");
		lblMiCamara.setBounds(250, 0, 160, 120); 
		lblMiCamara.setOpaque(true);
		lblMiCamara.setBackground(Color.GRAY);
		getContentPane().add(lblMiCamara);
		
		btnCamara = new JButton("Encender c\u00E1mara");
		btnCamara.setBounds(410, 0, 160, 40);
		getContentPane().add(btnCamara);
		
		lblTransmision = new JLabel("");
		lblTransmision.setOpaque(true);
		lblTransmision.setBackground(Color.GRAY);
		lblTransmision.setBounds(730, 0, 160, 120);
		getContentPane().add(lblTransmision);
		
		btnTransmitir = new JButton("Transmitir");
		btnTransmitir.setBounds(410, 40, 160, 40);
		getContentPane().add(btnTransmitir);
		
		lblInterlocutor = new JLabel("");
		lblInterlocutor.setBounds(250, 120, 640, 480);
		lblInterlocutor.setOpaque(true);
		lblInterlocutor.setBackground(Color.GRAY);
		getContentPane().add(lblInterlocutor);
		
		btnEncriptar = new JButton("Encriptar");
		btnEncriptar.setBounds(410, 80, 160, 40);
		getContentPane().add(btnEncriptar);
		
		btnEnviar.setEnabled(false);
		btnTransmitir.setEnabled(false);
		btnEncriptar.setEnabled(false);
	}
	
	private JTextArea taMensajes;
	private JTextField txtMensaje;
	private JButton btnEnviar;
	private JLabel lblMiCamara;
	private JButton btnCamara;
	private JLabel lblTransmision;
	private JButton btnTransmitir;
	private JLabel lblInterlocutor;
	private JButton btnEncriptar;

	public void addMensajeRecibidoTCP(String mensaje) {
		taMensajes.append(mensaje + "\n");
		btnEnviar.setEnabled(true);
	}

	public void inicializar() {
		btnEnviar.setActionCommand(ENVIAR);
		btnEnviar.addActionListener(controlador);
		btnCamara.setActionCommand(CAMARA);
		btnCamara.addActionListener(controlador);
		btnTransmitir.setActionCommand(TRANSMITIR);
		btnTransmitir.addActionListener(controlador);
		btnEncriptar.setActionCommand(ENCRIPTAR);
		btnEncriptar.addActionListener(controlador);
	}

	public String getTexto() {
		btnEnviar.setEnabled(false);
		String salida = txtMensaje.getText();
		txtMensaje.setText("");
		return salida;
	}
	
	public void pintaCamara(Image imagen) {
		lblMiCamara.setIcon(new ImageIcon(imagen.getScaledInstance(160, 120, Image.SCALE_FAST)));
	}

	public void pintaTransmision(byte[] imagen) throws IOException {
		Image im = ImageIO.read(new ByteArrayInputStream(imagen));
		lblTransmision.setIcon(new ImageIcon(im.getScaledInstance(160, 120, Image.SCALE_FAST)));
	}

	public void pintaInterlocutor(byte[] imagen) throws IOException {
		Image im = ImageIO.read(new ByteArrayInputStream(imagen));
		lblInterlocutor.setIcon(new ImageIcon(im.getScaledInstance(640, 480, Image.SCALE_FAST)));
	}
	
	public void ajustarBotones() {
		if (controlador.isCamaraEncendida()) {
			btnTransmitir.setEnabled(true);
			btnCamara.setText("Parar vídeo");
		} else {
			btnTransmitir.setEnabled(false);
			btnCamara.setText("Encender c\u00E1mara");
		}
		if (controlador.isTransmitiendo()) {
			btnEncriptar.setEnabled(true);
			btnTransmitir.setText("Terminar transmisión");
		} else {
			btnEncriptar.setEnabled(false);
			btnTransmitir.setText("Transmitir");
		}
		if (controlador.isEncriptando()) {
			btnEncriptar.setText("Dejar de encriptar");
		} else {
			btnEncriptar.setText("Encriptar");
		}
	}

	public void habilitarEnviarTexto() {
		btnEnviar.setEnabled(true);
	}

	public void pintaPanelFractal(ArrayList<ConjuntoDePuntos> fractal, int ancho, int alto) {
		JPanel panel = new PanelPintaFractal(fractal, ancho, alto);
		panel.setBounds(570,0,160, 120);
		getContentPane().add(panel);
	}
}
