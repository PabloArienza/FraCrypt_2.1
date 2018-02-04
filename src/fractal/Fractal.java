package fractal;

import java.util.ArrayList;

/**
 * Genera las distintas listas de puntos necesarios para formar el fractal
 * seg�n la funci�n de escape seleccionada.
 * 
 * @author PABLO ARIENZA CARRERA
 * @version 24.08.2017
 */
public abstract class Fractal {

	protected ArrayList<ConjuntoDePuntos> fractal;
	protected int ultimoLeido, contadorDePuntos, iteraciones, limite, escala;
	protected int[] coordenadasCentro;

	/**
	 * Constructor de la clase.
	 * 
	 * @param dimensiones
	 *            el tama�o de las dimensiones del espacio
	 * @param coordenadasCentro
	 *            el centro relativo del espacio n-dimensional
	 * @param iteraciones
	 *            el n�mero m�ximo de veces que se iterar� cada punto
	 * @param limite
	 *            el valor m�ximo de la funci�n de escape
	 * @param escala
	 *            el modificador de la funci�n de escape
	 */
	public Fractal(int[] dimensiones, int[] coordenadasCentro, int iteraciones, int limite, int escala) {
		this.fractal = new ArrayList<ConjuntoDePuntos>();
		this.ultimoLeido = 0;
		this.contadorDePuntos = 0;
		this.coordenadasCentro = coordenadasCentro;
		this.iteraciones = iteraciones;
		this.limite = limite;
		this.escala = escala;
		calculaLosConjuntos(dimensiones);
	}// fin del constructor

	/**
	 * Calcula a iteraci�n en la que el punto escapa del l�mite establecido.
	 * 
	 * @param coordenadas
	 *            las coordenadas del punto a iterar
	 */
	public abstract void calculaElConjuntoDelPunto(int[] coordenadas);

	/**
	 * Calcula a qu� conjunto pertence cada punto del espacio n-dimensional.
	 * 
	 * @param dimensiones
	 *            el n�mero de dimensiones y su tama�o
	 */
	public void calculaLosConjuntos(int[] dimensiones) {

		// Conjunto de multiplicadores necesarios para generar iterativamente todos los
		// puntos del espacio n-dimensional
		double[] multiplicadores = new double[dimensiones.length];
		for (int i = 0; i < dimensiones.length; i++)
			multiplicadores[i] = 1;
		for (int i = 0; i < dimensiones.length; i++) {
			for (int j = i + 1; j < dimensiones.length; j++)
				multiplicadores[i] *= dimensiones[j];
		}
		// El n�mero de puntos total del espacio
		int numeroPuntos = 1;
		for (int d : dimensiones)
			numeroPuntos *= d;
		for (int i = 0; i < numeroPuntos; i++) {
			int[] coordenadas = new int[dimensiones.length];
			for (int j = 0; j < dimensiones.length; j++) {
				coordenadas[j] = (int) ((i / multiplicadores[j]) % dimensiones[j]);
			}
			calculaElConjuntoDelPunto(coordenadas);
		}
	}// fin calculaLosConjuntos

	/**
	 * A�ade un punto al conjunto al que pertence en funci�n de su valor de
	 * escape.
	 * 
	 * @param punto
	 *            el punto a incluir en un conjunto
	 * @param iteracion
	 *            la iteraci�n en la que el punto escapa a la funci�n de escape
	 */
	public void addPuntoASuConjunto(Punto punto, int iteracion) {
		boolean anotado = false;
		for (ConjuntoDePuntos c : fractal) {
			if (c.getiD() == iteracion) {
				c.addPunto(punto);
				anotado = true;
				break;
			}
		}
		// Si no existe un conjunto con el valor de escape se crea y se a�ade el punto
		if (!anotado) {
			ConjuntoDePuntos nuevo = new ConjuntoDePuntos(iteracion);
			nuevo.addPunto(punto);
			fractal.add(nuevo);
		}
	}// fin addPuntoASuConjunto

	/**
	 * Altera el orden de los conjuntos y los puntos del fractal en funci�n del
	 * punto desde el que se inicia el algoritmo.
	 * 
	 * @param avanceConjuntos
	 *            el n�mero de conjuntos que se pasan al final de la lista de
	 *            conjuntos
	 * @param avancePuntos
	 *            el n�mero de puntos que se pasan al final del conjunto
	 */
	public void setPuntoDeInicio(int avanceConjuntos, int avancePuntos) {
		for (int i = 0; i < avanceConjuntos; i++) {
			ConjuntoDePuntos primero = fractal.get(0);
			fractal.remove(0);
			fractal.add(primero);
			fractal.get(0).setPuntoDeInicio(avancePuntos);
		}
	}// fin setPuntoDeInicio

	/**
	 * Devuelve el punto con el que transforma el dato.
	 * 
	 * @return el punto con el que transforma el dato
	 */
	public Punto leePunto() {
		return fractal.get(ultimoLeido).getPunto();
	}// fin leePunto

	/**
	 * Avanza el contador de conjuntos y el contador de puntos en el conjunto
	 * seleccionado.
	 * 
	 * @param avance
	 *            la cantidad de conjuntos que se avanza
	 */
	public void pasaAlSiguienteConjunto(int avance) {
		ultimoLeido = (ultimoLeido + avance) % fractal.size();
		fractal.get(ultimoLeido).pasaAlSiguientePunto();
	}// fin pasaAlSiguienteConjunto

	/**
	 * Traslada el factor de mutaci�n al objeto correspondiente.
	 * 
	 * @param avance
	 *            el factor de mutaci�n
	 */
	public void mutarElPunto(int avance) {
		fractal.get(ultimoLeido).mutarElPunto(avance);
	}// fin mutarElPunto
}// fin Fractal
