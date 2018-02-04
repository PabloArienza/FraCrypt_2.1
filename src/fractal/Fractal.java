package fractal;

import java.util.ArrayList;

/**
 * Genera las distintas listas de puntos necesarios para formar el fractal
 * según la función de escape seleccionada.
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
	 * Calcula a iteración en la que el punto escapa del límite establecido.
	 * 
	 * @param coordenadas
	 *            las coordenadas del punto a iterar
	 */
	public abstract void calculaElConjuntoDelPunto(int[] coordenadas);

	/**
	 * Calcula a qué conjunto pertence cada punto del espacio n-dimensional.
	 * 
	 * @param dimensiones
	 *            el número de dimensiones y su tamaño
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
		// El número de puntos total del espacio
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
	 * Añade un punto al conjunto al que pertence en función de su valor de
	 * escape.
	 * 
	 * @param punto
	 *            el punto a incluir en un conjunto
	 * @param iteracion
	 *            la iteración en la que el punto escapa a la función de escape
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
		// Si no existe un conjunto con el valor de escape se crea y se añade el punto
		if (!anotado) {
			ConjuntoDePuntos nuevo = new ConjuntoDePuntos(iteracion);
			nuevo.addPunto(punto);
			fractal.add(nuevo);
		}
	}// fin addPuntoASuConjunto

	/**
	 * Altera el orden de los conjuntos y los puntos del fractal en función del
	 * punto desde el que se inicia el algoritmo.
	 * 
	 * @param avanceConjuntos
	 *            el número de conjuntos que se pasan al final de la lista de
	 *            conjuntos
	 * @param avancePuntos
	 *            el número de puntos que se pasan al final del conjunto
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
	 * Traslada el factor de mutación al objeto correspondiente.
	 * 
	 * @param avance
	 *            el factor de mutación
	 */
	public void mutarElPunto(int avance) {
		fractal.get(ultimoLeido).mutarElPunto(avance);
	}// fin mutarElPunto
}// fin Fractal
