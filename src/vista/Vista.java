package vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import controlador.Controlador;

public class Vista extends JFrame implements Acciones{
	
	Controlador controlador;
	
	public Vista(Controlador controlador, boolean esServidor) {
		
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
		
		this.controlador = controlador;
		
		if (esServidor) {
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
		
		lblTransmision = new JLabel("");
		lblTransmision.setOpaque(true);
		lblTransmision.setBackground(Color.GRAY);
		lblTransmision.setBounds(730, 0, 160, 120);
		getContentPane().add(lblTransmision);
		
		lblInterlocutor = new JLabel("");
		lblInterlocutor.setBounds(250, 120, 640, 480);
		lblInterlocutor.setOpaque(true);
		lblInterlocutor.setBackground(Color.GRAY);
		getContentPane().add(lblInterlocutor);
		
		btnCamara = new JButton("Encender c\u00E1mara");
		btnCamara.setBounds(410, 0, 160, 40);
		getContentPane().add(btnCamara);
		
		btnTransmitir = new JButton("Transmitir");
		btnTransmitir.setBounds(410, 40, 160, 40);
		getContentPane().add(btnTransmitir);
		
		btnEncriptar = new JButton("Encriptar");
		btnEncriptar.setBounds(410, 80, 160, 40);
		getContentPane().add(btnEncriptar);
		
		btnEnviar.setEnabled(false);
		btnTransmitir.setEnabled(false);
		btnEncriptar.setEnabled(false);
		
		
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
	
	private JTextArea taMensajes;
	private JTextField txtMensaje;
	private JButton btnEnviar;
	private JLabel lblMiCamara;
	private JLabel lblTransmision;
	private JLabel lblInterlocutor;
	private JButton btnCamara;
	private JButton btnTransmitir;
	private JButton btnEncriptar;
	
	public void setNombreVentana(String nombre) {
		setTitle(nombre);
	}
	
	public void addMensajeAPantalla(String mensaje) {
		taMensajes.append(mensaje + "\n");
	}

	public void pintaInterlocutor(BufferedImage img) {
		lblInterlocutor.setIcon(new ImageIcon(img));
	}

	public String getTexto() {
		return txtMensaje.getText();
	}

	public void habilitarEnviar() {
		btnEnviar.setEnabled(true);
	}
	
	public void deshabilitarEnviar() {
		btnEnviar.setEnabled(false);
	}

	public String enviaMensaje() {
		return txtMensaje.getText();
	}

	public void pintaTransmision(byte[] imagen) {
		// TODO Auto-generated method stub
		
	}

	public void pintaCamara(Image imagen) {
		lblMiCamara.setIcon(new ImageIcon(imagen.getScaledInstance(160, 120, Image.SCALE_FAST)));
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
}
