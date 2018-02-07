package vista;

import java.util.ArrayList;

import javax.swing.JFrame;

import fractal.ConjuntoDePuntos;

/**
 * Contiene el panel en el que se pintan los conjuntos de puntos seleccionados para el tratamiento de datos
 * 
 * @author PABLO ARIENZA
 * @version 10.07.2017
 */
public class Ventana {
	
	private JFrame ventana;
	
	/**
	 * Constructor de la clase
	 * 
	 * @param listaDeConjuntos la lista de listas de puntos 
	 * @param ancho el ancho de la ventana
	 * @param alto el alto de la ventana
	 */
	public Ventana(ArrayList<ConjuntoDePuntos> listaDeConjuntos, int ancho, int alto, int xVista, int yVista, String enviarORecibir){
		ventana = new JFrame("Fractal generado para " + enviarORecibir);
		ventana.setSize(ancho,alto);		
		ventana.add(new PanelPintaFractal(listaDeConjuntos, ancho, alto));
		ventana.setVisible(true);
		ventana.setLocation(xVista, yVista);
		ventana.setResizable(false);
	}//fin del constructor
}//fin de la clase
