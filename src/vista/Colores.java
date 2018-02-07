package vista;

import java.awt.Color;

/**
 * Prepara los colores para representar los diferentes conjuntos de puntos seleccionados para el tratamiento de los datos
 * 
 * @author PABLO ARIENZA
 * @version 10.07.2017
 */
public class Colores {

	private Color[] colores;
	
	private int colorElegido;
	
	/**
	 * Constructor de la clase
	 */
	public Colores(int numeroColores){
		colores = new Color[numeroColores];
		for(int i = 0; i < numeroColores; i++) {
			colores[i] = new Color(i%256,(2*(i+i))%256,(2*(i+i+i))%256);
		}
		this.colorElegido = 0;
	}//fin del constructor
	
	/**
	 * Método para seleccionar el color del conjunto
	 * 
	 * @return el color para el conjunto
	 */
	public Color seleccionaColor(){
		colorElegido++;
		//reinicia la selección de color
		if(colorElegido == colores.length) colorElegido = 0;
		return colores[colorElegido];
	}//fin del método
}// fin Colores
