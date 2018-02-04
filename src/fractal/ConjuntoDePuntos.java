package fractal;

import java.util.ArrayList;

/**
 * Genera la lista de punto seleccionados del fractal que escapan en una
 * iteración concreta.
 * 
 * @author PABLO ARIENZA CARRERA
 * @version 19.08.2017
 */
public class ConjuntoDePuntos {
	private int iD, ultimoLeido;
	private ArrayList<Punto> listaDePuntos;

	/**
	 * Constructor de la clase.
	 * 
	 * @param iD
	 *            identidad del conjunto de puntos que coincide con la iteración en
	 *            la que escapa
	 */
	public ConjuntoDePuntos(int iD) {
		this.iD = iD;
		this.ultimoLeido = -1;
		this.listaDePuntos = new ArrayList<Punto>();
	}// fin del constructor

	/**
	 * Añade un punto a la lista.
	 * 
	 * @param punto
	 *            el punto que se añade a la lista
	 */
	public void addPunto(Punto punto) {
		listaDePuntos.add(punto);
	}// fin addPunto

	/**
	 * Altera el orden de los puntos del fractal en función del punto desde el que
	 * se inicia el algoritmo.
	 * 
	 * @param avance
	 *            el número de puntos que se pasan al final para modificar el punto
	 *            de inicio del algoritmo
	 */
	public void setPuntoDeInicio(int avance) {
		for (int i = 0; i < avance; i++) {
			Punto primero = listaDePuntos.get(0);
			listaDePuntos.remove(0);
			listaDePuntos.add(primero);
		}
	}// fin setPuntoDeInicio

	/**
	 * Avanza el contador de posición. Si estaba en el último reinicia la lista.
	 */
	public void pasaAlSiguientePunto() {
		ultimoLeido++;
		if (ultimoLeido >= listaDePuntos.size()) {
			ultimoLeido = 0;
		}
	}// fin pasaAlSiguientePunto

	/**
	 * Llama a la función de mutación del punto
	 * 
	 * @param avance
	 *            el valor del byte que se ha modificado en el tratamiento del
	 *            archivo
	 */
	public void mutarElPunto(int avance) {
		listaDePuntos.get(ultimoLeido).mutarElPunto(avance);
	}// fin mutarElPunto

	/**
	 * Devuelve la identidad del conjunto de puntos.
	 * 
	 * @return identidad del conjunto de puntos
	 */
	public int getiD() {
		return iD;
	}// fin getID

	/**
	 * Devuelve el punto con el que operar.
	 * 
	 * @return el punto con el que operar
	 */
	public Punto getPunto() {
		return listaDePuntos.get(ultimoLeido);
	}// fin getPunto

	/**
	 * Devuelve el número de puntos en el conjunto
	 * 
	 * @return el número de puntos en el conjunto
	 */
	public int getTamLista() {
		return listaDePuntos.size();
	}// fin getTamLista

	// ******************************************************************************
	// Presentación en pantalla. No forma parte del algoritmo.

	/**
	 * Para presentación en pantalla. Imprime todos los puntos del conjunto
	 * tabulados para una lectura más sencilla.
	 */
	public void imprimeListaDePuntos() {
		int celdaMaxima = 0;
		int[] coordenadas = listaDePuntos.get(0).getCoordenadas();
		// Busca la coordenada mÃ¡s grande para formatear las celdas de la tabla
		for (Punto p : listaDePuntos) {
			coordenadas = p.getCoordenadas();
			for (int c : coordenadas) {
				if (c > celdaMaxima)
					celdaMaxima = c;
			}
		}
		// Calcula cuantos puntos caben en cada linea de pantalla
		int puntosPorLinea = 100 / (((Integer.toString(celdaMaxima).length() + 1) * coordenadas.length) + 3);
		System.out.println("\n*** Conjunto de escape nÃºmero " + iD + " ***\n");
		String linea = "";
		if (puntosPorLinea > 1) {
			int enLinea = 0;
			for (Punto p : listaDePuntos) {
				linea += p.formateaPuntoParaImprimir(celdaMaxima);
				enLinea++;
				if (listaDePuntos.indexOf(p) != listaDePuntos.size() - 1) {
					linea += ",";
					if (enLinea == puntosPorLinea) {
						System.out.println(linea);
						enLinea = 0;
						linea = "";
					}
				} else {
					System.out.println(linea);
				}

			}
		} else {
			for (Punto p : listaDePuntos) {
				linea = p.formateaPuntoParaImprimir(celdaMaxima);
				if (listaDePuntos.indexOf(p) != listaDePuntos.size() - 1)
					linea += ",";
				System.out.println(linea);
			}
		}
	}// fin imprimeListaDePuntos

	// ******************************************************************************
}// fin ConjuntoDePuntos
