package fractal;

import java.security.MessageDigest;

import controlador.Controlador;

public class TransformadorVideo {

	Controlador controlador;
	boolean encriptando;
	Fractal fractal;
	int[] dimensiones;
	
	public TransformadorVideo(Controlador controlador, String password, boolean encriptando) {
		this.controlador = controlador;
		String sha = creaSha256(password);
		int[][] parametros = setParametros(sha);
		dimensiones = parametros[0];
		this.encriptando = encriptando;
		fractal = new Mandelbrot2DModificado(parametros[0], parametros[1], parametros[3][0], parametros[4][0],
				parametros[5][0]);
		fractal.setPuntoDeInicio(parametros[2][0], parametros[2][1]);
		
		fractal.imprimeDatos();
	}
	
	protected String creaSha256(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}// fin creaSha256
	
	protected int[][] setParametros(String sha) {
		int[][] shaCortado = new int[6][2];
		int multiplicador = Character.getNumericValue(sha.charAt(63)) + 100;
		// ancho del plano
		for(int i = 0; i < 7; i++) {
			shaCortado[0][0] += Character.getNumericValue(sha.charAt(i));
		}
		// alto del plano
		for(int i = 7; i < 14; i++) {
			shaCortado[0][1] += Character.getNumericValue(sha.charAt(i));
		}
		//coordenada X del centro
		for(int i = 14; i < 21; i++) {
			shaCortado[1][0] += Character.getNumericValue(sha.charAt(i));
		}		
		// coordenanda Y del centro
		for(int i = 21; i < 28; i++) {
			shaCortado[1][1] += Character.getNumericValue(sha.charAt(i));
		}		
		// avance de conjuntos para buscar el punto de inicio
		for(int i = 28; i < 35; i++) {
			shaCortado[2][0] += Character.getNumericValue(sha.charAt(i));
		}		
		// avance de puntos deltro del conjunto para buscar el punto de inicio
		for(int i = 35; i < 42; i++) {
			shaCortado[2][1] += Character.getNumericValue(sha.charAt(i));
		}		
		// iteraciones
		for(int i = 42; i < 49; i++) {
			shaCortado[3][0] += Character.getNumericValue(sha.charAt(i));
		}
		//límite
		for(int i = 49; i < 56; i++) {
			shaCortado[4][0] += Character.getNumericValue(sha.charAt(i));
		}
		// escala
		for(int i = 56; i < 63; i++) {
			shaCortado[1][0] += Character.getNumericValue(sha.charAt(i));
		}		
		//tamaño mínimo del plano 300x300
		while(shaCortado[0][0] < 300 || shaCortado[0][1] < 300) {
			shaCortado[0][0] += multiplicador;
			shaCortado[0][1] += multiplicador;
		}		
 		// restricción del centro en el plano
		while(shaCortado[1][0] > shaCortado[0][0]) {
			shaCortado[1][0] -= shaCortado[0][0];
		}
		while(shaCortado[1][1] > shaCortado[0][1]) {
			shaCortado[1][1] -=shaCortado[0][1];
		}
		// un mínimo de 100 iteraciones de los puntos
		while(shaCortado[3][0] < 100) {
			shaCortado[3][0] += multiplicador;
		}
		// límite mínimo de 100
		while(shaCortado[4][0] < 100) {
			shaCortado[4][0] += multiplicador;
		}
		// escala mínima de 400
		while(shaCortado[5][0] < 400) {
			shaCortado[5][0] += multiplicador;
		}

		System.out.println("Multiplicador: " + multiplicador +
						   "\nDimensiones: " + shaCortado[0][0] + "x" + shaCortado[0][1] +
						   "\nCentro:      " + shaCortado[1][0] + "x" + shaCortado[1][1] +
						   "\nInicio:      " + shaCortado[2][0] + "x" + shaCortado[2][1] +
						   "\nIteraciones: " + shaCortado[3][0] +
						   "\nLímite       " + shaCortado[4][0] +
						   "\nEscala:      " + shaCortado[5][0]);
		return shaCortado;
	}// fin setParametros	

	public byte[] transformaImagen(byte[] imagen, boolean encriptando) {
		byte[] imagenSalida = null;
		if(encriptando) {
			imagenSalida = new byte[imagen.length];
			Punto punto = fractal.leePunto();
			for (int i = 0; i < imagen.length; i++) {			
					int operadorXOR = 0;
					for (int p = 0; p < punto.getCoordenadas().length; p++) {
						operadorXOR += punto.getCoordenadas()[p];
					}
					imagenSalida[i] = (byte) (imagen[i] ^ (operadorXOR) % 256);
					int avance = 0;
					if (this.encriptando) {
						avance = Math.abs(imagen[i]);
					} else {
						avance = Math.abs(imagenSalida[i]);
					}
					fractal.mutarElPunto(avance);
					fractal.pasaAlSiguienteConjunto(avance);
					punto = fractal.leePunto();
			}
		}else {
			imagenSalida = imagen;
		}
		return imagenSalida;
	}
	
	public Fractal getFractal() {
		return fractal;
	}
	
	public int[] getDimensiones() {
		return dimensiones;
	}
}
