package fractal;

import fractal.*;

/**
 * Genera un fractal de Mandelbrot modificado.
 * 
 * @author PABLO ARIENZA CARRERA
 * @version 24.08.2017
 */
public class Mandelbrot2DModificado extends Fractal {

	/**
	 * Constructor de la clase.
	 * 
	 * @see Fractal.java
	 * 
	 * @param dimensiones
	 *            el tamaño de las dimensiones del espacio
	 * @param coordenadasCentro
	 *            el centro relativo del espacio n-dimensional
	 * @param iteraciones
	 *            el número máximo de veces que se iterará cada punto
	 * @param limite
	 *            el valor máximo de la función de escape
	 * @param escala
	 *            el modificador de la función de escape
	 */
	public Mandelbrot2DModificado(int[] dimensiones, int[] coordenadasCentro, int iteraciones, int limite, int escala) {
		super(dimensiones, coordenadasCentro, iteraciones, limite, escala);
	}// fin del constructor

	@Override
	public void calculaElConjuntoDelPunto(int[] coordenadas) {
		contadorDePuntos++;
		float xC = ((float) coordenadas[0] - (float) coordenadasCentro[0]) / (float) escala;
		float yC = ((float) coordenadas[1] - (float) coordenadasCentro[1]) / (float) escala;
		float zX = 0;
		float zY = 0;
		// para el punto c = a + b*i
		// f(z) = z^2 + c
		for (int i = 0; i < iteraciones; i++) {
			float nX = zX * zX - zY * zY + xC;
			float nY = 2 * zX * zY + yC;
			zX = nX;
			zY = nY;
			// La funciÃ³n de escape es x^2 - y^2 < limite
			if (zX * zX - zY * zY > (float) limite) {
				Punto punto = new PuntoDeMutacionPorAvance(coordenadas);
				addPuntoASuConjunto(punto, i);
				break;
			}
		}
	}// fin calculaElConjuntoDelPunto
}// fin Mandelbrot2DModificado
