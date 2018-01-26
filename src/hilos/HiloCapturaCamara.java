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

public class HiloCapturaCamara extends Thread {

	Controlador controlador;
	Webcam camara;
	Image imagen;

	public HiloCapturaCamara(Controlador controlador) {
		this.controlador = controlador;
		camara = Webcam.getDefault();
		camara.setViewSize(new Dimension(640, 480));
	}

	public void run() {
		camara.open();
		while (controlador.isCamaraEncendida()) {
			imagen = camara.getImage();
			controlador.pintaCamara(imagen);
		}
		camara.close();
	}

	public byte[] getImagen() throws IOException {
		BufferedImage bufImg = new BufferedImage(imagen.getWidth(null), imagen.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D bGr = bufImg.createGraphics();
		bGr.drawImage(imagen, 0, 0, null);
		bGr.dispose();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufImg, "jpg", baos);
		return baos.toByteArray();
	}
}
