package fractal;

import controlador.Controlador;

public class TransformadorVideo {
Controlador controlador;
	
	public TransformadorVideo(Controlador controlador) {
		this.controlador = controlador;
	}

	public byte[] transformaImagen(byte[] imagen) {
		byte[] imagenSalida = null;
		if(controlador.isEncriptando()) {
			
		}else {
			imagenSalida = imagen;
		}
		return imagenSalida;
	}
}
