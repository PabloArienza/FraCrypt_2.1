package hilos;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;

import controlador.Controlador;

/**
 * Captura imágenes de la webcam
 * 
 * @author Pablo Arienza Carrera
 * @version 26.1.2018
 */
public class HiloCapturaCamara extends Thread {

	Controlador controlador;
	Webcam camara;
	Image imagen;

	/**
	 * constructor de la clase.
	 * 
	 * @param controlador
	 *            el controlador al que se asocia
	 */
	public HiloCapturaCamara(Controlador controlador) {
		this.controlador = controlador;
		camara = Webcam.getDefault();
		camara.setViewSize(new Dimension(640, 480));
	}// fin del constructor

	public void run() {
		camara.open();
		while (controlador.isCamaraEncendida()) {
			imagen = camara.getImage();
			controlador.pintaCamara(imagen);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		camara.close();
	}// fin run

	/**
	 * Transforma una imagen capturada de la camara en array de bytes.
	 * 
	 * @return la imagen convertida en array de bytes
	 * 
	 * @throws IOException
	 */
	public byte[] getImagen() throws IOException {
		BufferedImage bufImg = new BufferedImage(imagen.getWidth(null), imagen.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D bGr = bufImg.createGraphics();
		bGr.drawImage(imagen, 0, 0, null);
		bGr.dispose();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufImg, "jpg", baos);
		return baos.toByteArray();
	}// fin getImagen
}// fin HiloCapturaCamara
