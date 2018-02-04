package fractal;

/**
 * Construye un punto
 * 
 * @Author PABLO ARIENZA CARRERA
 * @version 11.12.2017
 */
public abstract class Punto {

	protected int[] coordenadas;

	/**
	 * Constructor de la clase
	 * 
	 * @param coordenadas
	 *            el arreglo de enteros que forman las coordenadas del punto.
	 */
	public Punto(int[] coordenadas) {
		this.coordenadas = coordenadas;
	}// fin constructor

	/**
	 * Modifica las coordenadas del punto en función del byte que haya modificado.
	 * Suma y resta 1 alternativamente a cada coordenada. Si el byte es mayor que
	 * 127 empieza restando. Si no empieza sumando.
	 * 
	 * @param avance
	 *            el valor del byte que se ha modificado en el tratamiento del
	 *            fichero
	 */
	public abstract void mutarElPunto(int avance);

	/**
	 * Devuelve las coordenadas del punto.
	 * 
	 * @return las coordenadas del punto
	 */
	public int[] getCoordenadas() {
		return coordenadas;
	}// fin getCoordenadas

	// ******************************************************************************
	// Presentación en pantalla. No forma parte del algoritmo.
	/**
	 * Para presentación en pantalla. Prepara el punto entre corchetes, con comas
	 * entre los valores y con el espacio máximo necesario en el conjunto de puntos
	 * para cada celda.
	 * 
	 * @param celdaMaxima
	 *            el tamaño del número más grande de todo el conjunto de puntos
	 * @return las coordendas del punto formateadas para presentarlas en pantalla
	 */
	public String formateaPuntoParaImprimir(int celdaMaxima) {
		String puntoParaImprimir = "[";
		for (int i = 0; i < coordenadas.length; i++) {
			for (int j = 0; j < celdaMaxima - Integer.toString(coordenadas[i]).length(); j++)
				puntoParaImprimir += " ";
			puntoParaImprimir += coordenadas[i];
			if (i != coordenadas.length - 1)
				puntoParaImprimir += ",";
		}
		puntoParaImprimir = "]";
		return puntoParaImprimir;
	}// fin formateaPuntoParaImprimir
	
	// ******************************************************************************
}//fin Punto
